package com.teamruse.rarerare.tritontravel;

/**
 * Created by Xinyu on 11/5/2017.
 */

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.teamruse.rarerare.tritontravel.SegmentFactory.TravelMode.BUS;
import static com.teamruse.rarerare.tritontravel.SegmentFactory.TravelMode.WALKING;

//based on the tutorial from
//https://github.com/hiepxuan2008/GoogleMapDirectionSimple/blob/master/app/src/main/java/Modules/DirectionFinder.java
public class DirectionGenerator {
    private LatLng mOrigin;
    private LatLng mDest;
    private DirectionGeneratorListener listener;
    private static final String ApiUrl = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String ApiKey = "AIzaSyDW5Ey3-PU1oYnLrxCo27RbFmKbRjX-wig";

    private static final String TAG = "Direction_Generator";

    public DirectionGenerator(DirectionGeneratorListener listener, LatLng origin, LatLng dest) {
        this.listener = listener;
        this.mOrigin = origin;
        this.mDest = dest;
    }

    public String buildUrl() {
        String originUrl = (mOrigin.latitude + "," + mOrigin.longitude).replaceAll(" ", "+");
        String destUrl = (mDest.latitude + "," + mDest.longitude).replaceAll(" ", "+");
        String URL = ApiUrl + "origin=" + originUrl + "&destination="
                + destUrl + "&mode=transit&key=" + ApiKey;
        //String URL = ApiUrl + "origin=place_id:"+origin+"&destination=place_id"
        //        + dest + "&mode=transit&key=" + ApiKey;
        Log.i(TAG, "Request URL:"+URL);
        return URL;
    }

    public void generate() {
        listener.onGenerateStart();
        new DownloadTask().execute(buildUrl());
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try{
                URL url = new URL(strings[0]);
                InputStream inputStream = url.openConnection().getInputStream();
                BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));

                String inputLine;
                StringBuilder builder = new StringBuilder();
                //builder.ensureCapacity(1048576);
                while ((inputLine = in.readLine()) != null) {
                    builder.append(inputLine + "\n");
                }
                //Log.d("JSON", builder.toString());
                final int chunkSize = 2048;
                for (int i = 0; i < builder.toString().length(); i += chunkSize) {
                    Log.d(TAG, builder.toString().substring(i, Math.min(builder.toString().length(), i + chunkSize)));
                }
                inputStream.close();
                in.close();
                return builder.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String response) {
            try {
                parseJson(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void parseJson(String data) throws JSONException {
        SegmentFactory segmentFactory = new SegmentFactory();
        if (data == null) {
            return;
        }
        List<Path> paths = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(data);
        try {
            //json array that contains all routs
            JSONArray jsonArray = jsonObject.getJSONArray("routes");
            for (int i = 0; i < jsonArray.length(); ++i) {
                //retrieve each route
                JSONObject jsonRoute = jsonArray.getJSONObject(i);
                JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
                JSONObject jsonLeg = jsonLegs.getJSONObject(0);
                JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
                JSONObject jsonDuration = jsonLeg.getJSONObject("duration");
                //JSONObject jsonDepartureTime = jsonLeg.getJSONObject("departure_time");
                //JSONObject jsonArrivalTime = jsonLeg.getJSONObject("arrival_time");
                JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");
                JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
                JSONArray jsonStepsArray = jsonLeg.getJSONArray("steps");
                LatLng startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
                LatLng endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
                String duration = jsonDuration.getString("text");
                String distance = jsonDistance.getString("text");
                Path newPath = new Path(startLocation, endLocation, duration, distance);
                //newPath.setArrivalTime(jsonArrivalTime.getString("text"));
                //newPath.setDepartureTime(jsonDepartureTime.getString("text"));
                Log.d(TAG, "number of steps " + jsonStepsArray.length());

                for (int j = 0; j < jsonStepsArray.length(); ++j) {
                    //retrieve each step
                    JSONObject jsonStep = jsonStepsArray.getJSONObject(j);
                    JSONObject jsonStepDistance = jsonStep.getJSONObject("distance");
                    JSONObject jsonStepDuration = jsonStep.getJSONObject("duration");
                    JSONObject jsonStepStartLocation = jsonStep.getJSONObject("start_location");
                    JSONObject jsonStepEndLocation = jsonStep.getJSONObject("end_location");
                    JSONObject jsonStepPolyline = jsonStep.getJSONObject("polyline");
                    LatLng stepStartLocation = new LatLng(jsonStepStartLocation.getDouble("lat"),
                            jsonStepStartLocation.getDouble("lng"));
                    LatLng stepEndLocation = new LatLng(jsonStepEndLocation.getDouble("lat"),
                            jsonStepEndLocation.getDouble("lng"));
                    String stepDuration = jsonStepDuration.getString("text");
                    String stepDistance = jsonStepDistance.getString("text");
                    String stepPolyline = jsonStepPolyline.getString("points");
                    Log.d(TAG, stepPolyline);
                    SegmentFactory.TravelMode travelMode;
                    if (jsonStep.getString("travel_mode").equals("WALKING")) {
                        travelMode = WALKING;
                    } else if (jsonStep.getString("travel_mode").equals("TRANSIT")) {
                        travelMode = BUS;
                    } else {
                        break;
                    }
                    PathSegment newSegment = segmentFactory.getSegment(travelMode, stepStartLocation,
                            stepEndLocation, stepDuration, stepDistance);
                    newSegment.setEncodedPolyline(stepPolyline);
                    newSegment.setPolyLine(decodePolyline(stepPolyline));
                    Log.d(TAG, "num of points: " + newSegment.getPolyLine().size());
                    if (travelMode == BUS) {
                        JSONObject jsonTransitDetails = jsonStep.getJSONObject("transit_details");
                        JSONObject jsonArrivalStop = jsonTransitDetails.getJSONObject("arrival_stop");
                        JSONObject jsonDepartureStop = jsonTransitDetails.getJSONObject("departure_stop");
                        JSONObject jsonTransitDepartureTime = jsonTransitDetails.getJSONObject("departure_time");
                        JSONObject jsonTransitArrivalTime = jsonTransitDetails.getJSONObject("arrival_time");
                        JSONObject jsonTransitLine = jsonTransitDetails.getJSONObject("line");
                        String headsign = jsonTransitDetails.getString("headsign");
                        int numStops = jsonTransitDetails.getInt("num_stops");
                        ((BusSegment) newSegment).setDepartureTime(jsonTransitDepartureTime.getString("text"));
                        ((BusSegment) newSegment).setArrivalTime(jsonTransitArrivalTime.getString("text"));
                        ((BusSegment) newSegment).setStartStop(jsonDepartureStop.getString("name"));
                        ((BusSegment) newSegment).setEndStop(jsonArrivalStop.getString("name"));
                        ((BusSegment) newSegment).setBusHeadsign(headsign);
                        ((BusSegment) newSegment).setNumStops(numStops);
                        ((BusSegment) newSegment).setBusName(jsonTransitLine.getString("short_name"));
                    }
                    newPath.getPathSegments().add(newSegment);
                }
                paths.add(newPath);
            }
        } catch (Exception e) {
            Log.e(TAG, "Json parsing failed.");
            Log.e(TAG, e.getMessage());
            listener.onGenerateFailure();
        }
        //Log.i(TAG, "Paths size:" + paths.size());
        listener.onGenerateSuccess(paths);
    }

    private ArrayList<LatLng> decodePolyline(String encoded) {
        int len = encoded.length();
        int index = 0;
        ArrayList<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }
        return decoded;
    }
}





