package de.felixbublitz.simra_rq.quality.segments;

import de.felixbublitz.simra_rq.etc.ListOperation;

import java.util.ArrayList;

/**
 * Segment of a dataset
 */

public class DataSegment {
    private int start;
    private int end;
    private double variance;

    /**
     * Creates a new DataSegment object
     * @param start start of segment
     * @param end end of segment
     * @param data data source
     */
    public DataSegment(int start, int end, ArrayList<Double> data){
        this.start = start;
        this.end = end;
        this.variance = ListOperation.getStandardDeviation(data.subList(start, end));
    }

    /**
     * Returns variance of segment
     * @return variance
     */
    public double getVariance(){return variance;};

    /**
     * Returns standard deviation of segment
     * @return standard deviation
     */
    public double getStandardDeviation(){return Math.sqrt(variance);};

    /**
     * Returns start of segment
     * @return start point
     */
    public int getStart(){
        return start;
    }

    /**
     * Returns end of segment
     * @return end point
     */
    public int getEnd(){
        return end;
    }
}
