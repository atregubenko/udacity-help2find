package udacity.gdg.help2find.database;

import android.database.Cursor;

import udacity.gdg.help2find.database.HelpFindContract.AnnouncementEntry;
import udacity.gdg.help2find.entities.Announcement;

/**
 * Created by nnet on 05.01.15.
 */
public class DBUtils {
    public static Announcement fromCursor(Cursor cursor) {
        Announcement announcement = new Announcement();
        announcement.setId(cursor.getLong(cursor.getColumnIndex(AnnouncementEntry.ANNOUNCEMENT_ID)));
        announcement.setTitle(cursor.getString(cursor.getColumnIndex(AnnouncementEntry.ANNOUNCEMENT_TITLE)));
        announcement.setDescription(cursor.getString(cursor.getColumnIndex(AnnouncementEntry.ANNOUNCEMENT_DESCRIPTION)));
        announcement.setAddress(cursor.getString(cursor.getColumnIndex(AnnouncementEntry.ANNOUNCEMENT_ADDRESS)));
        announcement.setCategory(cursor.getInt(cursor.getColumnIndex(AnnouncementEntry.ANNOUNCEMENT_CATEGORY)));

        announcement.setCreatedAt(cursor.getString(cursor.getColumnIndex(AnnouncementEntry.ANNOUNCEMENT_CREATED_AT)));
        announcement.setUpdatedAt(cursor.getString(cursor.getColumnIndex(AnnouncementEntry.ANNOUNCEMENT_UPDATED_AT)));
        announcement.setDate(cursor.getString(cursor.getColumnIndex(AnnouncementEntry.ANNOUNCEMENT_DATE)));

        announcement.setLost(cursor.getInt(cursor.getColumnIndex(AnnouncementEntry.ANNOUNCEMENT_IS_LOST)) > 0);
        announcement.setLatitude(cursor.getDouble(cursor.getColumnIndex(AnnouncementEntry.ANNOUNCEMENT_LATITUDE)));
        announcement.setLongitude(cursor.getDouble(cursor.getColumnIndex(AnnouncementEntry.ANNOUNCEMENT_LONGITUDE)));
        announcement.setPreviewImageUrl(cursor.getString(cursor.getColumnIndex(AnnouncementEntry.ANNOUNCEMENT_PREVIEW_IMAGE)));
        return announcement;
    }
}
