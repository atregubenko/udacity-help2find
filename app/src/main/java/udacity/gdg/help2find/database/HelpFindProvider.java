package udacity.gdg.help2find.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import udacity.gdg.help2find.database.HelpFindContract.AnnouncementEntry;
import udacity.gdg.help2find.database.HelpFindContract.ImageEntry;
import udacity.gdg.help2find.database.HelpFindContract.CategoryEntry;

/**
 * Created by nnet on 30.12.14.
 */
public class HelpFindProvider extends ContentProvider {
    private static final int ANNOUNCEMENT = 100;
    private static final int ANNOUNCEMENT_BY_CATEGORY = 101;
    private static final int ANNOUNCEMENT_ID = 102;
    private static final int IMAGE = 200;
    private static final int IMAGE_ID = 201;
    private static final int CATEGORY = 300;
    private static final int CATEGORY_ID = 301;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private HelpFindDBHelper mOpenHelper;
    private static final SQLiteQueryBuilder sQueryBuilder;

    static{
        sQueryBuilder = new SQLiteQueryBuilder();
        sQueryBuilder.setTables(
                HelpFindContract.AnnouncementEntry.TABLE_NAME
        );
    }

    private String sAnnouncementByCategorySelection =
            AnnouncementEntry.TABLE_NAME+
                    "." + AnnouncementEntry.ANNOUNCEMENT_CATEGORY + " = ? ";

    private String sAnnouncementIdSelection =
            AnnouncementEntry.TABLE_NAME+
                    "." + AnnouncementEntry.ANNOUNCEMENT_ID + " = ? ";

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        final String authority = HelpFindContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, HelpFindContract.ANNOUNCEMENT_PATH, ANNOUNCEMENT);
        matcher.addURI(authority, HelpFindContract.ANNOUNCEMENT_PATH + "/*/*", ANNOUNCEMENT_BY_CATEGORY);
        matcher.addURI(authority, HelpFindContract.ANNOUNCEMENT_PATH + "/*", ANNOUNCEMENT_ID);

        matcher.addURI(authority, HelpFindContract.IMAGE_PATH, IMAGE);
        matcher.addURI(authority, HelpFindContract.IMAGE_PATH + "/#", IMAGE_ID);

        matcher.addURI(authority, HelpFindContract.CATEGORY_PATH, CATEGORY);
        matcher.addURI(authority, HelpFindContract.CATEGORY_PATH + "/#", CATEGORY_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new HelpFindDBHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case ANNOUNCEMENT_BY_CATEGORY:
            {
                retCursor = getAnnouncementByCategory(uri, projection, sortOrder);
                break;
            }
            case ANNOUNCEMENT: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        AnnouncementEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case ANNOUNCEMENT_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        AnnouncementEntry.TABLE_NAME,
                        projection,
                        AnnouncementEntry.ANNOUNCEMENT_ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case IMAGE_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ImageEntry.TABLE_NAME,
                        projection,
                        ImageEntry.IMAGE_ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case IMAGE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ImageEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case CATEGORY_ID: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        CategoryEntry.TABLE_NAME,
                        projection,
                        CategoryEntry.CATEGORY_ID + " = '" + ContentUris.parseId(uri) + "'",
                        null,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case CATEGORY: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        CategoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ANNOUNCEMENT:
                return AnnouncementEntry.CONTENT_TYPE;
            case ANNOUNCEMENT_BY_CATEGORY:
                return AnnouncementEntry.CONTENT_TYPE;
            case ANNOUNCEMENT_ID:
                return AnnouncementEntry.CONTENT_ITEM_TYPE;
            case IMAGE:
                return ImageEntry.CONTENT_TYPE;
            case IMAGE_ID:
                return ImageEntry.CONTENT_ITEM_TYPE;
            case CATEGORY:
                return CategoryEntry.CONTENT_TYPE;
            case CATEGORY_ID:
                return CategoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case ANNOUNCEMENT: {
                long _id = db.insert(AnnouncementEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = AnnouncementEntry.buildAnnouncementUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case IMAGE: {
                long _id = db.insert(ImageEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ImageEntry.buildImageUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case CATEGORY: {
                long _id = db.insert(ImageEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = CategoryEntry.buildCategoryUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case ANNOUNCEMENT:
                rowsDeleted = db.delete(
                        AnnouncementEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case IMAGE:
                rowsDeleted = db.delete(
                        ImageEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CATEGORY:
                rowsDeleted = db.delete(
                        CategoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String whereClause, String[] whereArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case ANNOUNCEMENT:
                rowsUpdated = db.update(
                        AnnouncementEntry.TABLE_NAME, values, whereClause, whereArgs);
                break;
            case IMAGE:
                rowsUpdated = db.update(
                        ImageEntry.TABLE_NAME, values, whereClause, whereArgs);
                break;
            case CATEGORY:
                rowsUpdated = db.update(
                        CategoryEntry.TABLE_NAME, values, whereClause, whereArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount;
        switch (match) {
            case ANNOUNCEMENT:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(AnnouncementEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case IMAGE:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ImageEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case CATEGORY:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(CategoryEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    private Cursor getAnnouncementByCategory(
            Uri uri, String[] projection, String sortOrder) {
        String categoryFromUri = AnnouncementEntry.getCategoryFromUri(uri);

        return sQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sAnnouncementByCategorySelection,
                new String[]{categoryFromUri},
                null,
                null,
                sortOrder
        );
    }

}
