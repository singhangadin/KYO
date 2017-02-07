package as.knowyouropinion.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import static as.knowyouropinion.sync.KYOSyncAdapter.ACTION_DATA_UPDATED;

/**
 * <p>
 * Created by Angad on 6/2/17.
 * </p>
 */

public class Utility {

    public static boolean adShown = false;

    public static boolean isNetworkAvailable(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static void updateWidget(Context c) {
        Log.d("mytag", "updated widget");
        Intent updatedDataIntent = new Intent(ACTION_DATA_UPDATED);
        updatedDataIntent.setPackage(c.getPackageName());
        c.sendBroadcast(updatedDataIntent);
    }
}
