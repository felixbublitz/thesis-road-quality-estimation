package de.felixbublitz.simra_rq.track;

import de.felixbublitz.simra_rq.DebugHelper;
import de.felixbublitz.simra_rq.simra.GPSData;
import de.felixbublitz.simra_rq.simra.SimraData;
import de.felixbublitz.simra_rq.track.Road;

import java.util.ArrayList;

public class TrackSegment {
    private Road road;
    private int startX;
    private int endX;
    private int startPosition;
    private int endPosition;
    private SimraData data;


    public TrackSegment(Road road, SimraData data, int start, int end){
        this.road = road;
        this.startX = start;
        this.endX = end;
        this.getPositions(data);
    }

    public int getLength(){
        return Math.abs(startPosition-endPosition);
    }

    public Road getRoad(){
        return road;
    }

    private void getPositions(SimraData data){
        if(endX <= startX){
            return;
        }
        startPosition = road.getPosition(data.getGPSData(startX, true));
        endPosition = road.getPosition(data.getGPSData(endX, true));

    }

    public int getStartPosition(){
        return startPosition;
    }

    public int getEndPosition(){
        return endPosition;
    }

    public ArrayList<GPSData> getNodes(){
        return road.getNodes(startPosition, endPosition);
    }

    public int getStart(){
        return startX;
    }

    public int getEnd(){
        return endX;
    }



}

