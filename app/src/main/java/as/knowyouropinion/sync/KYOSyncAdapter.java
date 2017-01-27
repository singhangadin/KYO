package as.knowyouropinion.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.sqlite.SQLiteDatabase;
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
import java.util.HashMap;

import as.knowyouropinion.R;
import as.knowyouropinion.data.QuestionContract;
import as.knowyouropinion.data.QuestionDBHelper;

public class KYOSyncAdapter extends AbstractThreadedSyncAdapter {
    private Context context;
    private ContentValues questionValues;
    private boolean syncStart = false;

    private SQLiteDatabase db;

    // 60 seconds (1 minute) * 180 = 3 hours
    private static final int SYNC_INTERVAL = 60 * 60 * 3;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

//    @Retention(RetentionPolicy.SOURCE)
//    @IntDef({LOCATION_STATUS_OK,
//            LOCATION_STATUS_SERVER_DOWN,
//            LOCATION_STATUS_SERVER_INVALID,
//            LOCATION_STATUS_UNKNOWN,
//            LOCATION_STATUS_INVALID})
//
//    public @interface LocationStatus {}
//
//    public static final int LOCATION_STATUS_OK = 0;
//    public static final int LOCATION_STATUS_SERVER_DOWN = 1;
//    public static final int LOCATION_STATUS_SERVER_INVALID = 2;
//    public static final int LOCATION_STATUS_UNKNOWN = 3;
//    public static final int LOCATION_STATUS_INVALID = 4;

    public KYOSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.context=context;
    }

    @Override
    public void onPerformSync(final Account account, Bundle extras, final String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.e("SYNC","Sync Performed");
        QuestionDBHelper dbHelper = new QuestionDBHelper(context);
        db = dbHelper.getWritableDatabase();
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
                    {   //Temp(database,i,str);
                        new FbDBInsertThread(context, i, str, database).start();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    synchronized private void Temp(FirebaseDatabase database, final int i, String answer)
    {   if(!syncStart)
        {   context.getContentResolver().delete(
                QuestionContract.QuestionEntry.CONTENT_URI,
                null,
                null
            );
            syncStart = true;
        }
        questionValues = new ContentValues();
        questionValues.put(QuestionContract.QuestionEntry.COLUMN_QNO, i);
        questionValues.put(QuestionContract.QuestionEntry.COLUMN_CHOICE, (((int)answer.charAt(0)-(int)'a')+1));
        Log.e("SYNC","Question No. "+i);
        final boolean[] ques = {false};
        final boolean[] ans = {false};
        final boolean[] opt = {false};
        DatabaseReference questions = database.getReference("Questions").child(i+"");
        DatabaseReference options = database.getReference("Options").child(i+"");
        DatabaseReference answers = database.getReference("Answers").child(i+"");
        questions.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String question = (String) dataSnapshot.getValue();
//                Log.e("SYNC","Questions:"+question);
                questionValues.put(QuestionContract.QuestionEntry.COLUMN_QUES, question);
                ques[0] = true;
                if(opt[0]&&ans[0])
                {   Log.e("SYNC","Inserted: "+i+" -"+question+" ");
                    db.insert(QuestionContract.QuestionEntry.TABLE_NAME, null, questionValues);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        options.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> HM = (HashMap<String, String>) dataSnapshot.getValue();
                questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS1, HM.get("a"));
                questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS2, HM.get("b"));
                questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS3, HM.get("c"));
                questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS4, HM.get("d"));
                opt[0] = true;
                if(ques[0]&&ans[0])
                {   Log.e("SYNC","Inserted: "+i);
                    db.insert(QuestionContract.QuestionEntry.TABLE_NAME, null, questionValues);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        answers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Long> HM = (HashMap<String, Long>) dataSnapshot.getValue();
                questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS1V, HM.get("a"));
                questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS2V, HM.get("b"));
                questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS3V, HM.get("c"));
                questionValues.put(QuestionContract.QuestionEntry.COLUMN_ANS4V, HM.get("d"));

                ans[0] = true;
                if(ques[0]&&opt[0])
                {   Log.e("SYNC","Inserted: "+i);
                    db.insert(QuestionContract.QuestionEntry.TABLE_NAME, null, questionValues);
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
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    private static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    private static Account getSyncAccount(Context context) {
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

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