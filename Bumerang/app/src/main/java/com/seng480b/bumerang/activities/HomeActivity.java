package com.seng480b.bumerang.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
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
import com.seng480b.bumerang.utils.Utility;
import com.seng480b.bumerang.utils.caching.UserDataCache;


public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Firebase Setup here.
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        // Initialize facebook SDK
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_nav_drawer);

        //toolbar/actionbar setup
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        String notifyReceived = getIntent().getStringExtra("MsgReceived");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    floatingClicked();
                }
            });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

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
                Fragment my_requests = new MyRequestsFragment();
                ft.addToBackStack("my_request");
                ft.replace(R.id.mainFrame, my_requests);
                ft.commit();
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
                logoutFromFacebook(false); // true = it manually logged out
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
            Log.e("ERROR","something went wrong with checking first login.");
            Intent login = new Intent(this, MainActivity.class);
            startActivity(login);
        }
        return false;
    }

    //will start up the browse fragment if is not the first time opening the app
    //will open the edit profile page if it is the first time.
    public void loadStartupFragment(boolean first){
         if (first) {
            // Hide the Floating action button
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            p.setAnchorId(View.NO_ID);
            fab.setLayoutParams(p);
            fab.setVisibility(View.GONE);

            //build intent for the first welcoming screen
             Intent welcome = new Intent(this, WelcomeActivity.class);
             startActivity(welcome);

        } else {
            updateProfileDeviceId();
            loadTabs();
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
        Fragment createReq = new CreateRequestFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack("create_request");
        ft.add(createReq, "create_request");
        ft.replace(R.id.mainFrame,createReq);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the HomeActivity/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setActionBarTitle(String title){
        if (getSupportActionBar().isShowing()){
            getSupportActionBar().setTitle(title);
        }

    }

    public void logoutFromFacebook(boolean manual){
        FacebookSdk.sdkInitialize(getApplicationContext());
        if (AccessToken.getCurrentAccessToken()==null){
            return; //already logged out
        }
        if(manual) {
            com.facebook.Profile p = com.facebook.Profile.getCurrentProfile();
            Utility.longToast(getApplicationContext(), getString(R.string.goodbye) +" "+ p.getFirstName()+".");
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment createReq = new CreateRequestFragment();

        Fragment my_requests = new MyRequestsFragment();

        Fragment profilePage = new ProfilePageFragment();

        if (id == R.id.nav_createReq) {
            // Hide the Floating action button
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            p.setAnchorId(View.NO_ID);
            fab.setLayoutParams(p);
            fab.setVisibility(View.GONE);
            // Transition to the create request fragment
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.addToBackStack("create_request");
            ft.replace(R.id.mainFrame,createReq);
            ft.commit();

        } else if (id == R.id.nav_home) {

            // this is just to fix a bug might be unneeded later
            // Show Floating action button
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            p.setAnchorId(View.NO_ID);
            fab.setLayoutParams(p);
            fab.setVisibility(View.VISIBLE);
            Intent browse = new Intent(this, HomeActivity.class);
            startActivity(browse);
            finish();
        } else if (id == R.id.nav_manage) {
            // Hide the Floating action button
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            p.setAnchorId(View.NO_ID);
            fab.setLayoutParams(p);
            fab.setVisibility(View.GONE);
            // Call the Profile Page Fragment
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.addToBackStack("profile");
            ft.replace(R.id.mainFrame,profilePage);
            ft.commit();
        } else if (id == R.id.nav_my_requests){
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setVisibility(View.VISIBLE);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.addToBackStack("my_requests");
            ft.replace(R.id.mainFrame, my_requests);
            ft.commit();
        } else if (id == R.id.nav_logout) {
            logoutFromFacebook(true); // true = it manually logged out
            //Go back to login page
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        }

        //close drawer after the action
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}