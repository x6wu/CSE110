package com.teamruse.rarerare.tritontravel;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Xinyu on 11/19/2017.
 */

public class SegmentFactory {
    public enum TravelMode{
        BUS,
        WALKING,
        SHUTTLE
    }
    public PathSegment getSegment(TravelMode travelMode, LatLng startLocation, LatLng endLocation,
                                  String duration, String distance){
        switch (travelMode){
            case BUS:
                return new BusSegment(startLocation, endLocation, duration, distance, travelMode);
            case WALKING:
                return new WalkingSegment(startLocation, endLocation, duration, distance, travelMode);
            case SHUTTLE:
                return new ShuttleSegment(startLocation, endLocation, duration, distance, travelMode);
            default:
                return null;
        }
    }
}
