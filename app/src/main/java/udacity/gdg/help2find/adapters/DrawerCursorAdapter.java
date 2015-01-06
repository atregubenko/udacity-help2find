package udacity.gdg.help2find.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import udacity.gdg.help2find.R;
import udacity.gdg.help2find.database.HelpFindContract.CategoryEntry;
import udacity.gdg.help2find.database.HelpFindProvider;

/**
 * Created by nnet on 31.12.14.
 */
public class DrawerCursorAdapter extends CursorAdapter {
    private static final String TAG = DrawerCursorAdapter.class.getSimpleName();
    private HelpFindProvider mContent;

    public DrawerCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_drawer, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder vh = (ViewHolder) view.getTag();
        String title = cursor.getString(cursor.getColumnIndex(CategoryEntry.CATEGORY_NAME));
        vh.title.setText(title);
        vh.image.setImageResource(vh.getDefaultImageResByCategoryId(cursor.getInt(cursor.getColumnIndex(CategoryEntry.CATEGORY_ID))));
        String imageUrl = cursor.getString(cursor.getColumnIndex(CategoryEntry.CATEGORY_PHOTO));
    }

    private static class ViewHolder {
        ImageView image;
        TextView title;

        private ViewHolder(View view) {
            image = (ImageView) view.findViewById(R.id.category_image);
            title = (TextView) view.findViewById(R.id.category_name);
        }

        private int getDefaultImageResByCategoryId(int categoryId) {
            switch (categoryId) {
                case 1:
                    return R.drawable.animals_selector;
                case 2:
                    return R.drawable.people_selector;
                case 3:
                    return R.drawable.things_selector;
                case 4:
                    return R.drawable.crime_selector;
            }
            return -1;
        }

    }
}
