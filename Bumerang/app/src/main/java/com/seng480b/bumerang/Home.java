package com.seng480b.bumerang;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;


public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FloatingActionButton fab;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    //ViewPager hosts section contents
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);
        //toolbar/actionbar setup
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        //Fragment Browse --> main page
        /*ListFragment browse = new Browse();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainFrame,browse);
        ft.commit();*/


        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floatingClicked();
            }
        });


        //put logic here that will check if they have an account or not
        loadStartupFragment(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    //will start up the browse fragment if is not the first time opening the app
    //will open the edit profile page if it is the first time.
    public void loadStartupFragment(boolean first){

        if(first){
            // Hide the Floating action button
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            p.setAnchorId(View.NO_ID);
            fab.setLayoutParams(p);
            fab.setVisibility(View.GONE);
            // Call the Edit Profile Fragment
            Fragment editProfileFragment = new EditProfileFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame,editProfileFragment);
            ft.commit();
        }else{
            ListFragment browse = new Browse();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame,browse);
            ft.commit();

        }


    }

    public void floatingClicked() {
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
        p.setAnchorId(View.NO_ID);
        fab.setLayoutParams(p);
        fab.setVisibility(View.GONE);
        Fragment createReq = new CreateRequest();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
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
        // automatically handle clicks on the Home/Up button, so long
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment editProfile = new EditProfileFragment();
        Fragment fragment2 = new TestFragment();

        Fragment createReq = new CreateRequest();
        //ListFragment browse = new Browse();
        Fragment my_requests = new MyRequests();

        Fragment profilePage = new ProfilePage();

        Fragment browse = new Browse();


        if (id == R.id.nav_createReq) {
            // Hide the Floating action button
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            p.setAnchorId(View.NO_ID);
            fab.setLayoutParams(p);
            fab.setVisibility(View.GONE);
            // Transition to the create request fragment
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame,createReq);
            ft.commit();

        } else if (id == R.id.nav_home) {

            // this is just to fix a bug might be unneeded later
            // Hide the Floating action button
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            p.setAnchorId(View.NO_ID);
            fab.setLayoutParams(p);
            fab.setVisibility(View.VISIBLE);
            // Call the Edit Profile Fragment
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, browse);
            ft.commit();


            //Intent reload = new Intent(this, Home.class );
            //startActivity(reload);

        } else if (id == R.id.nav_manage) {
            // Hide the Floating action button
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            p.setAnchorId(View.NO_ID);
            fab.setLayoutParams(p);
            fab.setVisibility(View.GONE);
            // Call the Profile Page Fragment
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame,profilePage);
            ft.commit();
        } else if (id == R.id.nav_my_requests){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.mainFrame, my_requests);
            ft.commit();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        //close drawer after the action
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}