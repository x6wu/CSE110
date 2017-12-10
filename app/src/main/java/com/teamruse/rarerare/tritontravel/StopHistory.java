package com.teamruse.rarerare.tritontravel;

import com.google.android.gms.location.places.Place;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    private String stopTimeStr;
    private String placeId;



    private long timeStamp;


    public StopHistory() {
        stopName = "";
        stopName = "";
        placeId="";

    }

    public StopHistory(String name, long time, String placeId) {
        this.stopName = name;
        this.timeStamp = time;
        this.placeId=placeId;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd | hh:mm:ss aa");
        Date date = new Date(timeStamp);
        stopTimeStr=dateFormat.format(date);

    }

    public StopHistory(Place p) {
        this.stopName = p.getName().toString();
        this.placeId=p.getId();

    }

    public StopHistory(String name, String placeId) {
        this.stopName = name;
        //this.timeStamp = time;
        this.placeId=placeId;
        //DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd | hh:mm:ss aa");
        //Date date = new Date(timeStamp);
        //stopTimeStr=dateFormat.format(date);

    }

    public void setStopName (String name) {
        stopName = name;
    }

    public void setStopTimeStr (String time) {
        stopTimeStr = time;
    }

    public String getStopName() {
        return stopName;
    }

    public String getStopTimeStr() {
        return stopTimeStr;
    }

    public String getPlaceId() {
        return placeId;
    }
    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;

    }



}
