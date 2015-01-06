package udacity.gdg.help2find.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by nnet on 05.01.15.
 */
public class HelpFindAuthenticatorService extends Service {
    private HelpFindAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new HelpFindAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}

