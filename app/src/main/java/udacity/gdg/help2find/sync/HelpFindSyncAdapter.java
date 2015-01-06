package udacity.gdg.help2find.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import udacity.gdg.help2find.R;
import udacity.gdg.help2find.database.HelpFindContract;
import udacity.gdg.help2find.entities.Announcement;
import udacity.gdg.help2find.entities.Image;
import udacity.gdg.help2find.utils.JsonUtils;

/**
 * Created by nnet on 05.01.15.
 */
public class HelpFindSyncAdapter extends AbstractThreadedSyncAdapter {
    private static final String TAG = HelpFindSyncAdapter.class.getSimpleName();
    public final String LOG_TAG = HelpFindSyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final String ALL_ANNOUNCEMENTS_URL = "http://helpme2findit.herokuapp.com/api/announcements.json";

    public HelpFindSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String jsonStr = null;
        try {
            Uri builtUri = Uri.parse(ALL_ANNOUNCEMENTS_URL).buildUpon()
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return;
            }
            jsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            getDataFromJson(jsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return;
    }

    private void getDataFromJson(String jsonStr) throws JSONException {
        List<Announcement> announcements = new ArrayList<Announcement>();
        JSONArray response = new JSONArray(jsonStr);
        for (int i = 0; i< response.length(); i++) {
            try {
                JsonNode actualObj = JsonUtils.defaultMapper().readTree(String.valueOf(response.get(i)));
                announcements.add(JsonUtils.parseJson(actualObj, Announcement.class));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Vector<ContentValues> announcementVector = new Vector<ContentValues>(announcements.size());
        Vector<ContentValues> imageContentValues = new Vector<ContentValues>();

        for(Announcement announcement : announcements) {
            ContentValues values = new ContentValues();
            values.put(HelpFindContract.AnnouncementEntry.ANNOUNCEMENT_ID, announcement.getId());

            values.put(HelpFindContract.AnnouncementEntry.ANNOUNCEMENT_PREVIEW_IMAGE, announcement.getPreviewImageUrl());
            values.put(HelpFindContract.AnnouncementEntry.ANNOUNCEMENT_CATEGORY, announcement.getCategory());
            values.put(HelpFindContract.AnnouncementEntry.ANNOUNCEMENT_IS_LOST, announcement.isLost());
            values.put(HelpFindContract.AnnouncementEntry.ANNOUNCEMENT_TITLE, announcement.getTitle());
            values.put(HelpFindContract.AnnouncementEntry.ANNOUNCEMENT_DESCRIPTION, announcement.getDescription());

            values.put(HelpFindContract.AnnouncementEntry.ANNOUNCEMENT_ADDRESS, announcement.getAddress());
            values.put(HelpFindContract.AnnouncementEntry.ANNOUNCEMENT_LATITUDE, announcement.getLatitude());
            values.put(HelpFindContract.AnnouncementEntry.ANNOUNCEMENT_LONGITUDE, announcement.getLatitude());

            values.put(HelpFindContract.AnnouncementEntry.ANNOUNCEMENT_CREATED_AT, HelpFindContract.getDbDateString(new Date(announcement.getCreatedAt() * 1000L)));
            values.put(HelpFindContract.AnnouncementEntry.ANNOUNCEMENT_UPDATED_AT, HelpFindContract.getDbDateString(new Date(announcement.getUpdatedAt() * 1000L)));
            values.put(HelpFindContract.AnnouncementEntry.ANNOUNCEMENT_DATE, HelpFindContract.getDbDateString(new Date(announcement.getDate() * 1000L)));

            Cursor idCursor = getContext().getContentResolver().query(
                    HelpFindContract.AnnouncementEntry.CONTENT_URI,
                    new String[]{HelpFindContract.AnnouncementEntry._ID},
                    HelpFindContract.AnnouncementEntry._ID + " = ?",
                    new String[]{String.valueOf(announcement.getId())},
                    null);

            if (idCursor.moveToFirst()) {
                int idIndex = idCursor.getColumnIndex(HelpFindContract.AnnouncementEntry._ID);
                long announcementId = idCursor.getLong(idIndex);
                Log.d(TAG, "Announcement with id = " + announcementId + "already exists");
            } else {

                for (Image image : announcement.getImages()) {
                    imageContentValues.add(addImage(announcement.getId(), image));
                }

                announcementVector.add(values);
            }
        }
        saveAnnouncements(announcementVector);
        saveImages(imageContentValues);
    }

    private void saveImages(Vector<ContentValues> imageContentValues) {
        if (imageContentValues.size() > 0) {
            ContentValues[] cvArray = new ContentValues[imageContentValues.size()];
            imageContentValues.toArray(cvArray);
            getContext().getContentResolver().bulkInsert(HelpFindContract.ImageEntry.CONTENT_URI, cvArray);
        }
    }

    private void saveAnnouncements(Vector<ContentValues> announcementVector) {
        if (announcementVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[announcementVector.size()];
            announcementVector.toArray(cvArray);
            getContext().getContentResolver().bulkInsert(HelpFindContract.AnnouncementEntry.CONTENT_URI, cvArray);
        }
    }

    private ContentValues addImage(long announcementId, Image image) {
        ContentValues values = new ContentValues();

        values.put(HelpFindContract.ImageEntry.IMAGE_URL, image.getImageUrl());
        values.put(HelpFindContract.ImageEntry.IMAGE_ANNOUNCEMENT_ID, announcementId);

        return values;
    }


    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if ( null == accountManager.getPassword(newAccount) ) {

            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        HelpFindSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
