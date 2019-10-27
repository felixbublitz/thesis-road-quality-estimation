package de.felixbublitz.simra_rq.track;

import de.felixbublitz.simra_rq.simra.SimraData;
import de.felixbublitz.simra_rq.track.road.Road;
import de.felixbublitz.simra_rq.track.road.RoadPath;

/**
 * Part of Track. Represents the bridge between Segment of Track and Segment of Data
 */

public class TrackSegment {
    private Road road;
    private int startX;
    private int endX;
    private Integer startPosition;
    private Integer endPosition;
    private RoadPath roadPath;
    private SimraData data;

    /**
     * Creates a now track segment
     * @param road road
     * @param data data
     * @param start start of segment
     * @param end end of segment
     */
    public TrackSegment(Road road, SimraData data, int start, int end){
        this.road = road;
        this.startX = start;
        this.endX = end;
        this.roadPath = road.getRoadGeometry().getPath(end);
        if(isValid()) {
            this.getPositions(data);
        }
    }

    /**
     * Checks if track segment exists
     * @return validity
     */
    public boolean isValid(){
       return roadPath == null ? false:true;
    }

    /**
     * Get length of track segment in meter
     * @return length
     */
    public int getLength(){
        if(startPosition == null || endPosition == null)
            return 0;
        return Math.abs(startPosition-endPosition);
    }

    /**
     * Get road of track segment
     * @return road
     */
    public Road getRoad(){
        return road;
    }

    /**
     * Get Road Path on which track segment lays
     * @return road path
     */
    public RoadPath getRoadPath(){
        return roadPath;
    }

    /**
     * Get Path that can be used to draw track
     * @return track path
     */
    public RoadPath getTrackPath(){
        return roadPath.getIntersection(startPosition,endPosition);
    }

    /**
     * Estimate the start and end road position of tracksegment
     * @param data simradata object
     */
    private void getPositions(SimraData data){
        if(endX <= startX){
             return;
        }

        startPosition = roadPath.getPosition(data.getGPSData(startX, true));
        endPosition = roadPath.getPosition(data.getGPSData(endX, true));

    }

    /**
     * Get start road position of track
     * @return start position
     */
    public Integer getStartPosition(){
        return startPosition;
    }

    /**
     * Get start end position of track
     * @return end position
     */
    public Integer getEndPosition(){
        return endPosition;
    }

    /**
     * Get Start X Value of track
     * @return
     */
    public int getStart(){
        return startX;
    }

    /**
     * Get End X Value of track
     * @return
     */
    public int getEnd(){
        return endX;
    }

}

