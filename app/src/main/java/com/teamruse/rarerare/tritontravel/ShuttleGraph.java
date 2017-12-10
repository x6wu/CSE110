package com.teamruse.rarerare.tritontravel;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * Created by Xinyu on 12/7/2017.
 */

public class ShuttleGraph {
    public ArrayList<Edge> edges;
    public HashMap<LatLng, Node> nodes;
    public HashMap<String, Node> idMap;


    public ShuttleGraph(){
        edges = new ArrayList<>();
        nodes = new HashMap<>();
        idMap = new HashMap<>();
    }

    public boolean loadFromFile(Context context){
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open("ShuttleStop.txt");
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                /*
                String[] substrings = inputLine.split(":");
                String line = substrings[0];
                String stopName = substrings[1];
                String stopId = substrings[2];
                */
                String[] substrings = inputLine.split(":");
                String stopId = substrings[0];
                String stopName = substrings[1];
                String latLngStr = substrings[2];
                LatLng latLng = new LatLng(Double.parseDouble(latLngStr.split(",")[0]),
                        Double.parseDouble(latLngStr.split(",")[1]));
                addNode(stopId, stopName, latLng);
            }
            inputStream.close();
            in.close();
            inputStream = assetManager.open("lines.txt");
            in = new BufferedReader(new InputStreamReader(inputStream));
            String prevHeadsign = null;
            String currHeadsign = null;
            ArrayList<Node> stops = new ArrayList<>();
            while((inputLine = in.readLine()) != null){
                String[] substrings = inputLine.split(":");
                String line = substrings[0];
                String stopName = substrings[1];
                String stopId = substrings[2];
                prevHeadsign = currHeadsign;
                currHeadsign = line;
                if(prevHeadsign == null){
                    prevHeadsign = currHeadsign;
                }
                if(currHeadsign.equals(prevHeadsign)){
                    if(idMap.containsKey(stopId)) {
                        if(nodes.containsKey(idMap.get(stopId).getLatLng())){
                            Node currNode = nodes.get(idMap.get(stopId).getLatLng());
                            stops.add(currNode);
                        }
                    }
                }
                else {
                    for(int index = 0; index < stops.size()-1; ++index) {
                        Node currNode = stops.get(index);
                        Node nextNode = stops.get(index+1);
                        addEdge(currNode, nextNode, prevHeadsign);
                    }
                    addEdge(stops.get(stops.size()-1), stops.get(0), prevHeadsign);
                    stops.clear();
                    if(idMap.containsKey(stopId)) {
                        if(nodes.containsKey(idMap.get(stopId).getLatLng())){
                            Node currNode = nodes.get(idMap.get(stopId).getLatLng());
                            stops.add(currNode);
                        }
                    }
                }
            }
            if(stops.size() > 0){
                for(int index = 0; index < stops.size()-1; ++index) {
                    Node currNode = stops.get(index);
                    Node nextNode = stops.get(index+1);
                    addEdge(currNode, nextNode, prevHeadsign);
                }
                addEdge(stops.get(stops.size()-1), stops.get(0), prevHeadsign);
            }
            return true;
        }catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    };

    public void addNode(String id, String stopName, LatLng latLng){
        Node newNode = new Node(stopName, latLng);
        if(!idMap.containsKey(id)){
            idMap.put(id, newNode);
        }
        if(!nodes.containsKey(latLng)){
            nodes.put(latLng, newNode);
        }
    }

    public void addEdge(Node from, Node to, String headsign){
        Edge newEdge = new Edge(from, to, headsign);
        from.edges.add(newEdge);
    }

    public ArrayList<Path> generateShuttleRoutes(LatLng startLocation, LatLng endLocation){
        ArrayList<Path> paths = new ArrayList<>();
        ArrayList<Node> sourceNodes = new ArrayList<>();
        ArrayList<Node> destinationNodes = new ArrayList<>();
        for(LatLng curr:nodes.keySet()){
            Log.d("curr", curr.toString());
            Log.d("start", startLocation.toString());
            Log.d("node size", ""+MapUtils.distance(curr, startLocation));
            if(MapUtils.distance(curr, startLocation) <= 0.5){
                Log.d("node size", "test");
            }
            if(MapUtils.distance(curr, endLocation) <= 0.5){
                Log.d("node size", "test");
            }
        }
        Log.d("node size", sourceNodes.size()+"");
        Log.d("node size", destinationNodes.size()+"");
        for(int i = 0; i < sourceNodes.size(); ++i){
            DFS(sourceNodes.get(i));
            for(int j = 0; j < destinationNodes.size(); ++j){
                if(destinationNodes.get(j).visited) {
                    Path newPath = new Path(startLocation, endLocation, "", "");
                    ArrayList<PathSegment> segments = newPath.getPathSegments();
                    if(outputPath(sourceNodes.get(i), destinationNodes.get(j)) != null) {
                        if(outputPath(sourceNodes.get(i), destinationNodes.get(j)).size() == 0){
                            Log.d("size 0", sourceNodes.get(i).getName());
                            Log.d("size 0", destinationNodes.get(j).getName());
                        }
                        segments.addAll(outputPath(sourceNodes.get(i), destinationNodes.get(j)));
                        WalkingSegment walkingSegment1 = new WalkingSegment(startLocation, segments.get(0).getStartLocation(),
                                "", "", SegmentFactory.TravelMode.WALKING);
                        WalkingSegment walkingSegment2 = new WalkingSegment(segments.get(segments.size() - 1).getEndLocation(),
                                endLocation, "", "", SegmentFactory.TravelMode.WALKING);
                        segments.add(0, walkingSegment1);
                        segments.add(walkingSegment2);
                        newPath.setPathSegments(segments);
                        paths.add(newPath);
                    }
                }
            }
        }
        return paths;
    }

    private void DFS(Node from){
        clear();
        Node currNode;
        Deque<Node> queue = new ArrayDeque<>();
        queue.push(from);
        while(!queue.isEmpty()){
            currNode = queue.peek();
            queue.pop();
            for(int index = 0; index < currNode.edges.size(); ++index){
                Edge currEdge = currNode.edges.get(index);
                if(!currEdge.to.visited){
                    currEdge.to.visited = true;
                    currEdge.to.setPrev(new Pair<>(currEdge.headsign, currNode));
                    queue.push(currEdge.to);
                }
            }
        }
    }

    private ArrayList<ShuttleSegment> outputPath(Node from, Node to){
        if(!to.visited){
            return null;
        }
        if(from.getLatLng().equals(to.getLatLng())){
            return null;
        }
        else{
            //TODO
            Deque<Pair<String, Node>> connections = new ArrayDeque<>();
            ArrayList<ShuttleSegment> shuttleSegments = new ArrayList<>();
            Node curr = to;
            while(!curr.getLatLng().equals(from.getLatLng())){
                connections.push(curr.prev);
                curr = curr.prev.getSecond();
            }
            Log.d("size zero", connections.size()+"");
            while(!connections.isEmpty()){
                Pair<String, Node> top = connections.peek();
                LatLng startLocation = top.getSecond().getLatLng();
                String startStop = top.getSecond().getName();
                connections.pop();
                LatLng endLocation;
                String endStop;
                if(!connections.isEmpty()) {
                    endLocation = connections.peek().getSecond().getLatLng();
                    endStop = connections.peek().getSecond().getName();
                }
                else{
                    endLocation = to.getLatLng();
                    endStop = to.getName();
                }
                ShuttleSegment segment = new ShuttleSegment(startLocation, endLocation, "",
                        "", SegmentFactory.TravelMode.SHUTTLE);
                segment.setNumStops(1);
                segment.setStartStop(startStop);
                segment.setShuttleHeadsign(top.getFirst());
                segment.setEndStop(endStop);
                shuttleSegments.add(segment);
            }
            return compressPath(shuttleSegments);
        }
    }

    public ArrayList<ShuttleSegment> compressPath(ArrayList<ShuttleSegment> shuttleSegments){
        ArrayList<ShuttleSegment> compressedShuttleSegments = new ArrayList<>();
        int numStops = 0;
        int startIndex = 0;
        int endIndex = startIndex + 1;
        if(shuttleSegments.size() == 1){
            return shuttleSegments;
        }
        while(endIndex < shuttleSegments.size()) {
            if (shuttleSegments.get(startIndex).getShuttleHeadsign()
                    .equals(shuttleSegments.get(endIndex).getShuttleHeadsign())) {
                endIndex++;
            }
            else{
                ShuttleSegment compressedSegment = new ShuttleSegment
                        (shuttleSegments.get(startIndex).getStartLocation(), shuttleSegments.get(endIndex-1).getEndLocation(),
                                "", "", SegmentFactory.TravelMode.SHUTTLE);
                compressedSegment.setStartStop(shuttleSegments.get(startIndex).getStartStop());
                compressedSegment.setEndStop(shuttleSegments.get(endIndex-1).getEndStop());
                compressedSegment.setShuttleHeadsign(shuttleSegments.get(startIndex).getShuttleHeadsign());
                compressedSegment.setNumStops(endIndex-startIndex);
                compressedShuttleSegments.add(compressedSegment);
                startIndex = endIndex;
                endIndex = startIndex + 1;

            }
            if(endIndex >= shuttleSegments.size()){
                ShuttleSegment compressedSegment = new ShuttleSegment(shuttleSegments.get(startIndex).getStartLocation(),
                        shuttleSegments.get(shuttleSegments.size()-1).getEndLocation(), "", "",
                        SegmentFactory.TravelMode.SHUTTLE);
                compressedSegment.setStartStop(shuttleSegments.get(startIndex).getStartStop());
                compressedSegment.setEndStop(shuttleSegments.get(shuttleSegments.size()-1).getEndStop());
                compressedSegment.setShuttleHeadsign(shuttleSegments.get(startIndex).getShuttleHeadsign());
                compressedSegment.setNumStops(endIndex-startIndex);
                compressedShuttleSegments.add(compressedSegment);
            }
        }
        Log.d("compressed", ""+compressedShuttleSegments.size());
        return compressedShuttleSegments;
    }
    public void clear(){
        for(Node curr: nodes.values()){
            curr.visited = false;
            curr.setPrev(null);
        }
    }
}
