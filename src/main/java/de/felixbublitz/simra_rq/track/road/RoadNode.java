package de.felixbublitz.simra_rq.track.road;

import de.felixbublitz.simra_rq.simra.GPSData;

/**
 * Represents a node of a road path
 */

public class RoadNode {
    private GPSData gps;
    private int position;

    /**
     * Create Road Node
     * @param gps GPS position
     * @param position Road position
     */
    public RoadNode(GPSData gps, int position){
        this.gps = gps;
        this.position = position;
    }

    /**
     * Returns gps position
     * @return gps position
     */
    public GPSData getGPSData(){
        return gps;
    }

    /**
     * Returns road position
     * @return road position
     */
    public int getPosition(){
        return position;
    }
}
