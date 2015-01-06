package udacity.gdg.help2find.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import udacity.gdg.help2find.R;
import udacity.gdg.help2find.fragments.AnnouncementFragment;
import udacity.gdg.help2find.fragments.BlankFragment;
import udacity.gdg.help2find.fragments.CategoryListFragment;
import udacity.gdg.help2find.fragments.NavigationDrawerFragment;
import udacity.gdg.help2find.sync.HelpFindSyncAdapter;


public class MainActivity extends ActionBarActivity implements CategoryListFragment.OnCategoryItemSelectedListener,
        NavigationDrawerFragment.CategorySelectedListener {
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private boolean mTwoPane;
    private long mCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HelpFindSyncAdapter.initializeSyncAdapter(this);
        HelpFindSyncAdapter.syncImmediately(this);

        if (findViewById(R.id.fragment_category) != null) {
            mTwoPane = true;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, BlankFragment.newInstance())
                    .commit();
        } else {
            mTwoPane = false;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, CategoryListFragment.newInstance(mCategoryId))
                        .commit();
                HelpFindSyncAdapter.syncImmediately(this);
            }
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        mNavigationDrawerFragment.init(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCategoryItemSelected(long announcementId) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putLong(DetailActivity.ANNOUNCEMENT_ID, announcementId);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, AnnouncementFragment.newInstance(announcementId))
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(DetailActivity.ANNOUNCEMENT_ID, announcementId);
            startActivity(intent);
        }
    }

    @Override
    public void onCategorySelected(long categoryId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (mTwoPane) {
            CategoryListFragment fragmentById = (CategoryListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_category);
            fragmentById.setCategoryId(categoryId);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, BlankFragment.newInstance())
                    .commit();
        } else {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.container, CategoryListFragment.newInstance(categoryId));
            ft.commit();
        }
    }
}
