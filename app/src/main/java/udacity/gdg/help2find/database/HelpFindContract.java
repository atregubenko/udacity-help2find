package udacity.gdg.help2find.database;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import udacity.gdg.help2find.entities.Announcement;

/**
 * Created by nnet on 30.12.14.
 */
public class HelpFindContract {
    public static final String CONTENT_AUTHORITY = "udacity.gdg.help2find";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    static final String ANNOUNCEMENT_PATH = "announcement";
    static final String IMAGE_PATH = "image";
    static final String CATEGORY_PATH = "category";

    public static final String DATE_FORMAT = "yyyyMMdd";

    public static final class CategoryEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(CATEGORY_PATH).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + CATEGORY_PATH;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + CATEGORY_PATH;

        public static final String TABLE_NAME = "category";

        public static final String CATEGORY_ID ="_id";
        public static final String CATEGORY_NAME ="name";
        public static final String CATEGORY_PHOTO ="photo";
        public static final String CATEGORY_DESCRIPTION ="description";

        public static Uri buildCategoryUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class AnnouncementEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(ANNOUNCEMENT_PATH).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + ANNOUNCEMENT_PATH;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + ANNOUNCEMENT_PATH;

        public static final String TABLE_NAME = "announcement";

        public static final String ANNOUNCEMENT_ID ="_id";
        public static final String ANNOUNCEMENT_ADDRESS ="address";
        public static final String ANNOUNCEMENT_CATEGORY ="category_id";
        public static final String ANNOUNCEMENT_DATE ="date";
        public static final String ANNOUNCEMENT_DESCRIPTION ="about";
        public static final String ANNOUNCEMENT_IS_LOST ="islost";
        public static final String ANNOUNCEMENT_LATITUDE ="latitude";
        public static final String ANNOUNCEMENT_LONGITUDE ="longitude";
        public static final String ANNOUNCEMENT_TITLE ="title";
        public static final String ANNOUNCEMENT_PREVIEW_IMAGE ="preview_image";
        public static final String ANNOUNCEMENT_CREATED_AT ="created_at";
        public static final String ANNOUNCEMENT_UPDATED_AT ="updated_at";

        public static Uri buildAnnouncementUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildAnnouncementByCategory(long categoryId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(categoryId))
                    .appendPath(String.valueOf(categoryId)).build();
        }

        public static Uri buildAnnouncementWithImagesById(long announcementId) {
            return CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(announcementId))
                    .build();
        }

        public static Uri buildAnnouncementByCategoryOrderBy(long categoryId) {
            return CONTENT_URI.buildUpon()
                    .appendQueryParameter(ANNOUNCEMENT_CATEGORY, String.valueOf(categoryId))
                    .build();
        }


        public static String getCategoryFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

    }

    public static final class ImageEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(IMAGE_PATH).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + IMAGE_PATH;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + IMAGE_PATH;

        public static final String TABLE_NAME = "image";

        public static final String IMAGE_ID ="_id";
        public static final String IMAGE_ANNOUNCEMENT_ID ="announcement_id";
        public static final String IMAGE_URL ="image_url";

        public static Uri buildImageUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildImageWithAnnouncementId(long announcementId) {
            return CONTENT_URI.buildUpon().appendPath(String.valueOf(announcementId)).build();
        }

    }

    public static String getDbDateString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    public static Date getDateFromDb(String dateText) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return dbDateFormat.parse(dateText);
        } catch ( ParseException e ) {
            e.printStackTrace();
            return null;
        }
    }
}
