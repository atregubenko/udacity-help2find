package udacity.gdg.help2find.activities;

import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import udacity.gdg.help2find.R;
import udacity.gdg.help2find.fragments.AnnouncementFragment;

public class DetailActivity extends ActionBarActivity implements AnnouncementFragment.OnFragmentInteractionListener {

    public static final String ANNOUNCEMENT_ID = "announcement_id";
    private long mAnnouncementId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            mAnnouncementId = getIntent().getLongExtra(ANNOUNCEMENT_ID, -1);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, AnnouncementFragment.newInstance(mAnnouncementId))
                    .commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
