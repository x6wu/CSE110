package com.teamruse.rarerare.tritontravel;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback{
    private GoogleMap map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng latLng=place.getLatLng();
                map.addMarker(new MarkerOptions().position(latLng)
                        .title(""));
                map.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("selector", "An error occurred: " + status);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng ucsd = new LatLng(32.879409,-117.2389395);
        googleMap.addMarker(new MarkerOptions().position(ucsd)
                .title(""));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(ucsd));
        this.map=googleMap;
    }
}
