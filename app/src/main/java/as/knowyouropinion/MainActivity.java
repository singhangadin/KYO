package as.knowyouropinion;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements
        RecyclerView.OnItemTouchListener,
        GoogleApiClient.OnConnectionFailedListener{

    private String TITLES[] = {"Home", "My Answers", "About", "Logout"};
    private int ICONS[] = { R.drawable.ic_action_home, R.drawable.ic_action_answered,
                            R.drawable.ic_action_about, R.drawable.ic_action_log_out};

    private DrawerLayout Drawer;
    private RecyclerView mRecyclerView;
    private GestureDetector mGestureDetector;
    private View lastChild = null;
    private int childPos = 0;
    private boolean doubleBackToExitPressedOnce = false;
    private RecyclerView.Adapter mAdapter;
    private GoogleApiClient mGoogleApiClient;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(BuildConfig.OAUTH_CLIENT_ID)
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(this);
        String NAME=preferences.getString("NAME","Name");
        String EMAIL=preferences.getString("EMAIL","Email");
        String PROFILE=preferences.getString("PHOTO","null");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Log.e("TAG", PROFILE);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new DrawerAdapter(TITLES, ICONS, NAME, EMAIL, PROFILE, this);
        mRecyclerView.setAdapter(mAdapter);
        mGestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
        mRecyclerView.addOnItemTouchListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Drawer = (DrawerLayout) findViewById(R.id.DrawerLayout);
        if(Drawer!=null) {
            ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, Drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {
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
            Drawer.addDrawerListener(mDrawerToggle);
            mDrawerToggle.syncState();
        }
        if (savedInstanceState == null) {
            handleDrawerActions(0);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mGoogleApiClient!=null)
        {   mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient!=null&&mGoogleApiClient.isConnected())
        {   mGoogleApiClient.disconnect();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent e) {
        View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if (child != null && mGestureDetector.onTouchEvent(e)) {
            if(Drawer!=null) {
                Drawer.closeDrawers();
            }
            int position = recyclerView.getChildAdapterPosition(child);
            if (position == 0) {
                return true;
            } else {
                handleDrawerActions(position - 1);
                if (lastChild != null) {
                    lastChild.setBackgroundColor(Color.parseColor("#111111"));
                    TextView TV = (TextView) lastChild.findViewById(R.id.rowText);
                    TV.setTextColor(Color.parseColor("#DDDDDD"));
                    ImageView Img = (ImageView) lastChild.findViewById(R.id.rowIcon);
                    Img.setImageResource(ICONS[childPos]);
                }
                child.setBackgroundColor(Color.parseColor("#222222"));
                TextView TV = (TextView) child.findViewById(R.id.rowText);
                TV.setTextColor(Color.parseColor("#FFFFFF"));
                lastChild = child;
                childPos = position - 1;
                return true;
            }
        }
        return false;
    }

    private void handleDrawerActions(int i) {
        Fragment frag=null;
        switch (i)
        {   case 0: frag=new HomeFragment();
                    break;

            case 1: frag=new HistoryFragment();
                    break;

            case 2: frag=new AboutFragment();
                    break;

            case 3: FirebaseAuth.getInstance().signOut();
                    if(mGoogleApiClient.isConnected())
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if(status.isSuccess())
                            {   startActivity(new Intent(MainActivity.this, SignInActivity.class));
                                finish();
                            }
                        }
                    });
                    break;
        }
        if(frag!=null)
        {   FragmentManager manager=getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.frame_container,frag);
            transaction.commit();
        }
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    @Override
    public void onBackPressed() {
        if (Drawer!=null&&Drawer.isDrawerOpen(mRecyclerView)) {
            Drawer.closeDrawer(mRecyclerView);
        }
        else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 2000);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
