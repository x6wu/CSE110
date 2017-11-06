package com.teamruse.rarerare.tritontravel;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    /*
    private String serverKey = "AIzaSyDtVu0VrjihbAtuZhyB52qJbuZ79d9jvxQ";
    private LatLng originLoc = new LatLng(13.7371063, 100.5642539);
    private LatLng destinationLoc = new LatLng(13.7604896, 100.5594266);

    public MainActivity() throws InterruptedException, ApiException, IOException {
    }

    private GeoApiContext getGeoContext(){
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3)
                .setApiKey(getString(R.string.directionsApiKey))
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }
    */

    private String origin = "";
    private String dest = "";
    private Marker mOrigin;
    private Marker mDest;
   // private TravelMode mode = TravelMode.TRANSIT;
    private Button btnNavigation;
    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnNavigation = (Button)findViewById(R.id.search_button);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        PlaceAutocompleteFragment autocompleteFragmentOrigin = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_origin);
        PlaceAutocompleteFragment autocompleteFragmentDest = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_dest);

        autocompleteFragmentOrigin.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                /*
                LatLng latLng = place.getLatLng();
                map.addMarker(new MarkerOptions().position(latLng)
                        .title(""));
                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                */
                origin = place.getAddress().toString();
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
                dest = place.getAddress().toString();
            }

            @Override
            public void onError(Status status) {

            }
        });

        btnNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*

                try {
                    DirectionsApiRequest request = DirectionsApi.getDirections()
                    /*
                    DirectionsResult result = DirectionsApi.newRequest(getGeoContext())
                            .mode(TravelMode.TRANSIT).origin(origin)
                            .destination(dest).departureTime(now)
                            .await();
                    //addMarkersToMap(result, map);

                } catch (ApiException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                */
            }
        });

    }

    /*
    private void addMarkersToMap(DirectionsResult results, GoogleMap mMap) {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(results.routes[0].legs[0].startLocation.lat, results.routes[0].legs[0].startLocation.lng))
                .title(results.routes[0].legs[0].startAddress));
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(results.routes[0].legs[0].endLocation.lat, results.routes[0].legs[0].endLocation.lng))
                .title(results.routes[0].legs[0].startAddress));
        //.snippet(getEndLocationTitle(results)));
    }

    private String getEndLocationTitle(DirectionsResult results) {
        return "Time :" + results.routes[0].legs[0].duration.humanReadable
                + " Distance :" + results.routes[0].legs[0].distance.humanReadable;
    }
    */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng ucsd = new LatLng(32.879409, -117.2389395);
        googleMap.addMarker(new MarkerOptions().position(ucsd)
                .title(""));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(ucsd));
        this.map = googleMap;
    }

}
