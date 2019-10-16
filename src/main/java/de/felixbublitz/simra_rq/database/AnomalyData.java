package de.felixbublitz.simra_rq.database;

import de.felixbublitz.simra_rq.track.Road;

import java.util.Date;

public class AnomalyData {

    private Road road;
    private Date recorded;
    private int position;



    public AnomalyData(Road road, Date recorded, int position){
        this.road = road;
        this.recorded = recorded;
        this.position = position;
    }
}
