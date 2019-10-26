package de.felixbublitz.simra_rq.track;

import de.felixbublitz.simra_rq.simra.SimraData;
import de.felixbublitz.simra_rq.track.road.Road;

public class TrackSegment {
    private Road road;
    private int startX;
    private int endX;
    private Integer startPosition;
    private Integer endPosition;
    private RoadPath roadPath;
    private SimraData data;

    public TrackSegment(Road road, SimraData data, int start, int end){
        this.road = road;
        this.startX = start;
        this.endX = end;
        this.roadPath = road.getPath(data, end);
        if(isValid()) {
            this.getPositions(data);
        }
    }

    public boolean isValid(){
       return roadPath == null ? false:true;
    }



    public int getLength(){
        if(startPosition == null || endPosition == null)
            return 0;
        return Math.abs(startPosition-endPosition);
    }

    public Road getRoad(){
        return road;
    }


    public RoadPath getRoadPath(){
        return roadPath;
    }
    public RoadPath getTrackPath(){
        return roadPath.getIntersection(startPosition,endPosition);
    }


    private void getPositions(SimraData data){
        if(endX <= startX){
             return;
        }

        startPosition = roadPath.getPosition(data.getGPSData(startX, true));
        endPosition = roadPath.getPosition(data.getGPSData(endX, true));

    }

    public Integer getStartPosition(){
        return startPosition;
    }

    public Integer getEndPosition(){
        return endPosition;
    }


    public int getStart(){
        return startX;
    }

    public int getEnd(){
        return endX;
    }



}

