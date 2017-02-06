package as.knowyouropinion.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import as.knowyouropinion.R;
import as.knowyouropinion.data.QuestionContract;

public class KYOSyncAdapter extends AbstractThreadedSyncAdapter {
    private Context context;

    public static final String ACTION_DATA_UPDATED = "as.knowyouropinion.DATA_UPDATED";

    private static final int SYNC_INTERVAL = 60 * 15;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public KYOSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context=context;
    }

    @Override
    public void onPerformSync(final Account account, Bundle extras, final String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.e("SYNC","Sync Performed");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        String email = preferences.getString("EMAIL","Email");
        String userID = email.split("@")[0].replace(".",",");
        final DatabaseReference user = database.getReference("Users").child(userID);
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                context.getContentResolver().delete(
                    QuestionContract.QuestionEntry.CONTENT_URI,
                    null,
                    null
                );
                ArrayList<String> ans = (ArrayList<String>) dataSnapshot.getValue();
                for(int i=0; i<ans.size(); i++)
                {   String str = ans.get(i);
                    if(str!=null&&!str.equals("null"))
                    {   new FbDBInsertThread(context, i, str, database).start();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        }
        else {
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }

    private static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

    private static Account getSyncAccount(Context context) {
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));
        if ( null == accountManager.getPassword(newAccount) ) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        KYOSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}