package com.teamruse.rarerare.tritontravel;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;

/**
 * Created by Xinyu on 11/19/2017.
 */

public abstract class PathSegment {
    private LatLng mStartLocation;
    private LatLng mEndLocation;
    private String mDuration;
    private String mDistance;
    //private Polyline mPolyLine;
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

    public void setPolyLine(String encodedPolyLine){
        mEncodedPolyLine = encodedPolyLine;
    }
}
