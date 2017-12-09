package com.teamruse.rarerare.tritontravel;

/**
 * Created by Ruoyu Xu
 */

public class StopHistoryWithKey extends StopHistory{


    private String key;

    public StopHistoryWithKey(){
        super();
    }
    public StopHistoryWithKey(String name, long time, String placeId) {
        super(name, time, placeId);

    }
    public StopHistoryWithKey(String name, String placeId, String inputTag) {
        super(name, placeId, inputTag);

    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }




}
