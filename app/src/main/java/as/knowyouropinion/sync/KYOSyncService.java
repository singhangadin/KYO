package as.knowyouropinion.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class KYOSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static KYOSyncAdapter sSunshineSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("KYOSyncService", "onCreate - KYOSyncService");
        synchronized (sSyncAdapterLock) {
            if (sSunshineSyncAdapter == null) {
                sSunshineSyncAdapter = new KYOSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSunshineSyncAdapter.getSyncAdapterBinder();
    }
}