package com.seng480b.bumerang.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import com.google.firebase.messaging.FirebaseMessaging;
import com.seng480b.bumerang.fragments.MyRequestsFragment;
import com.seng480b.bumerang.fragments.ProfilePageFragment;
import com.seng480b.bumerang.utils.ProfileUtility;
import com.seng480b.bumerang.R;
import com.seng480b.bumerang.adapters.SectionsPagerAdapter;
import com.seng480b.bumerang.fragments.CreateRequestFragment;
import com.seng480b.bumerang.fragments.EditProfileFragment;
import com.seng480b.bumerang.utils.Utility;
import com.seng480b.bumerang.utils.caching.UserDataCache;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private FloatingActionButton fab;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Firebase setup
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        // Initialize facebook SDK
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_nav_drawer);

        // Toolbar/Actionbar setup
        setupToolbar();

        String notifyReceived = getIntent().getStringExtra("MsgReceived");

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    floatingClicked();
                }
            });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // isFirst is used to both check if the user has ever logged in
        // And to set the current user profile if launched from a notification.
        if (notifyReceived != null) {
            boolean loggedIn = AccessToken.getCurrentAccessToken()!=null;
            // Ensure user is logged into the app. Otherwise launch main page.
            if (loggedIn) {
                checkFirstLogin();
                updateProfileDeviceId();
                Fragment myRequests = new MyRequestsFragment();
                replaceMainFrameWithFragment(myRequests, "my_requests");
            } else {
                Intent login = new Intent(this, MainActivity.class);
                startActivity(login);
            }
        } else {
            // If user hasn't logged in before, redirect them to profile edit page
            if (com.facebook.Profile.getCurrentProfile() != null ) {
                boolean isFirst = checkFirstLogin();
                loadStartupFragment(isFirst);
            } else {
                Utility.longToast(this, getString(R.string.error_message));
                // Redirect to login screen.
                logoutFromFacebook();
                Intent login = new Intent(this, MainActivity.class);
                startActivity(login);
            }
        }

    }

    public boolean checkFirstLogin() {
        try {
            return ProfileUtility.isFirstLogin(com.facebook.Profile.getCurrentProfile());
        } catch (Exception e) {
            // Something went wrong send back to login page.
            Utility.longToast(this, getString(R.string.error_message));
            Intent login = new Intent(this, MainActivity.class);
            startActivity(login);
        }
        return false;
    }

    //will start up the browse fragment if is not the first time opening the app
    //will open the edit profile page if it is the first time.
    public void loadStartupFragment(boolean first){
         if (first) {
             setFabVisibility(false);
             Intent welcome = new Intent(this, WelcomeActivity.class);
             startActivity(welcome);
        } else {
            updateProfileDeviceId();
            loadTabs();
        }
    }

    public void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawerToggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(drawerToggle);
            drawerToggle.syncState();
        }
    }

    public void loadTabs(){
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    public void floatingClicked() {
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        p.setAnchorId(View.NO_ID);
        fab.setLayoutParams(p);
        fab.setVisibility(View.GONE);
        Fragment createRequest = new CreateRequestFragment();
        replaceMainFrameWithFragment(createRequest, "create_request");
    }

    @Override
    public void onBackPressed() {
            // turn on the Navigation Drawer image;
            // this is called in the LowerLevelFragments

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            FragmentManager ft = getSupportFragmentManager();
            if (ft.findFragmentById(R.id.tabs) == null && ft.getBackStackEntryCount() == 0) {
                Intent browse = new Intent(this, HomeActivity.class);
                startActivity(browse);
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d("DEBUG", "home");
                return false;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setActionBarTitle(String title){
        if (getSupportActionBar().isShowing()){
            getSupportActionBar().setTitle(title);
        }

    }

    public void logoutFromFacebook(){
        FacebookSdk.sdkInitialize(getApplicationContext());
        if (AccessToken.getCurrentAccessToken()==null){
            return; //already logged out
        }
        LoginManager.getInstance().logOut();
    }

    public void updateProfileDeviceId() {
        if (UserDataCache.getCurrentUser() != null) {
            ProfileUtility.updateDeviceId();
        } else {
            Log.e("ERROR", " User profile does not exist");
        }
    }

    public void setFabVisibility(boolean isVisible) {
        CoordinatorLayout.LayoutParams fabLayout = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        fabLayout.setAnchorId(View.NO_ID);
        fab.setLayoutParams(fabLayout);
        if (isVisible) {
            fab.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.GONE);
        }
    }

    private void replaceMainFrameWithFragment(Fragment fragment, String tag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack(tag);
        ft.replace(R.id.mainFrame, fragment);
        ft.commit();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        Fragment createRequest = new CreateRequestFragment();

        Fragment myRequests = new MyRequestsFragment();

        Fragment profilePage = new ProfilePageFragment();

        switch (item.getItemId()) {
            case R.id.nav_createReq:
                setFabVisibility(false);
                // Transition to the create request fragment
                replaceMainFrameWithFragment(createRequest, "create_request");
                break;
            case R.id.nav_home:
                setFabVisibility(true);
                Intent browse = new Intent(this, HomeActivity.class);
                startActivity(browse);
                finish();
                break;
            case R.id.nav_manage:
                setFabVisibility(false);
                replaceMainFrameWithFragment(profilePage, "profile");
                break;
            case R.id.nav_my_requests:
                setFabVisibility(true);
                replaceMainFrameWithFragment(myRequests, "my_requests");
                break;
            case R.id.nav_logout:
                logoutFromFacebook();
                //Go back to login page
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        //close drawer after the action
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}