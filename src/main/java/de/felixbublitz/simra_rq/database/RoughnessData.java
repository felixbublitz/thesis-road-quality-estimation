package de.felixbublitz.simra_rq.database;

import de.felixbublitz.simra_rq.track.Road;

import java.util.Date;

public class RoughnessData  {
    private Road road;
    private Date recorded;
    private double variance;
    private int start;
    private int end;



    public RoughnessData(Road road, Date recorded, int start, int end, double variance){
        this.road = road;
        this.recorded = recorded;
        this.start = start;
        this.end = end;
        this.variance = variance;
    }
}
