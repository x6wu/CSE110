package com.teamruse.rarerare.tritontravel;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Xinyu on 11/19/2017.
 */

public abstract class PathSegment {
    private LatLng mStartLocation;
    private LatLng mEndLocation;
    private String mDuration;
    private String mDistance;
    private ArrayList<LatLng> mPolyLine;
    private String mEncodedPolyLine;
    private SegmentFactory.TravelMode mTravelMode;

    public PathSegment(LatLng startLocation, LatLng endLocation, String duration, String distance,
                       SegmentFactory.TravelMode travelMode){
        mStartLocation = startLocation;
        mEndLocation = endLocation;
        mDistance = distance;
        mDuration = duration;
        mTravelMode = travelMode;
    }

    public void setEncodedPolyline(String encodedPolyline){
        mEncodedPolyLine = encodedPolyline;
    }

    public void setPolyLine(ArrayList<LatLng> polyLine){
        mPolyLine = polyLine;
    }

    public LatLng getStartLocation() {
        return mStartLocation;
    }

    public LatLng getEndLocation() {
        return mEndLocation;
    }

    public String getDuration() {
        return mDuration;
    }

    public String getDistance() {
        return mDistance;
    }

    public ArrayList<LatLng> getPolyLine() {
        return mPolyLine;
    }

    public String getEncodedPolyLine() {
        return mEncodedPolyLine;
    }

    public SegmentFactory.TravelMode getTravelMode() {
        return mTravelMode;
    }
}
