package com.teamruse.rarerare.tritontravel;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Xinyu on 11/18/2017.
 */

public class Stop{
    private String mName;
    private LatLng mLatLng;

    public Stop(String name, LatLng latLng){
        mName = name;
        mLatLng = latLng;
    }

    public String getName(){
        return mName;
    }

    private void setName(String name){
        mName = name;
    }

    public LatLng getLatLng(){
        return mLatLng;
    }

    private void setLatLng(LatLng latLng){
        mLatLng = latLng;
    }
}
