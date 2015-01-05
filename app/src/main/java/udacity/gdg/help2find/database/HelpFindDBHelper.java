package udacity.gdg.help2find.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import udacity.gdg.help2find.database.HelpFindContract.AnnouncementEntry;
import udacity.gdg.help2find.database.HelpFindContract.ImageEntry;
import udacity.gdg.help2find.database.HelpFindContract.CategoryEntry;

/**
 * Created by nnet on 30.12.14.
 */
public class HelpFindDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "base.db";
    private static final int DATABASE_VERSION = 1;
    public HelpFindDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_CATEGORY_TABLE = "CREATE TABLE " + CategoryEntry.TABLE_NAME + " (" +
                CategoryEntry.CATEGORY_ID + " INTEGER PRIMARY KEY," +
                CategoryEntry.CATEGORY_NAME + " TEXT NOT NULL, " +
                CategoryEntry.CATEGORY_DESCRIPTION + " TEXT, " +
                CategoryEntry.CATEGORY_PHOTO + " TEXT, " +
                " FOREIGN KEY (" + CategoryEntry.CATEGORY_ID + ") REFERENCES " +
                AnnouncementEntry.TABLE_NAME + " (" + AnnouncementEntry.ANNOUNCEMENT_CATEGORY + ") " +
                " );";
        final String SQL_CREATE_IMAGE_TABLE = "CREATE TABLE " + ImageEntry.TABLE_NAME + " (" +
                ImageEntry.IMAGE_ID + " INTEGER PRIMARY KEY," +
                ImageEntry.IMAGE_ANNOUNCEMENT_ID + " INTEGER NOT NULL, " +
                ImageEntry.IMAGE_URL + " TEXT, " +
                " FOREIGN KEY (" + ImageEntry.IMAGE_ANNOUNCEMENT_ID + ") REFERENCES " +
                AnnouncementEntry.TABLE_NAME + " (" + AnnouncementEntry.ANNOUNCEMENT_ID + ") " +

                " );";
        final String SQL_CREATE_ANNOUNCEMENT_TABLE = "CREATE TABLE " + AnnouncementEntry.TABLE_NAME + " (" +
                AnnouncementEntry.ANNOUNCEMENT_ID + " INTEGER PRIMARY KEY , " +
                AnnouncementEntry.ANNOUNCEMENT_CATEGORY + " INTEGER, " +
                AnnouncementEntry.ANNOUNCEMENT_ADDRESS + " TEXT, " +
                AnnouncementEntry.ANNOUNCEMENT_DESCRIPTION + " TEXT, " +
                AnnouncementEntry.ANNOUNCEMENT_DATE + " INTEGER," +

                AnnouncementEntry.ANNOUNCEMENT_TITLE + " TEXT, " +
                AnnouncementEntry.ANNOUNCEMENT_IS_LOST + " BOOLEAN, " +

                AnnouncementEntry.ANNOUNCEMENT_LATITUDE + " REAL, " +
                AnnouncementEntry.ANNOUNCEMENT_LONGITUDE + " REAL, " +
                AnnouncementEntry.ANNOUNCEMENT_PREVIEW_IMAGE + " TEXT, " +
                AnnouncementEntry.ANNOUNCEMENT_CREATED_AT + " INTEGER, " +
                AnnouncementEntry.ANNOUNCEMENT_UPDATED_AT + " INTEGER " +

                ");";

        db.execSQL(SQL_CREATE_CATEGORY_TABLE);
        db.execSQL(SQL_CREATE_IMAGE_TABLE);
        db.execSQL(SQL_CREATE_ANNOUNCEMENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + CategoryEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ImageEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AnnouncementEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
