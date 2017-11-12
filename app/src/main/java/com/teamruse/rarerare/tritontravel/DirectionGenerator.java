package com.teamruse.rarerare.tritontravel;

/**
 * Created by Xinyu on 11/5/2017.
 */

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        new DownloadTask().execute(buildUrl());
    }

    private class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                InputStream inputStream = url.openConnection().getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
                String inputLine;
                StringBuffer buffer = new StringBuffer();
                while((inputLine = in.readLine()) != null){
                    buffer.append(inputLine + "\n");
                }
                Log.d("JSON", buffer.toString());
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
