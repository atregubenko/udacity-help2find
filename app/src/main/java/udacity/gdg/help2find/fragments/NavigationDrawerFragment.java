package udacity.gdg.help2find.fragments;

import android.app.Activity;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import udacity.gdg.help2find.R;
import udacity.gdg.help2find.adapters.DrawerCursorAdapter;
import udacity.gdg.help2find.database.HelpFindContract;

/**
 * Created by nnet on 04.01.15.
 */
public class NavigationDrawerFragment extends Fragment{
    private static final String TAG = NavigationDrawerFragment.class.getSimpleName();
    private static final String CURRENT_POSITION = "current_position";
    private static final String NEED_TO_OPEN_DRAWER = "need_to_open_drawer";

    private CategorySelectedListener mCallbacks;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mContainer;
    private int mSelectedPosition = 1;
    private boolean mNeedToOpenDrawer = true;
    private DrawerCursorAdapter mAdapter;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mSelectedPosition = savedInstanceState.getInt(CURRENT_POSITION);
            mNeedToOpenDrawer = savedInstanceState.getBoolean(NEED_TO_OPEN_DRAWER);
        }
        selectItem(mSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDrawerListView = (ListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        Uri contentUri = HelpFindContract.CategoryEntry.CONTENT_URI;
        Cursor cursor = getActivity().getContentResolver().query(
                contentUri,
                null,
                null,
                null,
                null);
        mAdapter = new DrawerCursorAdapter(getActivity(), cursor, 0);
        mDrawerListView.setAdapter(mAdapter);
        selectItem(mSelectedPosition);
        return mDrawerListView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mContainer);
    }

    public void init(int fragmentId, DrawerLayout drawerLayout) {
        mContainer = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),
                mDrawerLayout,
                R.drawable.ic_navigation_drawer_indicator,
                android.R.drawable.ic_delete
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }
                getActivity().supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }
                getActivity().supportInvalidateOptionsMenu();
            }
        };
            if (mNeedToOpenDrawer) {
                mDrawerLayout.openDrawer(mContainer);
                mNeedToOpenDrawer = false;
            } else if (mSelectedPosition > 0) {
                selectItem(mSelectedPosition);
            }
            mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mContainer);
        }
        categorySelected(position);
    }

    private void categorySelected(int position) {
        if (mCallbacks != null && mAdapter != null) {
            Cursor cursor = mAdapter.getCursor();
            cursor.moveToPosition(position);
            String title = cursor.getString(cursor.getColumnIndex(HelpFindContract.CategoryEntry.CATEGORY_NAME));
            ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(title);
            mCallbacks.onCategorySelected(cursor.getLong(cursor.getColumnIndex(HelpFindContract.CategoryEntry.CATEGORY_ID)));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (CategorySelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_POSITION, mSelectedPosition);
        outState.putBoolean(NEED_TO_OPEN_DRAWER, mNeedToOpenDrawer);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mDrawerLayout != null && isDrawerOpen()) {
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showGlobalContextActionBar() {
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    public static interface CategorySelectedListener {
        void onCategorySelected(long categoryId);
    }
}
