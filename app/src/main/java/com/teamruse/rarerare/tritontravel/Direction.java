package com.teamruse.rarerare.tritontravel;

/**
 * Created by Xinyu on 11/5/2017.
 */

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;


public class Direction {
    private String origin;
    private String dest;
    private static final String ApiUrl = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String ApiKey = "AIzaSyDL3mGK2R6RvVHzHqhz7f2623iv6gGfU9w";

    public Direction(String origin, String dest){
        this.origin = origin;
        this.dest = dest;
    }

    public String UrlBuilder(){
        String originUrl = origin.replaceAll(" ", "+");
        String destUrl = dest.replaceAll(" ", "+");
        return ApiUrl + "origin=" + originUrl + "&destination="
                + destUrl + "&mode=transit&key=" + ApiKey;

    }


}
