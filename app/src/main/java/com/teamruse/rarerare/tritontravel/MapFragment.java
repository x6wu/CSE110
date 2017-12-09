package com.teamruse.rarerare.tritontravel;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.LinearLayout;


import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import com.google.firebase.database.DatabaseReference;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import ru.whalemare.sheetmenu.SheetMenu;

import static com.teamruse.rarerare.tritontravel.SegmentFactory.TravelMode.WALKING;

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
        GoogleMap.OnMyLocationButtonClickListener,TagDialog.TagDialogListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final String TAG = "Map_Fragment";

    private String mOriginStr = "";
    private String mDestStr = "";

    private  Place mDestPlace;
    private  Place mOriginPlace;
    private Marker mOriginMarker;
    private Marker mDestMarker;

    private  DatabaseReference mDatabase;
    protected List<Path> mPaths;
    //private Button btnNavigation;
    //private GoogleMap mMap;


    public Button btnNavigation;
    public  GoogleMap mMap;

    private SupportPlaceAutocompleteFragment autocompleteFragmentOrigin;
    private SupportPlaceAutocompleteFragment autocompleteFragmentDest;

    private Location mLastKnownLocation;
    private Marker mLastKnownLocationMarker;
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng mDefaultLatLng = new LatLng(32.880088,  -117.234003);
    private int mDefaultZoom = 15;
    private LatLngBounds.Builder builder;
    private LatLngBounds bounds;
    private ArrayList<Polyline> mPolylines;
    private  FirebaseAuth mAuth;

    public String tag = "";

    View mapView;

    private OnFragmentInteractionListener mListener;
    private LatLng currLatLng;



    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mAuth = FirebaseAuth.getInstance();
        mPolylines = new ArrayList<>();
        mDatabase = ((MainActivity)getActivity()).getDatabase();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mapView = inflater.inflate(R.layout.map_frag, container, false);
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

        /*
         * Shuyuan Ma @Dec 7
         * Restructured to reduce duplicate codes, now using helper method
         */
        autocompleteFragmentOrigin.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onPlaceSelected(Place place) {
                fillInOriginSearchBox(place);
            }
            @Override
            public void onError(Status status) {
                Log.i(TAG, "onError(). An error occurred: " + status);
            }
        });

        /*
          Shuyuan Ma @Dec. 6
          Origin search box clear button listener
         */
        autocompleteFragmentOrigin.getView().findViewById(R.id.place_autocomplete_clear_button)
            .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // example : way to access view from PlaceAutoCompleteFragment
                // ((EditText) autocompleteFragment.getView()
                // .findViewById(R.id.place_autocomplete_search_input)).setText("");
                autocompleteFragmentOrigin.setText("");
                view.setVisibility(View.GONE);
                /*
                  Shuyuan Ma Dec 6th
                  Clear map when clear button clicked
                  If destination marker is still selected, reconstruct mDestMarker
                 */
                if (mDestMarker != null) {
                    mMap.clear();
                    //reconstruct the Dest marker
                    mDestMarker = mMap.addMarker(new MarkerOptions().position(mDestPlace.getLatLng()).
                            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    mDestMarker.setTitle(mDestPlace.getName().toString());
                } else {
                    mMap.clear();
                }

                mOriginPlace = null;
                mOriginMarker = null;
                mOriginStr="";
            }
        });

        /*
         * Shuyuan Ma @Dec 7
         * Restructured to reduce duplicate codes, now using helper method
         */
        autocompleteFragmentDest.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onPlaceSelected(Place place) {
                fillInDestSearchBox(place);
            }
            @Override
            public void onError(Status status) {
                Log.e(TAG, status.getStatusMessage());
            }
        });

        /**
         * Shuyuan Ma @Dec. 6
         * Destination search box clear button listener
         */
        autocompleteFragmentDest.getView().findViewById(R.id.place_autocomplete_clear_button)
            .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autocompleteFragmentDest.setText("");
                view.setVisibility(View.GONE);
                /**
                 * Shuyuan Ma Dec 6th
                 * Clear map when clear button clicked
                 * If origin marker is still selected, reconstruct mOriginMarker
                 */
                if (mOriginMarker != null) {
                    mMap.clear();
                    //reconstruct the Origin marker
                    mOriginMarker = mMap.addMarker(new MarkerOptions().position(mOriginPlace.getLatLng()).
                            icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    //mOriginMarker.setTitle(mOriginPlace.getName().toString());
                } else {
                    mMap.clear();
                }

                mDestPlace = null;
                mDestMarker = null;
                mDestStr="";
                /*
                if (mDestMarker != null)
                    mDestMarker.remove();
                */
            }
        });

        //set autocompleteFragment backgrounds
        autocompleteFragmentOrigin.getView().setBackgroundColor(Color.WHITE);
        autocompleteFragmentDest.getView().setBackgroundColor(Color.WHITE);

        btnNavigation = (Button) mapView.findViewById(R.id.search_button);
        btnNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDestMarker != null && mOriginMarker !=null) {
                    mOriginMarker.setVisible(true);
                    builder = new LatLngBounds.Builder();
                    builder.include(mOriginMarker.getPosition());
                    builder.include(mDestMarker.getPosition());
                    bounds = builder.build();
                    int padding = 200; // offset from edges of the map in pixels
                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                    mMap.animateCamera(cu);
                    //showRoutes(mOriginMarker, mDestMarker);
                }
                //navigation button callback to onFragmentInteraction
                //MainActivity will handle the sendRequest() part
                if(mListener != null) {
                    if(mOriginPlace == null) {
                        Toast.makeText(getContext(), "Please enter an origin.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    else if(mDestPlace == null){
                        Toast.makeText(getContext(), "Please enter a destination.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    mListener.onNavRequest(mOriginStr, mDestStr);
                }



            }
        });
    }



    //sheet menu for save
    private void showMenu(final Place p) {
        SheetMenu.with(getContext()).setTitle(p.getName().toString()).setMenu(R.menu.sheet_menu)
            .setClick(new MenuItem.OnMenuItemClickListener() {
                public final Place q = p;
                @Override
                public boolean onMenuItemClick(MenuItem item){
                    if  (item.getItemId() == R.id.seeAddrBtn) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setMessage(p.getAddress().toString());
                        alert.setNegativeButton("got you", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        //alert.show();

                        AlertDialog dialog = alert.create();
                        dialog.show();

                        Button b = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                        if(b != null) {
                            b.setTextColor(Color.parseColor("#064264"));

                        }

                    }


                    else if (item.getItemId() == R.id.saveStop) {

                        if (!((MainActivity)getActivity()).signedIn()) {
                            Toast.makeText(getContext(),"Please sign in", Toast.LENGTH_SHORT).show();
                        }
                        else {


                            openDialog(q);
                            //move the database part to TagDialog.java


                            tag = "";
                        }
                        return true;
                    }
                    return false;
                }
            }).show();
    }

    protected void writeOriginToDB(String tag){
        FirebaseUser user = mAuth.getCurrentUser();
        ((MainActivity)getActivity()).writeStopToDB(user, new StopHistory(mOriginPlace.getName().toString()
                , mOriginPlace.getId(), tag));
    }

    //Type = "route" or "origin" or "dest"
    protected Boolean hasDupe(String type){
        Log.d(TAG, "made it to hasDupe");
        final Long[] dupe = new Long[1];
        dupe[0] = (long) 0;
        //If you want to check for a duplicate route
        if(type.equals("route")){
            final String placeID = mOriginPlace.getId() + " -> " +  mDestPlace.getId();
            DatabaseReference route = FirebaseDatabase.getInstance().getReference().child("routes").child("route_id_" + mAuth.getUid());
            route.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for(DataSnapshot shot : snapshot.getChildren()) {
                        //Gets PlaceID from child
                        String place = shot.child("placeId").getValue().toString();
                        Log.d(TAG, "placeId:" + place);
                        Log.d(TAG, "Our placeID:" + placeID);
                        if(place.equals(placeID)){
                            Log.d(TAG, "made it into If Statement");
                            dupe[0] = (long) 1;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // ...
                }
            });
        }
        //If you want to check for a duplicate stop
        else{
            final String placeID;

            if(type.equals("origin")){
                placeID = mOriginPlace.getId();
            }
            else{
                placeID = mDestPlace.getId();
            }

            DatabaseReference stop = FirebaseDatabase.getInstance().getReference().child("stops").child("stop_id_" + mAuth.getUid());
            stop.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for(DataSnapshot shot : snapshot.getChildren()) {
                        //Gets PlaceID from child
                        String place = shot.child("placeId").getValue().toString();
                        Log.d(TAG, "placeId:" + place);
                        Log.d(TAG, "Our placeID:" + placeID);
                        if(place.equals(placeID)){
                            Log.d(TAG, "made it into If Statement");
                            dupe[0] = (long) 1;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // ...
                }
            });
        }

        long dupeVal = dupe[0];
        Log.d(TAG, "Value of Dupe: " + dupeVal);
        if(dupeVal == (long) 1){
            Log.d(TAG, "made it into true If Statement");
            return true;
        }
        if(dupeVal == (long) 0){
            Log.d(TAG, "made it into false If Statement");
            return false;
        }
        return true;
    }

    protected void writeDestToDB(String tag){
        FirebaseUser user = mAuth.getCurrentUser();
        ((MainActivity)getActivity()).writeStopToDB(user,new StopHistory(mDestPlace.getName().toString()
                ,mDestPlace.getId(),tag));
        Log.d(TAG, "history:"+new StopHistory(mDestPlace.getName().toString()
                ,mDestPlace.getId(),tag).toString());
    }

    //Ruoyu Xu
    protected void writeRouteToDB(){
        FirebaseUser user = mAuth.getCurrentUser();
        ((MainActivity)getActivity()).writeRouteToDB(user,new StopHistory(mOriginPlace.getName().toString()
                + " -> " + mDestPlace.getName().toString()
                ,mOriginPlace.getId() + " -> " +  mDestPlace.getId()
                ,tag));
    }

    public void openDialog( Place p) {
        Bundle args=new Bundle();

        if(p.equals(mDestPlace)) {
            args.putString("destOrOrigin", "dest");

        }
        else if(p.equals(mOriginPlace)){
            args.putString("destOrOrigin", "origin");

        }
        TagDialog dialog = new TagDialog();
        dialog.setArguments(args);
        dialog.show(getChildFragmentManager(),"tag dialog");
    }

    @Override
    public void applyTexts(String inputTag) {
        this.tag = inputTag;
    }


    //zijing show dummy routes
    /*
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
    */



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
            }
            else{
                mMap.setMyLocationEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e){
            Log.e(TAG, e.getMessage());
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
                            } catch (NullPointerException e){
                                Log.d(TAG, "Current location is null. Using defaults.");
                                Log.e(TAG, "Exception: " + task.getException());
                                CameraPosition cameraPosition = new CameraPosition(
                                        mDefaultLatLng, mDefaultZoom, mMap.getCameraPosition().tilt,
                                        mMap.getCameraPosition().bearing);
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 2000, null);
                                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Task Failed: " + task.getException());
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
            Log.e(TAG,"SecurityException :"+e.getMessage());
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap = googleMap;
        updateLocationUI();
        getDeviceLocation();
        updateLocationUI();

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;

        mMap.setPadding(0,height/5,0,0);
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

    public interface OnFragmentInteractionListener {
        // TODO: implement this interface in case we need to communicate with the activity
        void onNavRequest(String origin, String dest);
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public boolean onMyLocationButtonClick() {
        PlaceDetectionClient mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        /*
         * Ruoyu Xu
         * Set text in origin textbox to current location
         */

        /*
         * Shuyuan Ma @Dec 7
         * Rewrite to fix buges
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
                        fillInOriginSearchBox(mostLikelyPlace);
                        likelyPlaces.release();
                    }catch (Exception e){
                        Log.d(TAG,"exception when setting text in origin textbox to current location:"+e.getMessage());
                    }
                }
            }
        );
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void fillInOriginSearchBox(Place place){
        //clean map
        if (mOriginMarker != null) {
            if (mDestMarker != null) {
                mMap.clear();
                //reconstruct the Origin marker
                mDestMarker = mMap.addMarker(new MarkerOptions().position(mDestPlace.getLatLng()).
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                mDestMarker.setTitle(mDestPlace.getName().toString());
            } else {
                mMap.clear();
            }
        }
        //store place info to instance parameter
        mOriginPlace = place.freeze();
        mOriginStr = place.getAddress().toString();
        autocompleteFragmentOrigin.setText(place.getName().toString());
        //build marker
        mOriginMarker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        //Log report
        Log.i(TAG, "origin selected, Name: " + place.getName());
        Log.i(TAG, "\t\tId: " + place.getId());
        Log.i(TAG, "\t\tAddress: "+ place.getAddress().toString());

        //store destination into History
        if ( History.stopsList == null) {
            History.stopsList = new ArrayList<>();
        }
        //DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd | hh:mm");
        Date date = new Date();
        (new StopHistoryBaseHelper(getActivity().getApplicationContext()))
                .writeStopHistory( new StopHistory(place.getName().toString()
                        , date.getTime(), place.getId()));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker arg0) {
                if (arg0.equals(mOriginMarker)){
                    showMenu(mOriginPlace);
                    return true;
                }
                else if (arg0.equals(mDestMarker)) {// if marker source is clicked
                    showMenu(mDestPlace);
                    return true;
                }
                return false;
            }
        });
        //builder to move the camera to appropriate mode
        builder = new LatLngBounds.Builder();
        builder.include(mOriginMarker.getPosition());
        if (mDestMarker != null) {
            builder.include(mDestMarker.getPosition());
            bounds = builder.build();
            int padding = 200; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cu);
        } else {
            bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 18));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void setmDestPlace(Place place){
        try {
            fillInDestSearchBox(place);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /*
      Shuyuan Ma @Dec 7th
      Rewrite fillInDestSearchBox() to fix crashes
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void fillInDestSearchBox(Place place){
        //clean map
        if (mDestMarker != null) {
            if (mOriginMarker != null) {
                mMap.clear();
                //reconstruct the Origin marker
                mOriginMarker = mMap.addMarker(new MarkerOptions().position(mOriginPlace.getLatLng()).
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                mOriginMarker.setTitle(mOriginPlace.getName().toString());
            } else {
                mMap.clear();
            }
        }
        //store place info to instance parameter
        mDestPlace = place.freeze();
        mDestStr = place.getAddress().toString();
        autocompleteFragmentDest.setText(place.getName().toString());
        //build marker
        mDestMarker = mMap.addMarker(new MarkerOptions().position(place.getLatLng()).
                icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        //Log report
        Log.i(TAG, "destination selected, Name: " + place.getName());
        Log.i(TAG, "\t\tId: " + place.getId());
        Log.i(TAG, "\t\tAddress: "+ place.getAddress().toString());

        //store destination into History
        if ( History.stopsList == null) {
            History.stopsList = new ArrayList<>();
        }
        //DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd | hh:mm");
        Date date = new Date();
        (new StopHistoryBaseHelper(getActivity().getApplicationContext()))
                .writeStopHistory( new StopHistory(place.getName().toString()
                        , date.getTime(), place.getId()));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker arg0) {
                if (arg0.equals(mOriginMarker)){
                    showMenu(mOriginPlace);
                    return true;
                }
                else if (arg0.equals(mDestMarker)) {// if marker source is clicked
                    showMenu(mDestPlace);
                    return true;
                }
                return false;
            }
        });
        //builder to move the camera to appropriate mode
        zoomToDestWithOrigin(place);
    }

    //Ruoyu Xu refactor Zijing's code
    protected void zoomToDestWithOrigin(Place place){
        builder = new LatLngBounds.Builder();
        builder.include(mDestMarker.getPosition());
        if (mOriginMarker != null) {
            builder.include(mOriginMarker.getPosition());
            bounds = builder.build();
            int padding = 200; // offset from edges of the map in pixels
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cu);
        } else {
            bounds = builder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 18));
        }
    }

    @Override
    public void onPause(){
        super.onPause();

        Log.i(TAG, "onPause called");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.i(TAG, "onStop called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy called");
    }


    /*
     * Shuyuan Ma @Dec 8
     * Display path information on a bottomSheet
     */
    public void displayPath(List<Path> paths){
        mPaths = paths;
        View view = getLayoutInflater().inflate(R.layout.query_result_bottom_sheet, null);

        Button saveRoutesButton = view.findViewById(R.id.saveRoutesButton);
        saveRoutesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!((MainActivity)getActivity()).signedIn()) {
                    Toast.makeText(getContext(),"Please sign in", Toast.LENGTH_SHORT).show();
                }
                else {
                    openDialog();
                }
            }
        });


        BottomSheetDialog dialog = new BottomSheetDialog(getActivity());

        LinearLayout path_container = view.findViewById(R.id.path_container);

        ArrayList<pathResult> results = new ArrayList<>();
        for (Path path: paths) {
            results.add(new pathResult(path));
        }
        for(int i = 0; i<results.size(); i++) {
            LinearLayout a = new LinearLayout(getContext());
            a.setOrientation(LinearLayout.HORIZONTAL);
            //Place the object in the center of its container in both the vertical and horizontal
            //axis, not changing its size.
            a.setGravity(11);
            ArrayList<String> segments = results.get(i).segments;
            for (int j = 0; j < segments.size(); j++) {
                if (segments.get(j) == "Walking") {
                    ImageView img = new ImageView(getContext());
                    img.setImageResource(R.drawable.ic_walk);
                    a.addView(img);
                } else {
                    ImageView img = new ImageView(getContext());
                    img.setImageResource(R.drawable.ic_bus);
                    a.addView(img);
                    TextView txt = new TextView(getContext());
                    txt.setText(segments.get(j));
                    a.addView(txt);
                }
                ImageView img = new ImageView(getContext());
                img.setImageResource(R.drawable.ic_menu_send);
                a.addView(img);
            }
            a.setClickable(true);
            //set onClickListener for each line of result
            a.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int index = ((ViewGroup) view.getParent()).indexOfChild(view);
                    //render and draw the first path's polyline
                    ArrayList<PathSegment> recPathSegments = mPaths.get(index).getPathSegments();
                    for (int i = 0; i < recPathSegments.size(); ++i) {
                        PathSegment currSegment = recPathSegments.get(i);
                        Log.d("travel mode", currSegment.getTravelMode().toString());
                        Log.d("polyline", currSegment.getEncodedPolyLine());
                        PolylineOptions polylineOptions = new PolylineOptions()
                                .addAll(currSegment.getPolyLine())
                                .color(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.blue));
                        //set polyline pattern to be dotted if travel mode is walking
                        if (currSegment.getTravelMode() == WALKING) {
                            List<PatternItem> patternItemList = new ArrayList<>();
                            patternItemList.add(new Dot());
                            polylineOptions.pattern(patternItemList);
                        }
                        drawPolylines(polylineOptions);
                    }
                }
            });
            path_container.addView(a);
        }

        dialog.setContentView(view);
        dialog.show();
    }

    public void openDialog() {
        TagDialog dialog = new TagDialog();
        dialog.show(getChildFragmentManager(),"tag dialog");
    }

    public void drawPolylines(PolylineOptions polylineOptions){
        mMap.addPolyline(polylineOptions);
    }

    //Ruoyu Xu
    protected void goToTwoStops(String placeId1, String placeId2){
        (Places.getGeoDataClient(getActivity(),null)).getPlaceById(placeId1)
                .addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                        if (task.isSuccessful()) {
                            PlaceBufferResponse places = task.getResult();
                            mOriginPlace = places.get(0);
                            fillInOriginSearchBox(mOriginPlace);

                            places.release();
                        } else {
                            Log.e(TAG, "Place not found.");
                        }
                    }
                });
        (Places.getGeoDataClient(getActivity(),null)).getPlaceById(placeId2)
                .addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                        if (task.isSuccessful()) {
                            PlaceBufferResponse places = task.getResult();
                            mDestPlace = places.get(0);
                            fillInDestSearchBox(mDestPlace);

                            places.release();
                        } else {
                            Log.e(TAG, "Place not found.");
                        }
                    }
                });
        


    }

}
