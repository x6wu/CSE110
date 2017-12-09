package com.teamruse.rarerare.tritontravel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

/**
 * Created by Xinyu on 11/18/2017.
 */

public class Path {
    private LatLng mStartLocation;
    private LatLng mEndLocation;
    private Polyline mPolyline;
    private String mDuration;
    private String mDistance;
    private String mDepartureTime;
    private String mArrivalTime;
    private ArrayList<PathSegment> mPathSegments;

    public Path(LatLng startLocation, LatLng endLocation, String duration, String distance){
        mStartLocation = startLocation;
        mEndLocation = endLocation;
        mDuration = duration;
        mDistance = distance;
        mPathSegments = new ArrayList<PathSegment>();
    }

    public void setStartLocation(LatLng startLocation) {
        this.mStartLocation = startLocation;
    }

    public void setEndLocation(LatLng endLocation) {
        this.mEndLocation = endLocation;
    }

    public void setPolyline(Polyline polyline) {
        this.mPolyline = polyline;
    }

    public void setDuration(String duration) {
        this.mDuration = duration;
    }

    public void setDistance(String distance) {
        this.mDistance = distance;
    }

    public void setDepartureTime(String departureTime) {
        this.mDepartureTime = departureTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.mArrivalTime = arrivalTime;
    }

    public void setPathSegments(ArrayList<PathSegment> pathSegments) {
        this.mPathSegments = pathSegments;
    }

    public LatLng getStartLocation() {
        return mStartLocation;
    }

    public LatLng getEndLocation() {
        return mEndLocation;
    }

    public Polyline getPolyline() {
        return mPolyline;
    }

    public String getDuration() {
        return mDuration;
    }

    public String getDistance() {
        return mDistance;
    }

    public String getDepartureTime() {
        return mDepartureTime;
    }

    public String getArrivalTime() {
        return mArrivalTime;
    }

    public ArrayList<PathSegment> getPathSegments() {
        return mPathSegments;
    }

}
