package com.teamruse.rarerare.tritontravel;
import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.teamruse.rarerare.tritontravel.SegmentFactory.TravelMode.WALKING;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, DirectionGeneratorListener {
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final String TAG = "Main_Activity";
    private String mOrigin = "";
    private String mDest = "";
    private Marker mOriginMarker;
    private Marker mDestMarker;
    private ArrayList<Marker> mMarkerList;
    private Button btnNavigation;
    private GoogleMap mMap;
    //private GoogleApiClient mGoogleApiClient;
    //private LocationRequest mLocationRequest;
    private Location mLastKnownLocation;
    //private Marker mLastKnownLocationMarker;
    private boolean mLocationPermissionGranted;
    private ArrayList<Polyline> mPolylines;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng mDefaultLatLng = new LatLng(41.881832, -87.623177);
    private int mDefaultZoom = 15;
    private ListView mDrawerList;
    private ArrayAdapter<String> mAdapter;

    //Shuyuan's update on map padding
    //private int map_top_padding = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnNavigation = (Button)findViewById(R.id.search_button);
        mDrawerList = (ListView)findViewById(R.id.navList);
        addDrawerItems();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //initialize autocompleteFragment bars
        PlaceAutocompleteFragment autocompleteFragmentOrigin = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_origin);
        PlaceAutocompleteFragment autocompleteFragmentDest = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_dest);

        //set autocompleteFragment backgrounds
        autocompleteFragmentOrigin.getView().setBackgroundColor(Color.WHITE);
        autocompleteFragmentDest.getView().setBackgroundColor(Color.WHITE);

        /*
        map_top_padding = autocompleteFragmentDest.getView().getHeight() +
                autocompleteFragmentOrigin.getView().getHeight();
        Log.i("padding", String.valueOf(map_top_padding));
        */
        //set onPlaceSelectedListener
        autocompleteFragmentOrigin.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if(mOriginMarker != null){
                    mOriginMarker.remove();
                }
                mOriginMarker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()));
                //mMarkerList.add(mOriginMarker);
                //updateCamera();
                //TODO: update camera to fit marker
                mOrigin = place.getAddress().toString();
                Log.d("input", mOrigin);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("selector", "An error occurred: " + status);
            }
        });

        autocompleteFragmentDest.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if(mDestMarker != null){
                    mDestMarker.remove();
                }
                mDestMarker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()));
                mDest = place.getAddress().toString();
                Log.d("input", mDest);
            }

            @Override
            public void onError(Status status) {
                Log.i("selector", "An error occurred: " + status);
            }
        });

        btnNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //navigation button callback
                sendRequest();
            }
        });

    }

    private void updateCamera(){
        LatLngBounds.Builder latLngBuilder = new LatLngBounds.Builder();
        for (Marker marker : mMarkerList) {
            latLngBuilder.include(marker.getPosition());
        }
        LatLngBounds bounds = latLngBuilder.build();
        //TODO: Change the padding as per needed
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 25,25,5);
        mMap.animateCamera(cameraUpdate);
    }


    private void sendRequest(){
        new DirectionGenerator(this, mOrigin, mDest).generate();
    }


    private void addDrawerItems(){
        String[] optionArray = {"Sign up/Log in", "History", "Peak Time", "Feedback", "FAQ"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, optionArray);
        mDrawerList.setAdapter(mAdapter);
    }


    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults){
        mLocationPermissionGranted = false;
        switch(requestCode){
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:{
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if(mMap == null){
            return;
        }
        try{
            if(mLocationPermissionGranted){
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setZoomGesturesEnabled(true);
            }
            else{
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e){
            Log.e("Exception %s", e.getMessage());
        }
    }

    private void getDeviceLocation(){
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            try {
                                CameraPosition cameraPosition = new CameraPosition(
                                        new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()),
                                        mDefaultZoom, mMap.getCameraPosition().tilt,
                                        mMap.getCameraPosition().bearing);
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
                                mMap.getUiSettings().setMyLocationButtonEnabled(true);

                            }catch (NullPointerException e){
                                Log.d(TAG, "Current location is null. Using defaults.");
                                Log.e(TAG, "Exception: %s", task.getException());
                                CameraPosition cameraPosition = new CameraPosition(
                                        mDefaultLatLng, mDefaultZoom, mMap.getCameraPosition().tilt,
                                        mMap.getCameraPosition().bearing);
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
                                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            CameraPosition cameraPosition = new CameraPosition(
                                    mDefaultLatLng, mDefaultZoom, mMap.getCameraPosition().tilt,
                                    mMap.getCameraPosition().bearing);
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e){
            Log.e("Exception :%s", e.getMessage());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap = googleMap;
        updateLocationUI();
        getDeviceLocation();
        updateLocationUI();
        mMap.setPadding(0,350,0,0);
    }

    @Override
    public void onGenerateStart() {
        btnNavigation.setEnabled(false);
    }

    @Override
    public void onGenerateSuccess(List<Path> paths) {
        mPolylines = new ArrayList<>();
        btnNavigation.setEnabled(true);
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
            mPolylines.add(mMap.addPolyline(polylineOptions));
        }
    }
}
