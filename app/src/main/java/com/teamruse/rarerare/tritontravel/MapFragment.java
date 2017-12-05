package com.teamruse.rarerare.tritontravel;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Activity;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.Toast;

import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlacesOptions;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.whalemare.sheetmenu.SheetMenu;

/**
 * Author: Shuyuan Ma
 * Date: 2017/11/27
 * Description: Rewrite the original MainActivity of the map and two autocomplete search bars into
 *              fragments so that it works well inside Zijing's navigation desgin
 */

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */

public class MapFragment extends Fragment implements OnMapReadyCallback,
                                                     GoogleMap.OnMyLocationButtonClickListener {
    // TODO:parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final String TAG = "Map_Fragment";
    // TODO: parameters
    private String mOrigin = "";
    private String mDest = "";
    private Marker mOriginMarker;
    private Marker mDestMarker;
    //private Button btnNavigation;
    //private GoogleMap mMap;

    public Button btnNavigation;
    public  GoogleMap mMap;

    private SupportPlaceAutocompleteFragment autocompleteFragmentOrigin;
    private SupportPlaceAutocompleteFragment autocompleteFragmentDest;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastKnownLocation;
    private Marker mLastKnownLocationMarker;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng mDefaultLatLng = new LatLng(32.880088,  -117.234003);
    //new LatLng(32.879409, -117.2389395);
    private int mDefaultZoom = 15;
    private LatLngBounds.Builder builder;
    private LatLngBounds bounds;
    private ArrayList<Polyline> mPolylines;

    View mapView;

    private OnFragmentInteractionListener mListener;

    private LatLng currLatLng;
    private Place destPlace;

    //private LinearLayout routeBottomSheet;
    //private BottomSheetBehavior routeBottomSheetBehavior;
    private TextView routeText;
    private TextView originText;
    private TextView destText;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putInt("curChoice", mCurCheckPosition);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mPolylines = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mapView = inflater.inflate(R.layout.map_frag_test, container, false);
        return mapView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.fragMap);
        mapFragment.getMapAsync(this);

        //initialize autocompleteFragment bars
        autocompleteFragmentOrigin = (SupportPlaceAutocompleteFragment)
                getChildFragmentManager().findFragmentById(R.id.place_autocomplete_frag_origin);
        autocompleteFragmentDest = (SupportPlaceAutocompleteFragment)
                getChildFragmentManager().findFragmentById(R.id.place_autocomplete_frag_dest);
        autocompleteFragmentOrigin.setHint("From");
        autocompleteFragmentDest.setHint("To");

        //set onPlaceSelectedListener
        autocompleteFragmentOrigin.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                builder = new LatLngBounds.Builder();
                if(mDestMarker != null) {
                    builder.include(mDestMarker.getPosition());
                }
                if (mOriginMarker != null) {
                    mOriginMarker.remove();
                }
                mOriginMarker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mOriginMarker.setTitle(place.getName().toString());
                //mOrigin = place.getAddress().toString();
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker arg0) {
                        if (arg0.equals(mDestMarker)){
                            showMenu(mDestMarker);
                            return true;
                        }
                        else if (arg0.equals(mOriginMarker)) {// if marker source is clicked
                            showMenu(mOriginMarker);
                            return true;
                        }
                        //Toast.makeText(getContext(), "l",Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });
                mOrigin = place.getId();
                Log.i(TAG, "origin seleted: " + place.getAddress().toString());
                Log.i(TAG, "\tId: " + mOrigin);

                mOrigin = place.getAddress().toString();
                //mOrigin = place.getId();
                Log.i(TAG, "origin seleted, Name: " + place.getName());
                Log.i(TAG, "\t\tId: " + place.getId());
                Log.i(TAG, "\t\tAddress: "+ place.getAddress().toString());

                builder.include(mOriginMarker.getPosition());
                bounds = builder.build();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                if (mDestMarker!= null) {
                    int padding = 300; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.animateCamera(cu);
                }
            }
            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "onError(). An error occurred: " + status);
            }
        });

        autocompleteFragmentOrigin.getView().findViewById(R.id.place_autocomplete_clear_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // example : way to access view from PlaceAutoCompleteFragment
                        // ((EditText) autocompleteFragment.getView()
                        // .findViewById(R.id.place_autocomplete_search_input)).setText("");
                        autocompleteFragmentOrigin.setText("");
                        view.setVisibility(View.GONE);
                        if (mOriginMarker != null)
                            mOriginMarker.remove();
                    }
                });

        autocompleteFragmentDest.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onPlaceSelected(Place place) {
                builder = new LatLngBounds.Builder();
                if(mOriginMarker != null)
                    builder.include(mOriginMarker.getPosition());
                if (mDestMarker != null) {
                    mDestMarker.remove();
                }
                if ( History.stopsList == null) {
                    History.stopsList = new ArrayList<>();
                }
                //DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd | hh:mm");
                Date date = new Date();
                (new StopHistoryBaseHelper(getActivity().getApplicationContext()))
                        .writeStopHistory( new StopHistory(place.getName().toString()
                        , date.getTime(), place.getId()));

                mDestMarker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                mDestMarker.setTitle(place.getName().toString());
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker arg0) {
                        if (arg0.equals(mDestMarker)){
                            showMenu(mDestMarker);
                            return true;
                        }
                        else if (arg0.equals(mOriginMarker)) {// if marker source is clicked
                            showMenu(mOriginMarker);
                            return true;
                        }
                        //Toast.makeText(getContext(), "l",Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });

                mDest = place.getAddress().toString();
                //mDest = place.getId();

                Log.i(TAG, "destination seleted, Name: " + place.getName());
                Log.i(TAG, "\t\tId: " + place.getId());
                Log.i(TAG, "\t\tAddress: "+ place.getAddress().toString());

                builder.include(mDestMarker.getPosition());
                bounds = builder.build();
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                if (mOriginMarker != null) {
                    int padding = 300; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.animateCamera(cu);
                }
            }
            @Override
            public void onError(Status status) {
                //TODO:handle Error
                Log.e(TAG, status.getStatusMessage());
            }
        });

        autocompleteFragmentDest.getView().findViewById(R.id.place_autocomplete_clear_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // example : way to access view from PlaceAutoCompleteFragment
                        // ((EditText) autocompleteFragment.getView()
                        // .findViewById(R.id.place_autocomplete_search_input)).setText("");
                        autocompleteFragmentDest.setText("");
                        view.setVisibility(View.GONE);
                        if (mDestMarker != null)
                            mDestMarker.remove();
                    }
                });

        //set autocompleteFragment backgrounds
        autocompleteFragmentOrigin.getView().setBackgroundColor(Color.WHITE);
        autocompleteFragmentDest.getView().setBackgroundColor(Color.WHITE);

        btnNavigation = (Button) mapView.findViewById(R.id.search_button);
        btnNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: navigation button callback
                //sendRequest();
                if(mDestMarker != null && mOriginMarker !=null) {
                    mOriginMarker.setVisible(true);
                    builder = new LatLngBounds.Builder();
                    builder.include(mOriginMarker.getPosition());
                    builder.include(mDestMarker.getPosition());
                    bounds = builder.build();
                    int padding = 300; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.animateCamera(cu);
                    //showRoutes(mOriginMarker, mDestMarker);

                }
                //navigation button callback to onFragmentInteraction
                //MainActivity will handle the sendRequest() part
                if(mListener != null) {
                    mListener.onNavRequest(mOrigin, mDest);
                }
            }
        });
        fillInOriginSearchBox();
        if(destPlace!=null){
            fillInDestSearchBox(destPlace);
        }
        /*routeBottomSheet=getView().findViewById(R.id.route_bottom_sheet);
        routeBottomSheetBehavior = BottomSheetBehavior.from(routeBottomSheet);
        routeBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        Button hideRouteButt=getView().findViewById(R.id.hide_route_bottom_sheet_butt);
        hideRouteButt.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        routeBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                }
        );
        routeText=getView().findViewById(R.id.basic_route_text);
        originText=getView().findViewById(R.id.basic_origin_text);
        destText=getView().findViewById(R.id.basic_dest_text);*/
    }

    private void showMenu(Marker m) {
        SheetMenu.with(getContext()).setTitle(m.getTitle()).setMenu(R.menu.sheet_menu)
                .setClick(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        /*if (item.getItemId() == R.id.bus) {
                            Toast.makeText(getContext(),"fetching route", Toast.LENGTH_SHORT).show();
                            return true;
                        }*/
                        if (item.getItemId() == R.id.schedule) {
                            Toast.makeText(getContext(),"fetching schedule", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        else if (item.getItemId() == R.id.saveStop) {
                            Toast.makeText(getContext(),"saving", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        return false;
                    }
                }).show();
    }

    private void showRoutes(Marker o,Marker d) {
        SheetMenu.with(getContext()).setTitle(o.getTitle() + " > " + d.getTitle()).setMenu(R.menu.routes)
                .setClick(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.r1) {
                            Toast.makeText(getContext(),"fetching route 1", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        else if (item.getItemId() == R.id.r2) {
                            Toast.makeText(getContext(),"fetching route 2", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        else if (item.getItemId() == R.id.saveStop) {
                            Toast.makeText(getContext(),"fetching route 3", Toast.LENGTH_SHORT).show();
                            return true;
                        }
                        return false;
                    }
                }).show();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
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
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            mLastKnownLocation = task.getResult();
                            try {
                                currLatLng=new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                                CameraPosition cameraPosition = new CameraPosition(
                                        currLatLng,
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
            Log.e(TAG,"Exception :"+e.getMessage());
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
        mMap.setOnMyLocationButtonClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        Log.i(TAG, "onDetach");
    }

    //click listener to navigation button
    //now moved in to MainActivity
    /*
    private void sendRequest(){
        new DirectionGenerator(this, mOrigin, mDest).generate();
        Log.i(TAG, "sendRequest() called");
        Log.i(TAG, "mOrigin:"+mOrigin+" mDest:"+mDest);
    }
    */

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: implement this interface in case we need to communicate with the activity
        void onNavRequest(String origin, String dest);
    }
    @Override
    public boolean onMyLocationButtonClick() {
        fillInOriginSearchBox();
        return false;
    }

    private void fillInOriginSearchBox(){
        PlaceDetectionClient mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        /*
         * Ruoyu Xu
         * Set text in origin textbox to current location
         */
        Task<PlaceLikelihoodBufferResponse> placeResult = mPlaceDetectionClient.getCurrentPlace(null);
        placeResult.addOnCompleteListener(
                new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                        try{
                            PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();
                            Log.d(TAG, "got likely places");
                            Place mostLikelyPlace=likelyPlaces.get(0).getPlace();
                            autocompleteFragmentOrigin.setText(mostLikelyPlace.getAddress().toString());

                            mOrigin=mostLikelyPlace.getId();
                            mOriginMarker = mMap.addMarker(new MarkerOptions().position(mostLikelyPlace.getLatLng()).
                                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                            mOriginMarker.setVisible(false);
                            mOrigin = mostLikelyPlace.getAddress().toString();
                            Log.i(TAG, "origin seleted" + mOrigin);
                            likelyPlaces.release();
                        }catch (Exception e){
                            Log.d(TAG,"exception when setting text in origin textbox to current location:"+e.getMessage());
                        }
                    }
                }
        );
    }

    @Override
    public void onPause(){
        super.onPause();

        Log.d(TAG, "onPause called");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG, "onStop called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
    }

    protected void setDestPlace(Place place){
        PlaceDetectionClient mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);
        /*
         * Ruoyu Xu
         * Set text in destination textbox
         */
        this.destPlace=place;
        try {
            fillInDestSearchBox(place);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void fillInDestSearchBox(Place place){
        builder = new LatLngBounds.Builder();
        if(mOriginMarker != null)
            builder.include(mOriginMarker.getPosition());
        mDest=place.getId();
        autocompleteFragmentDest.setText(place.getAddress().toString());
        mDestMarker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mDest = place.getAddress().toString();
        Log.i(TAG, "destination seleted: " + place.getAddress().toString());
        builder.include(mDestMarker.getPosition());
        bounds = builder.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        if (mOriginMarker != null) {
            int padding = 300; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cu);
        }
        Log.i(TAG, "destination seleted" + mDest);
    }

    /*
     * Ruoyu Xu
     * Display path information on a bottomSheet
     */
    protected void displayPath(Path path){
        //originText.setText(mOrigin);
        //destText.setText(mDest+"\n");
        String basicPathStr="";
        for (PathSegment seg:path.getPathSegments()){
            if (seg.getTravelMode()==SegmentFactory.TravelMode.WALKING){
                basicPathStr+="Walk"+seg.getDistance()+"\n";
            }else if(seg.getTravelMode()==SegmentFactory.TravelMode.BUS){
                seg=(BusSegment)seg;
                basicPathStr+="Bus"+((BusSegment) seg).getBusName();
            }
        }
        //routeText.setText(basicPathStr);
        //routeBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        Log.d(TAG, "basicPathStr:\n"+basicPathStr);
    }

    public void drawPolylines(PolylineOptions polylineOptions){
        mMap.addPolyline(polylineOptions);
    }
}
