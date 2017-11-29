package com.teamruse.rarerare.tritontravel;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
        manager.beginTransaction().replace(R.id.fragment_container, new MapFragment())
                .commit();
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
        FragmentManager fragmentManager = getSupportFragmentManager();

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
        */
        } else if (id == R.id.fb) {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, new Feedback())
                    .commit();

        } else if (id == R.id.faq) {
            fragmentManager.beginTransaction().replace(R.id.fragment_container, new Faq())
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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
}
