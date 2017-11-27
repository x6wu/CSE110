package com.teamruse.rarerare.tritontravel;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Xinyu on 11/19/2017.
 */

public class ShuttleSegment extends PathSegment {
    private String mDepartureTime;
    private String mArrivalTime;
    public ShuttleSegment(LatLng startLocation, LatLng endLocation, String duration, String distance,
                          SegmentFactory.TravelMode travelMode){
        super(startLocation, endLocation, duration, distance, travelMode);
    }
    //TODO: initialize member variables
    //TODO: getters and setters
}
