package udacity.gdg.help2find.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import udacity.gdg.help2find.R;
import udacity.gdg.help2find.fragments.AnnouncementFragment;

public class DetailActivity extends ActionBarActivity {

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

}
