package udacity.gdg.help2find.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import udacity.gdg.help2find.HelpApp;
import udacity.gdg.help2find.R;
import udacity.gdg.help2find.database.HelpFindContract.AnnouncementEntry;

/**
 * Created by nnet on 30.12.14.
 */
public class CategoryCursorAdapter extends CursorAdapter implements Filterable {
    private static final String TAG = CategoryCursorAdapter.class.getSimpleName();
    private final Context mContext;
    private final long mCategoryId;
    private String orderCondition;

    public CategoryCursorAdapter(Context context, Cursor c, int flags, long categoryId) {
        super(context, c, flags);
        mContext = context;
        mCategoryId = categoryId;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder vh = (ViewHolder) view.getTag();
        String title = cursor.getString(cursor.getColumnIndex(AnnouncementEntry.ANNOUNCEMENT_TITLE));
        vh.title.setText(title);
        vh.description.setText(cursor.getString(cursor.getColumnIndex(AnnouncementEntry.ANNOUNCEMENT_DESCRIPTION)));
        String imageUrl = cursor.getString(cursor.getColumnIndex(AnnouncementEntry.ANNOUNCEMENT_PREVIEW_IMAGE));
        HelpApp.displayImage(imageUrl, vh.image, R.drawable.details_placeholder);
        cursor.getColumnIndex(AnnouncementEntry.ANNOUNCEMENT_TITLE);
        boolean isLost = cursor.getInt(cursor.getColumnIndex(AnnouncementEntry.ANNOUNCEMENT_IS_LOST)) > 0;
        if (isLost) {
            vh.markerLost.setImageResource(R.drawable.list_lost_marker);
            vh.lostHint.setText(R.string.category_lost);
            vh.lostHint.setBackgroundColor(context.getResources().getColor(R.color.lost_color_overlay));
        } else {
            vh.markerLost.setImageResource(R.drawable.list_found_marker);
            vh.lostHint.setText(R.string.category_found);
            vh.lostHint.setBackgroundColor(context.getResources().getColor(R.color.found_color_overlay));
        }

    }

    private static class ViewHolder {
        ImageView image;
        ImageView markerLost;
        TextView title;
        TextView description;
        TextView lostHint;

        private ViewHolder(View view) {
            image = (ImageView) view.findViewById(R.id.item_image);
            markerLost = (ImageView) view.findViewById(R.id.item_is_lost);
            title = (TextView) view.findViewById(R.id.item_title);
            description = (TextView) view.findViewById(R.id.item_description);
            lostHint = (TextView) view.findViewById(R.id.itemt_lost_hint);
        }

    }

    public String getOrderCondition() {
        return orderCondition;
    }

    public void setOrderCondition(String orderCondition) {
        this.orderCondition = orderCondition;
    }
}
