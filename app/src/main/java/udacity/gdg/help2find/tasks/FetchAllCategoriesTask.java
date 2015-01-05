package udacity.gdg.help2find.tasks;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
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
import java.util.List;
import java.util.Vector;

import udacity.gdg.help2find.activities.SplashActivity;
import udacity.gdg.help2find.database.HelpFindContract.CategoryEntry;
import udacity.gdg.help2find.entities.Category;
import udacity.gdg.help2find.utils.JsonUtils;

/**
 * Created by nnet on 30.12.14.
 */
public class FetchAllCategoriesTask extends AsyncTask<String, Void, Void> {

    private static final String ALL_CATEGORIES_URL = "http://helpme2findit.herokuapp.com:80/api/categories.json";
    private final String LOG_TAG = FetchAllCategoriesTask.class.getSimpleName();
    private final Context mContext;
    private final SplashActivity mActivity;
    private static final int DELAY_IN_SECONDS = 2;

    public FetchAllCategoriesTask(SplashActivity activity) {
        mContext = activity;
        mActivity = activity;
    }

    @Override
    protected Void doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String forecastJsonStr = null;
        try {
            Uri builtUri = Uri.parse(ALL_CATEGORIES_URL).buildUpon()
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            forecastJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            return null;
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
            getDataFromJson(forecastJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private void getDataFromJson(String forecastJsonStr) throws JSONException {
        List<Category> categories = new ArrayList<Category>();
        JSONArray response = new JSONArray(forecastJsonStr);
        for (int i = 0; i< response.length(); i++) {
            try {
                JsonNode actualObj = JsonUtils.defaultMapper().readTree(String.valueOf(response.get(i)));
                categories.add(JsonUtils.parseJson(actualObj, Category.class));
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Vector<ContentValues> announcementVector = new Vector<ContentValues>(categories.size());

        for(Category category : categories) {
            ContentValues values = new ContentValues();
            values.put(CategoryEntry.CATEGORY_ID, category.getId());

            values.put(CategoryEntry.CATEGORY_NAME, category.getName());
            values.put(CategoryEntry.CATEGORY_DESCRIPTION, category.getDescription());
            values.put(CategoryEntry.CATEGORY_PHOTO, category.getPhoto());

            announcementVector.add(values);
        }
        saveAnnouncements(announcementVector);
    }

    private void saveAnnouncements(Vector<ContentValues> announcementVector) {
        if (announcementVector.size() > 0) {
            ContentValues[] cvArray = new ContentValues[announcementVector.size()];
            announcementVector.toArray(cvArray);
            mContext.getContentResolver().bulkInsert(CategoryEntry.CONTENT_URI, cvArray);
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                mActivity.startMainActivity();
            }
        }, DELAY_IN_SECONDS * 1000);
    }
}
