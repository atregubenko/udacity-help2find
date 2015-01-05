package udacity.gdg.help2find.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import udacity.gdg.help2find.R;
import udacity.gdg.help2find.adapters.CategoryCursorAdapter;
import udacity.gdg.help2find.database.HelpFindContract.AnnouncementEntry;
import udacity.gdg.help2find.tasks.FetchAnnouncementsByCategoryTask;

public class CategoryListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    protected static final String ARG_CATEGORY_ID = "category_id";
    private OnCategoryItemSelectedListener mListener;
    private ListView mListView;
    private CategoryCursorAdapter mCategoryAdapter;
    private static final int ANNOUNCEMENT_LOADER = 0;
    private long mCategoryId;
    private int mPosition;

    public static CategoryListFragment newInstance(long categoryId) {
        CategoryListFragment fragment = new CategoryListFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CATEGORY_ID, categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    public CategoryListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mCategoryId = arguments.getLong(ARG_CATEGORY_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        mListView = (ListView) view.findViewById(R.id.listView);

        Cursor cursor = getActivity().getContentResolver().query(
                AnnouncementEntry.buildAnnouncementByCategory(mCategoryId), null, null, null, null);

        mCategoryAdapter = new CategoryCursorAdapter(
                getActivity(),
                cursor,
                0,
                mCategoryId
        );

        mListView.setAdapter(mCategoryAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Cursor cursor = mCategoryAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    ((OnCategoryItemSelectedListener) getActivity())
                            .onCategoryItemSelected(cursor.getLong(cursor.getColumnIndex(AnnouncementEntry.ANNOUNCEMENT_ID)));
                }
                mPosition = position;
            }
        });


        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(ANNOUNCEMENT_LOADER, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            new FetchAnnouncementsByCategoryTask(getActivity(), mCategoryId).execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(ANNOUNCEMENT_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnCategoryItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = AnnouncementEntry.ANNOUNCEMENT_CREATED_AT + " DESC";

        return new CursorLoader(
                getActivity(),
                AnnouncementEntry.buildAnnouncementByCategory(mCategoryId),
                null,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCategoryAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCategoryAdapter.swapCursor(null);
    }

    public interface OnCategoryItemSelectedListener {
        public void onCategoryItemSelected(long announcementId);
    }

}
