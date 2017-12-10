package com.teamruse.rarerare.tritontravel;


import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Xinyu on 12/7/2017.
 */

public class MapUtils {
    /**
     *
     * @param point1
     * @param point2
     * @return
     */
    //https://andrew.hedges.name/experiments/haversine/
    public static double distance(LatLng point1, LatLng point2){
        double dlon = Math.toRadians(point2.longitude) - Math.toRadians(point1.longitude);
        double dlat = Math.toRadians(point2.latitude) - Math.toRadians(point1.latitude);
        double a = Math.sin(dlat/2) * Math.sin(dlat/2) +
                Math.cos(Math.toRadians(point2.latitude)) * Math.cos(Math.toRadians(point1.latitude))
                * Math.sin(dlon/2) * Math.sin(dlon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = 3961 * c;

        return distance;
    }

}
