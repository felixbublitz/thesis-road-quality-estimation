package de.felixbublitz.simra_rq.database;

import de.felixbublitz.simra_rq.track.Road;

import java.util.Date;

public class AnomalyData {

    private Road road;
    private Date recorded;
    private int position;

    public Road getRoad() {
        return road;
    }

    public Date getRecorded() {
        return recorded;
    }

    public int getPosition() {
        return position;
    }

    public AnomalyData(Road road, Date recorded, int position){
        this.road = road;
        this.recorded = recorded;
        this.position = position;
    }
}
