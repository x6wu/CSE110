package com.teamruse.rarerare.tritontravel;

/**
 * Created by JingJing on 11/29/17.
 */

public class StopHistory {

    private String stopName;
    private String stopTime;

    public StopHistory() {
        stopName = "";
        stopName = "";

    }

    public StopHistory(String name, String time) {
        stopName = name;
        stopTime = time;
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


}
