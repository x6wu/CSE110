package com.teamruse.rarerare.tritontravel;

import com.google.android.gms.location.places.Place;

import java.util.Date;

/**
 * Created by JingJing on 11/29/17.
 */

public class StopHistory {
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private long id;
    private String stopName;
    private String stopTime;
    private String placeId;


    public StopHistory() {
        stopName = "";
        stopName = "";
        placeId="";

    }

    public StopHistory(String name, String time, String placeId) {
        this.stopName = name;
        this.stopTime = time;
        this.placeId=placeId;

    }

    public void setStopName (String name) {
        stopName = name;
    }

    public void setStopTime (String time) {
        stopTime = time;
    }

    public String getStopName() {
        return stopName;
    }

    public String getStopTime() {
        return stopTime;
    }

    public String getPlaceId() {
        return placeId;
    }




}
