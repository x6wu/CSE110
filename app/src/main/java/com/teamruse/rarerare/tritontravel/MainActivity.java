package com.teamruse.rarerare.tritontravel;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.teamruse.rarerare.tritontravel.SegmentFactory.TravelMode.WALKING;


public class MainActivity extends AppCompatActivity implements MapFragment.OnFragmentInteractionListener,
                   NavigationView.OnNavigationItemSelectedListener,
                   DirectionGeneratorListener {
    private static String TAG = "Main_Activity";
    private MapFragment mMapFragment;
    private String currFragTag;


    /*
     *Ruoyu Xu
     * Test checking login in MainActivity
     *
     */
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (signedIn()) {
            setContentView(R.layout.activity_main_signed_in);
        }
        else {
            setContentView(R.layout.activity_main);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer;

        if (signedIn()) {
            drawer = (DrawerLayout) findViewById(R.id.drawer_layout_signed_in);
        }
        else {
            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        }
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

        //Ruoyu Xu test signin in MainActivity
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer;
        if (signedIn()) {
            drawer = (DrawerLayout) findViewById(R.id.drawer_layout_signed_in);
        }
        else {
            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }


    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

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
        mMapFragment.btnNavigation.setEnabled(false);
    }

    @Override
    public void onGenerateSuccess(List<Path> paths) {
        mMapFragment.btnNavigation.setEnabled(true);
        ArrayList<PathSegment> recPathSegments = paths.get(0).getPathSegments();
        for(int i = 0; i < recPathSegments.size(); ++i){
            PathSegment currSegment = recPathSegments.get(i);
            Log.d("travel mode", currSegment.getTravelMode().toString());
            Log.d("polyline", currSegment.getEncodedPolyLine());
            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(currSegment.getPolyLine())
                    .color(ContextCompat.getColor(getApplicationContext(),R.color.blue));
            //set polyline pattern to be dotted if travel mode is walking
            if(currSegment.getTravelMode() == WALKING){
                List<PatternItem> patternItemList = new ArrayList<>();
                patternItemList.add(new Dot());
                polylineOptions.pattern(patternItemList);
            }
            mMapFragment.drawPolylines(polylineOptions);
        }
        if (paths.isEmpty()){
            return;
        }
        mMapFragment.displayPath(paths.get(0));
    }

    //TODO
    //check if user is signed in
    public boolean signedIn(){
        /*
         * Ruoyu Xu check user login in MainActivity
         *
         */
        if (mAuth==null){
            Log.d(TAG, "mAuth is null");
            return false;

        }
        if (mAuth.getCurrentUser()!=null){
            Log.d(TAG, "really logged in");
            return true;
        }
        Log.d(TAG, "currUser is null");
        return false;

    }

    /*
     * Ruoyu Xu
     * Switch the fragment in the fragment_container
     */
    protected void switchFrag(int id){
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currFrag=fragmentManager.findFragmentByTag(currFragTag);
        DrawerLayout drawer;
        if (signedIn())
            drawer = (DrawerLayout) findViewById(R.id.drawer_layout_signed_in);
        else
            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        String destFragTag="";
        Class destFragClass=null;
        switch (id){
            case R.id.login:
                destFragTag="login";
                destFragClass=login.class;
                break;
            case R.id.history:
                destFragTag="history";
                destFragClass=History.class;
                break;
            case R.id.fb:
                destFragTag="fb";
                destFragClass=Feedback.class;
                break;
            case R.id.faq:
                destFragTag="faq";
                destFragClass=Faq.class;
                break;
            case R.id.back:
                destFragTag="map";
                destFragClass=MapFragment.class;
                break;
            case R.id.saved:
                destFragTag="saved";
                destFragClass=Saved.class;
                break;
        }
        if (currFragTag.equals(destFragTag)){
            drawer.closeDrawer(GravityCompat.START);
            return;
        }
        if (destFragTag.equals("map")){
            if (mMapFragment==null){
                mMapFragment=new MapFragment();
                Log.d(TAG, "new MapFragment");
                fragmentManager.beginTransaction().add(R.id.fragment_container, mMapFragment, "map").commit();
            }
            Log.d(TAG, "replace to map frag");
            fragmentManager.beginTransaction().replace(R.id.fragment_container, mMapFragment).commit();
            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("map")).commit();
            currFragTag="map";
        }else if(fragmentManager.findFragmentByTag(destFragTag) != null) {
            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(destFragTag)).commit();
        } else {
            try {
                Log.d(TAG, "frag name:"+destFragTag.toString());
                //Log.d(TAG, "frag name:"+)
                fragmentManager.beginTransaction().add(R.id.fragment_container
                        , (Fragment)destFragClass.newInstance(), destFragTag).commit();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        if(currFrag != null){
            fragmentManager.beginTransaction().hide(currFrag).commit();
            if (currFragTag!="map"){
                Log.d(TAG, "remove "+currFragTag);
                fragmentManager.beginTransaction().remove(currFrag).commit();
            }
        }
        currFragTag=destFragTag;
        drawer.closeDrawer(GravityCompat.START);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
    }

    protected void goToStop(String placeId){
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (mMapFragment==null){
            mMapFragment=new MapFragment();
            Log.d(TAG, "new MapFragment");
            fragmentManager.beginTransaction().add(R.id.fragment_container, mMapFragment, "map").commit();
        }
        Log.d(TAG, "replace to map frag");
        fragmentManager.beginTransaction().replace(R.id.fragment_container, mMapFragment).commit();
        fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag("map")).commit();
        (Places.getGeoDataClient(this,null)).getPlaceById(placeId)
                .addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                        if (task.isSuccessful()) {
                            PlaceBufferResponse places = task.getResult();
                            Place myPlace = places.get(0);
                            mMapFragment.setDestPlace(myPlace);
                            Log.i(TAG, "Place found: " + myPlace.getName());
                            places.release();
                        } else {
                            Log.e(TAG, "Place not found.");
                        }
                    }
                });
        //mMapFragment.setDestPlace((Places.getGeoDataClient(this,null)).getPlaceById(placeId).getResult().get(0));
        currFragTag="map";
    }
}
