package as.knowyouropinion;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

import as.knowyouropinion.data.QuestionContract;
import as.knowyouropinion.model.DrawerAdapter;
import as.knowyouropinion.sync.KYOSyncAdapter;
import as.knowyouropinion.utils.OnRecyclerClickListener;
import as.knowyouropinion.utils.RecyclerTouchHelper;

import static as.knowyouropinion.R.id.recyclerView;

public class MainActivity extends AppCompatActivity implements
        OnRecyclerClickListener,
        GoogleApiClient.OnConnectionFailedListener {

    private int ICONS[] = {R.drawable.ic_action_home,
            R.drawable.ic_action_answered,
            R.drawable.ic_action_log_out};

    private FrameLayout mDrawerLayout;
    private DrawerLayout mDrawer;
    private View lastChild = null;
    private int childPos = 0;
    private RecyclerView.Adapter mAdapter;
    private GoogleApiClient mGoogleApiClient;
    private Toolbar mToolbar;
    private String[] TITLES;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TITLES = new String[]{getResources().getString(R.string.label_home),
                getResources().getString(R.string.label_history),
                getResources().getString(R.string.label_logout)};

        setContentView(R.layout.activity_main);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(BuildConfig.OAUTH_CLIENT_ID)
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        KYOSyncAdapter.initializeSyncAdapter(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String NAME = preferences.getString("NAME", "Name");
        String EMAIL = preferences.getString("EMAIL", "Email");
        String PROFILE = preferences.getString("PHOTO", "null");
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitleTextColor(Color.WHITE);
        mDrawerLayout = (FrameLayout) findViewById(R.id.drawer_layout);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new DrawerAdapter(TITLES, ICONS, NAME, EMAIL, PROFILE, this);
        mRecyclerView.setAdapter(mAdapter);
        RecyclerTouchHelper helper = new RecyclerTouchHelper(MainActivity.this);
        helper.setOnRecyclerClickListener(this);
        mRecyclerView.addOnItemTouchListener(helper);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDrawer = (DrawerLayout) findViewById(R.id.DrawerLayout);
        if (mDrawer != null) {
            ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawer, mToolbar, R.string.openDrawer, R.string.closeDrawer) {
                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    mAdapter.notifyDataSetChanged();
                }
            };
            mDrawer.addDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();
        }
        if (savedInstanceState == null) {
            handleDrawerActions(0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public boolean onClick(View child, int position) {
        if (mDrawer != null) {
            mDrawer.closeDrawers();
        }
        if (position == 0) {
            return true;
        } else {
            handleDrawerActions(position - 1);
            if (lastChild != null) {
                lastChild.setBackgroundColor(Color.parseColor("#111111"));
                TextView textView = (TextView) lastChild.findViewById(R.id.rowText);
                textView.setTextColor(Color.parseColor("#DDDDDD"));
                ImageView imageView = (ImageView) lastChild.findViewById(R.id.rowIcon);
                imageView.setImageResource(ICONS[childPos]);
            }
            child.setBackgroundColor(Color.parseColor("#222222"));
            TextView TV = (TextView) child.findViewById(R.id.rowText);
            TV.setTextColor(Color.parseColor("#FFFFFF"));
            lastChild = child;
            childPos = position - 1;
            return true;
        }
    }

    private void handleDrawerActions(int i) {
        Fragment frag = null;
        switch (i) {
            case 0:
                frag = new HomeFragment();
                mToolbar.setTitle(TITLES[i]);
                break;

            case 1:
                frag = new HistoryFragment();
                mToolbar.setTitle(TITLES[i]);
                break;

            case 2:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.label_logout));
                builder.setMessage(R.string.label_dialog_message);
                builder.setPositiveButton(R.string.dialog_label_positive, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        if (mGoogleApiClient.isConnected())
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                                @Override
                                public void onResult(@NonNull Status status) {
                                    if (status.isSuccess()) {
                                        getContentResolver().delete(
                                                QuestionContract.QuestionEntry.CONTENT_URI,
                                                null,
                                                null
                                        );
                                        startActivity(new Intent(MainActivity.this, SignInActivity.class));
                                        finish();
                                    }
                                }
                            });
                    }
                });
                builder.setNegativeButton(R.string.dialog_label_negative, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AppCompatDialog dialog = builder.create();
                dialog.setCancelable(false);
                dialog.show();
                break;
        }
        if (frag != null) {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.frame_container, frag);
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawer != null && mDrawerLayout != null && mDrawer.isDrawerOpen(mDrawerLayout)) {
            mDrawer.closeDrawer(mDrawerLayout);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
