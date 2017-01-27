package as.knowyouropinion.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class KYOAuthenticatorService extends Service {
    private KYOAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new KYOAuthenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
