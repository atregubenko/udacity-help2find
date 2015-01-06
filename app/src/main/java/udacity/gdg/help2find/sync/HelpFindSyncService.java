package udacity.gdg.help2find.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by nnet on 05.01.15.
 */
public class HelpFindSyncService extends Service {
    private static final Object syncAdapterLock = new Object();
    private static HelpFindSyncAdapter helpFindSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("SunshineSyncService", "onCreate - SunshineSyncService");
        synchronized (syncAdapterLock) {
            if (helpFindSyncAdapter == null) {
                helpFindSyncAdapter = new HelpFindSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return helpFindSyncAdapter.getSyncAdapterBinder();
    }
}
