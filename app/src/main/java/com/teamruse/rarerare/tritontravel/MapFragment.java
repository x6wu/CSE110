package com.teamruse.rarerare.tritontravel;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

import java.util.List;

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
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback, DirectionGeneratorListener {
    // TODO:parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final String TAG = "Map_Fragment";
    // TODO: parameters
    private String mOrigin = "";
    private String mDest = "";
    private Marker mOriginMarker;
    private Marker mDestMarker;
    private Button btnNavigation;
    private GoogleMap mMap;

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

    View mapView;

    private OnFragmentInteractionListener mListener;

    private LatLng currLatLng;
    /*
    // Container Activity must implement this interface
    public interface OnMapFragmentSelectedListener {
        public void onMapSelected(int arg);
    }

    public void onMapSelected(int arg){}
    */

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */

    // TODO: Rename and change types and number of parameters if needed
    public static MapFragment newInstance(String param1, String param2) {
        //Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        //fragment.setArguments(args);
        return new MapFragment();
    }

    //lazy initiation of singleton pattern for MapFragment
    //doesn't work, performance is exactly the same as new MapFragment().
    //TODO: try onSaveInstanceState() below to fix the problem of new instance of MapFragment everytime
    private static volatile MapFragment instance = null;

    public static MapFragment getInstance() {
        if (instance == null) {
            synchronized (MapFragment.class) {
                if (instance == null) {
                    instance = new MapFragment();
                }
            }
        }
        return instance;
    }

    /*
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putInt("curChoice", mCurCheckPosition);
    }
    */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        if (getArguments() != null) {
            mOrigin = getArguments().getString(ARG_PARAM1);
            mDest = getArguments().getString(ARG_PARAM2);
        }
        */
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

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
                if(mDestMarker != null)
                    builder.include(mDestMarker.getPosition());

                if(mOriginMarker != null){

                }

                if (mOriginMarker != null) {
                    mOriginMarker.remove();
                }


                mOriginMarker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mOrigin = place.getAddress().toString();
                Log.i(TAG, "origin seleted"+mOrigin);


                builder.include(mOriginMarker.getPosition());
                bounds = builder.build();

                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                if (mDestMarker!= null) {

                    int padding = 300; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.animateCamera(cu);
                }


                Log.i(TAG, "origin seleted" + mOrigin);

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
            @Override
            public void onPlaceSelected(Place place) {
                builder = new LatLngBounds.Builder();
                if(mOriginMarker != null)
                    builder.include(mOriginMarker.getPosition());

                if(mDestMarker != null){

                }
                if (mDestMarker != null) {
                    mDestMarker.remove();
                }



                mDestMarker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()));
                mDest = place.getAddress().toString();
                Log.i(TAG, "destination seleted"+mDest);


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


            @Override
            public void onError(Status status) {
                //TODO
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
                sendRequest();




            }
        });

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

                            mOrigin=mostLikelyPlace.getAddress().toString();
                            Log.i(TAG, "origin seleted" + mOrigin);
                            likelyPlaces.release();
                        }catch (Exception e){
                            Log.d(TAG,"exception when setting text in origin textbox to current location:"+e.getMessage());

                        }

                    }
                }
        );






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
    }

    @Override
    public void onGenerateStart() {
        //TODO
    }

    @Override
    public void onGenerateSuccess(List<Path> paths) {
        //TODO
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    }

    //click listener to navigation button
    private void sendRequest(){
        new DirectionGenerator(this, mOrigin, mDest).generate();
        Log.i(TAG, "sendRequest() called, no error thrown.");

    }


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
        void onFragmentInteraction(Uri uri);
    }











}
