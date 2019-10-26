package de.felixbublitz.simra_rq.track.road;

import de.felixbublitz.simra_rq.simra.GPSData;

public class RoadNode {
    private GPSData gps;
    private int position;

    public RoadNode(GPSData gps, int position){
        this.gps = gps;
        this.position = position;
    }

    public GPSData getGPSData(){
        return gps;
    }

    public int getPosition(){
        return position;
    }
}
