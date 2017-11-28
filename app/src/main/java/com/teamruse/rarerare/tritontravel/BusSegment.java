package com.teamruse.rarerare.tritontravel;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Xinyu on 11/19/2017.
 */

public class BusSegment extends PathSegment {
    private String mStartStop;
    private String mEndStop;
    private String mArrivalTime;
    private String mDepartureTime;
    private String mBusHeadsign;
    private String mBusName;
    private int mNumStops;

    public BusSegment(LatLng startLocation, LatLng endLocation, String duration, String distance,
                      SegmentFactory.TravelMode travelMode){
        super(startLocation, endLocation, duration, distance, travelMode);
    }


    public void setArrivalTime(String arrivalTime) {
        this.mArrivalTime = arrivalTime;
    }

    public void setDepartureTime(String departureTime) {
        this.mDepartureTime = departureTime;
    }

    public void setBusHeadsign(String busHeadsign) {
        this.mBusHeadsign = busHeadsign;
    }

    public void setBusName(String busName) {
        this.mBusName = busName;
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





}