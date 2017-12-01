package com.teamruse.rarerare.tritontravel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;


public class MainActivity extends AppCompatActivity
        implements MapFragment.OnFragmentInteractionListener,
                   NavigationView.OnNavigationItemSelectedListener,
                   DirectionGeneratorListener {

    private static String TAG = "Main_Activity";

    private MapFragment mMapFragment;
    private String currFragTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager manager = getSupportFragmentManager();
        mMapFragment=new MapFragment();

        /*manager.beginTransaction().replace(R.id.fragment_container, mMapFragment)
                .commit();*/
        manager.beginTransaction().add(R.id.fragment_container, mMapFragment, "map").commit();
        currFragTag="map";
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
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        /*FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.login) {
            // Handle the camera action
            //fragmentManager.beginTransaction().replace(R.id.content_frame, new login())
            //        .commit();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, new login())
                    .commit();
        } else if (id == R.id.history) {
            // Handle the camera action
            fragmentManager.beginTransaction().replace(R.id.fragment_container, new History())
                    .commit();
        /*
        } else if (id == R.id.pt) {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, new Peaktime())
                    .commit();
        *//*
        } else if (id == R.id.fb) {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, new Feedback())
                    .commit();

        } else if (id == R.id.faq) {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, new Faq())
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);*/
        switchFrag(id);
        return true;
    }

    //Origin and Dest are strings sent from MapFragment
    //This used to be SendRequest()
    @Override
    public void onNavRequest(String origin, String dest) {
        new DirectionGenerator(this, origin, dest).generate();
        Log.i(TAG, "sendRequest() called");
        Log.i(TAG, "mOrigin:" + origin + " mDest:" + dest);
    }


    @Override
    public void onGenerateStart() {
        //TODO
    }

    @Override
    public void onGenerateSuccess(List<Path> paths) {
        //TODO
    }


    protected void switchFrag(int id){

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currFrag=fragmentManager.findFragmentByTag(currFragTag);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (id == R.id.login) {
            if (currFragTag=="login"){
                drawer.closeDrawer(GravityCompat.START);
                return;
            }
            if(fragmentManager.findFragmentByTag("login") != null) {
                fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("login")).commit();
            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_container, new login(), "login").commit();
            }
            if(currFrag != null){
                fragmentManager.beginTransaction().hide(currFrag).commit();

                if (currFragTag!="map"){
                    Log.d(TAG, "remove "+currFragTag);
                    fragmentManager.beginTransaction().remove(currFrag).commit();
                }
            }
            currFragTag="login";
        } else if (id == R.id.history) {
            if (currFragTag=="history"){
                drawer.closeDrawer(GravityCompat.START);
                return;
            }
            if(fragmentManager.findFragmentByTag("history") != null) {
                fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("history")).commit();
            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_container, new History(), "history").commit();
            }
            if (currFrag==null){
                Log.d(TAG, "curr null");
            }
            if(currFrag != null){
                fragmentManager.beginTransaction().hide(currFrag).commit();
                Log.d(TAG, "hide curr");
                if (currFragTag=="map"){
                    Log.d(TAG, "curr tag is map");
                }
                if (currFragTag!="map"){
                    Log.d(TAG, "remove "+currFragTag);
                    fragmentManager.beginTransaction().remove(currFrag).commit();
                }
            }
            currFragTag="history";
        /*
        } else if (id == R.id.pt) {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, new Peaktime())
                    .commit();
        */
        } else if (id == R.id.fb) {
            if (currFragTag=="fb"){
                drawer.closeDrawer(GravityCompat.START);
                return;
            }
            if(fragmentManager.findFragmentByTag("fb") != null) {
                fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("fb")).commit();
            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_container, new Feedback(), "fb").commit();
            }
            if(currFrag != null){
                fragmentManager.beginTransaction().hide(currFrag).commit();
                if (currFrag!=mMapFragment){
                    fragmentManager.beginTransaction().remove(currFrag).commit();
                }
            }
            currFragTag="fb";
        } else if (id == R.id.faq) {
            if (currFragTag=="faq"){
                drawer.closeDrawer(GravityCompat.START);
                return;
            }
            if(fragmentManager.findFragmentByTag("faq") != null) {
                fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("faq")).commit();
            } else {
                fragmentManager.beginTransaction().add(R.id.fragment_container, new Faq(), "faq").commit();
            }
            if(currFrag != null){
                fragmentManager.beginTransaction().hide(currFrag).commit();
                if (currFrag!=mMapFragment){
                    fragmentManager.beginTransaction().remove(currFrag).commit();
                }
            }
            currFragTag="faq";
        } else if(id==R.id.back){

            if (mMapFragment==null){
                mMapFragment=new MapFragment();
                Log.d(TAG, "new MapFragment");
                fragmentManager.beginTransaction().add(R.id.fragment_container, mMapFragment, "map").commit();
            }
            Log.d(TAG, "replace to map frag");
            fragmentManager.beginTransaction().replace(R.id.fragment_container, mMapFragment).commit();
            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("map")).commit();
            currFragTag="map";

        }


        drawer.closeDrawer(GravityCompat.START);
    }
    @Override
    public void onDestroy() {

        super.onDestroy();
        Log.d(TAG, "onDestroy called");
    }

}
