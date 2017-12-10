package com.teamruse.rarerare.tritontravel;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Xinyu on 11/19/2017.
 */

public class ShuttleSegment extends PathSegment {
    private String mDepartureTime;
    private String mArrivalTime;
    private String mStartStop;
    private String mEndStop;
    private String mShuttleHeadsign;
    private int mNumStops;

    public ShuttleSegment(LatLng startLocation, LatLng endLocation, String duration, String distance,
                          SegmentFactory.TravelMode travelMode){
        super(startLocation, endLocation, duration, distance, travelMode);
    }

    public void setArrivalTime(String arrivalTime) {
        this.mArrivalTime = arrivalTime;
    }

    public void setDepartureTime(String departureTime) {
        this.mDepartureTime = departureTime;
    }

    public void setShuttleHeadsign(String shuttleHeadsign) {
        this.mShuttleHeadsign = shuttleHeadsign;
    }

    public void setNumStops(int numStops) {
        this.mNumStops = numStops;
    }

    public void setStartStop(String startStop) {
        this.mStartStop = startStop;
    }

    public void setEndStop(String endStop) {
        this.mEndStop = endStop;
    }

    public String getStartStop() {
        return mStartStop;
    }

    public String getEndStop(){return mEndStop;}

    public String getArrivalTime() {
        return mArrivalTime;
    }

    public String getDepartureTime() {
        return mDepartureTime;
    }

    public String getShuttleHeadsign() {
        return mShuttleHeadsign;
    }

    public int getNumStops() {
        return mNumStops;
    }
}
