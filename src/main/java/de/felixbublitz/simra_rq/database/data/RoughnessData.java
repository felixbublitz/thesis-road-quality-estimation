package de.felixbublitz.simra_rq.database;

import de.felixbublitz.simra_rq.track.Road;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RoughnessData  {
    private Road road;
    private Date recorded;
    private double variance;

    public Road getRoad() {
        return road;
    }

    public Date getRecordingDate() {
        return recorded;
    }

    public String getRecordingDate(String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(recorded);
    }

    public double getVariance() {
        return variance;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

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
