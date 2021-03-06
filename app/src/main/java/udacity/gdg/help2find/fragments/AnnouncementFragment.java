package udacity.gdg.help2find.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.GestureDetector;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import udacity.gdg.help2find.HelpApp;
import udacity.gdg.help2find.R;
import udacity.gdg.help2find.activities.DetailActivity;
import udacity.gdg.help2find.database.DBUtils;
import udacity.gdg.help2find.database.HelpFindContract.AnnouncementEntry;
import udacity.gdg.help2find.database.HelpFindContract.ImageEntry;
import udacity.gdg.help2find.entities.Announcement;
import udacity.gdg.help2find.entities.Image;


public class AnnouncementFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = AnnouncementFragment.class.getSimpleName();
    private static final float SWIPE_MIN_DISTANCE = 10;
    private static final float SWIPE_THRESHOLD_VELOCITY = 20f;
    private static final int DETAIL_LOADER = 0;
    private static final String ARG_ANNOUNCEMENT_ID = "announcement_id";
    public static final String HELP2_FIND_HASH_TAG = "#Help2Find";
    private int mImageWidth;
    private static View view;
    private GoogleMap mGoogleMap;
    private TextView mDescription;
    private ScrollView mScrollView;
    private HorizontalScrollView mImageScroll;
    private LinearLayout mImageContainer;
    private View mTransparentOverlay;
    private ImageView mMarkerLost;
    private TextView mLostHint;
    private LayoutInflater inflater;
    private ActionBarActivity activity;
    private TextView mAddress;
    private int mActiveFeature;
    private List<Image> mItems;
    private GestureDetector mGestureDetector = new GestureDetector(new MyGestureDetector());;
    private ShareActionProvider mShareActionProvider;
    private Announcement mAnnouncement;
    private long mAnnouncementId;

    public static AnnouncementFragment newInstance(long announcementId) {
        AnnouncementFragment fragment = new AnnouncementFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ANNOUNCEMENT_ID, announcementId);
        fragment.setArguments(args);
        return fragment;
    }

    public AnnouncementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mAnnouncementId = arguments.getLong(ARG_ANNOUNCEMENT_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_announcement, container, false);
            mScrollView = (ScrollView) view;
            mDescription = (TextView) view.findViewById(R.id.announcement_info);
            mAddress = (TextView) view.findViewById(R.id.announcement_address);
            mImageScroll = (HorizontalScrollView) view.findViewById(R.id.announcement_image_scroll);
            mImageContainer = (LinearLayout) view.findViewById(R.id.image_container);
            mMarkerLost = (ImageView) view.findViewById(R.id.is_lost);
            mLostHint = (TextView) view.findViewById(R.id.lost_hint);

            mTransparentOverlay =  view.findViewById(R.id.map_overlay);
            mTransparentOverlay.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow ScrollView to intercept touch events.
                            mScrollView.requestDisallowInterceptTouchEvent(true);
                            // Disable touch on transparent view
                            return false;

                        case MotionEvent.ACTION_UP:
                            // Allow ScrollView to intercept touch events.
                            mScrollView.requestDisallowInterceptTouchEvent(false);
                            return true;

                        case MotionEvent.ACTION_MOVE:
                            mScrollView.requestDisallowInterceptTouchEvent(true);
                            return false;

                        default:
                            return true;
                    }
                }
            });
        } catch (InflateException e) {
            Log.e(TAG, e.getMessage());
        }
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.details, menu);
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mAnnouncement != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        Uri uri = HelpApp.getCachedImageUri(mAnnouncement.getPreviewImageUrl());
        if (uri != null) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, mAnnouncement.getTitle() + " " + HELP2_FIND_HASH_TAG);
            shareIntent.setType("image/*");
        } else {
            shareIntent.putExtra(Intent.EXTRA_TEXT, mAnnouncement.getTitle() + " " + mAnnouncement.getDescription() + " " + HELP2_FIND_HASH_TAG);
            shareIntent.setType("text/plain");
        }
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (ActionBarActivity) getActivity();
        inflater = LayoutInflater.from(activity);
    }

    private void initImageScrollView() {
        mImageContainer.removeAllViews();
        mActiveFeature = 0;
        mImageScroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mGestureDetector.onTouchEvent(event)) {
                    return true;
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    int scrollX = mImageScroll.getScrollX();
                    int featureWidth = v.getMeasuredWidth();
                    mActiveFeature = ((scrollX + (featureWidth / 2)) / featureWidth);
                    int scrollTo = mActiveFeature * featureWidth;
                    mImageScroll.smoothScrollTo(scrollTo, 0);
                    return true;
                } else {
                    return false;
                }
            }
        });

        int size = mItems.size();
        mImageWidth = mTransparentOverlay.getMeasuredWidth();
        for (int i = 0; i < size; i++) {
            Image image = mItems.get(i);
            RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.item_image, mImageScroll, false);
            ViewHolder holder = new ViewHolder(view);
            HelpApp.displayImage(image.getImageUrl(), holder.image, R.drawable.details_placeholder);
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) view.getLayoutParams();
            params.width = mImageWidth;
            view.setLayoutParams(params);
            mImageContainer.addView(view);
            if (size > 1) {
                if (i == 0) {
                    holder.leftArrow.setVisibility(View.INVISIBLE);
                    holder.rightArrow.setVisibility(View.VISIBLE);
                } else if (i == size-1) {
                    holder.leftArrow.setVisibility(View.VISIBLE);
                    holder.rightArrow.setVisibility(View.INVISIBLE);
                } else {
                    holder.leftArrow.setVisibility(View.VISIBLE);
                    holder.rightArrow.setVisibility(View.VISIBLE);
                }
            } else {
                holder.leftArrow.setVisibility(View.INVISIBLE);
                holder.rightArrow.setVisibility(View.INVISIBLE);
            }
            holder.leftArrow.setOnClickListener(leftClickListener);
            holder.rightArrow.setOnClickListener(rightClickListener);
        }
        mImageScroll.fullScroll(HorizontalScrollView.FOCUS_LEFT);
    }

    private View.OnClickListener leftClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActiveFeature = (mActiveFeature > 0) ? mActiveFeature - 1 : 0;
            mImageScroll.smoothScrollTo(mActiveFeature * mImageWidth, 0);
        }
    };

    private View.OnClickListener rightClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            mActiveFeature = (mActiveFeature < (mItems.size() - 1))? mActiveFeature + 1:mItems.size() -1;
            mImageScroll.smoothScrollTo(mActiveFeature * mImageWidth, 0);
        }
    };

    private void initMapFragment() {
        SupportMapFragment mapFragment = ((SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map));
        mGoogleMap = mapFragment.getMap();
        if (mGoogleMap != null) {
            mGoogleMap.setMyLocationEnabled(true);
            UiSettings settings = mGoogleMap.getUiSettings();
            settings.setCompassEnabled(false);
            settings.setRotateGesturesEnabled(false);
            settings.setZoomControlsEnabled(false);
            settings.setMyLocationButtonEnabled(true);
            settings.setTiltGesturesEnabled(false);
        }
    }

    public void addMarker(Announcement announcement) {
        if(mGoogleMap !=null){
            LatLng latLng = new LatLng(announcement.getLatitude(),announcement.getLongitude());
            MarkerOptions point = new MarkerOptions()
                    .position(latLng)
                    .title(announcement.getTitle())
                    .snippet(announcement.getAddress())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin));
            mGoogleMap.addMarker(point);
            mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    marker.hideInfoWindow();
                }
            });
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8f));
        }
    }

    @Override
    public void onPause() {
        if(mGoogleMap !=null) {
            mGoogleMap.clear();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(DetailActivity.ANNOUNCEMENT_ID) &&
                mAnnouncement == null) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = AnnouncementEntry.ANNOUNCEMENT_CREATED_AT + " ASC";

        Uri announcementUri = AnnouncementEntry.buildAnnouncementUri(mAnnouncementId);

        return new CursorLoader(
                getActivity(),
                announcementUri,
                null,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            mAnnouncement = DBUtils.fromCursor(data);
            if (mAnnouncement.isLost()) {
                mMarkerLost.setImageResource(R.drawable.details_lost_marker);
                mLostHint.setText(R.string.announcement_lost);
                mLostHint.setBackgroundColor(getResources().getColor(R.color.lost_color_overlay));
            } else if (!mAnnouncement.isLost()) {
                mMarkerLost.setImageResource(R.drawable.details_found_marker);
                mLostHint.setText(R.string.announcement_found);
                mLostHint.setBackgroundColor(getResources().getColor(R.color.found_color_overlay));

            }
            activity.getSupportActionBar().setTitle(mAnnouncement.getTitle());
            mDescription.setText(mAnnouncement.getDescription());
            mAddress.setText("Address: " + mAnnouncement.getAddress());
            initImages();
            initImageScrollView();
            initMapFragment();
            addMarker(mAnnouncement);
        }
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    private void initImages() {
        mItems = new ArrayList<Image>();
        Cursor cursor = getActivity().getContentResolver().query(
                ImageEntry.CONTENT_URI,
                null,
                ImageEntry.IMAGE_ANNOUNCEMENT_ID + " = ?",
                new String[]{String.valueOf(mAnnouncement.getId())},
                null);
        while (cursor.moveToNext()) {
            Image image = new Image();
            image.setImageUrl(cursor.getString(cursor.getColumnIndex(ImageEntry.IMAGE_URL)));
            image.setId(cursor.getLong(cursor.getColumnIndex(ImageEntry.IMAGE_ID)));
            image.setAnnouncement(mAnnouncement);
            mItems.add(image);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private static class ViewHolder {
        ImageView image;
        ImageView leftArrow;
        ImageView rightArrow;

        private ViewHolder(View view) {
            image = (ImageView) view.findViewById(R.id.image);
            leftArrow = (ImageView) view.findViewById(R.id.arrow_left);
            rightArrow = (ImageView) view.findViewById(R.id.arrow_right);
        }

    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                //right to left
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    mActiveFeature = (mActiveFeature < (mItems.size() - 1))? mActiveFeature + 1:mItems.size() -1;
                    mImageScroll.smoothScrollTo(mActiveFeature * mImageWidth, 0);
                    return true;
                }
                //left to right
                else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    mActiveFeature = (mActiveFeature > 0)? mActiveFeature - 1:0;
                    mImageScroll.smoothScrollTo(mActiveFeature * mImageWidth, 0);
                    return true;
                }
            } catch (Exception e) {
                Log.e("Fling", "There was an error processing the Fling event:" + e.getMessage());
            }
            return false;
        }
    }


}
