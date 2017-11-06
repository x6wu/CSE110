package com.teamruse.rarerare.tritontravel;

/**
 * Created by Xinyu on 11/5/2017.
 */

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;


public class DirectionGenerator {
    private String origin;
    private String dest;
    private DirectionGeneratorListener listener;
    private static final String ApiUrl = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String ApiKey = "AIzaSyDL3mGK2R6RvVHzHqhz7f2623iv6gGfU9w";

    public DirectionGenerator(DirectionGeneratorListener listener, String origin, String dest){
        this.listener = listener;
        this.origin = origin;
        this.dest = dest;
    }

    public String buildUrl(){
        String originUrl = origin.replaceAll(" ", "+");
        String destUrl = dest.replaceAll(" ", "+");
        return ApiUrl + "origin=" + originUrl + "&destination="
                + destUrl + "&mode=transit&key=" + ApiKey;
    }

    public void generate() {
        listener.onStart();
        //TODO
    }

    private class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                InputStream in = url.openConnection().getInputStream();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
