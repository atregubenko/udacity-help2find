package udacity.gdg.help2find.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import udacity.gdg.help2find.R;
import udacity.gdg.help2find.fragments.AnnouncementFragment;
import udacity.gdg.help2find.fragments.CategoryListFragment;
import udacity.gdg.help2find.fragments.NavigationDrawerFragment;


public class MainActivity extends ActionBarActivity implements CategoryListFragment.OnCategoryItemSelectedListener,
        AnnouncementFragment.OnFragmentInteractionListener, NavigationDrawerFragment.NavigationDrawerCallbacks {
    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        TODO
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, CategoryFragment.newInstance(3))
//                    .commit();
//        }
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }
//

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
//        if (mTwoPane) {
//            // In two-pane mode, show the detail view in this activity by
//            // adding or replacing the detail fragment using a
//            // fragment transaction.
//            Bundle args = new Bundle();
//            args.putString(DetailActivity.DATE_KEY, date);
//
//            DetailFragment fragment = new DetailFragment();
//            fragment.setArguments(args);
//
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.weather_detail_container, fragment)
//                    .commit();
//        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra(DetailActivity.ANNOUNCEMENT_ID, announcementId);
            startActivity(intent);
//        }
    }

    @Override
    public void onCategorySelected(long categoryId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, CategoryListFragment.newInstance(categoryId));
        ft.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
