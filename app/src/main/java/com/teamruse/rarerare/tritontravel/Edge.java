package com.teamruse.rarerare.tritontravel;

/**
 * Created by Xinyu on 12/7/2017.
 */

public class Edge {
    public Node from;
    public Node to;
    public int weight;
    public String headsign;

    public Edge(Node from, Node to, String headsign){
        this.from = from;
        this.to = to;
        this.weight = 1;
        this.headsign = headsign;
    }

    public void setWeight(int weight){
        this.weight = weight;
    }
}
