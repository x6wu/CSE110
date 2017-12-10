package com.teamruse.rarerare.tritontravel;

import com.google.android.gms.maps.model.LatLng;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Xinyu on 12/7/2017.
 */

public class Node extends Stop {
    public boolean visited;
    public boolean isBusStop;
    public ArrayList<Edge> edges;
    public Pair<String, Node> prev;

    public Node(String name, LatLng latLng){
        super(name, latLng);
        this.edges = new ArrayList<>();
        visited = false;
    }

    public void setPrev(Pair<String, Node> prev){
        this.prev = prev;
    }
}
