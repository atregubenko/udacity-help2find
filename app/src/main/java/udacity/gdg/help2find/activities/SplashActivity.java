package udacity.gdg.help2find.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import udacity.gdg.help2find.R;
import udacity.gdg.help2find.tasks.FetchAllCategoriesTask;

public class SplashActivity extends FragmentActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private static final int DELAY_IN_SECONDS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new FetchAllCategoriesTask(SplashActivity.this).execute();
    }

    public void startMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
