package de.felixbublitz.simra_rq.quality;

import de.felixbublitz.simra_rq.etc.ListOperation;

import java.util.ArrayList;

public class DataSegment {
    private int start;
    private int end;
    private double variance;

    public DataSegment(int start, int end, ArrayList<Double> data){
        this.start = start;
        this.end = end;
        this.variance = ListOperation.getStandardDeviation(data.subList(start, end));
    }

    public double getVariance(){return variance;};
    public double getStandardDeviration(){return Math.sqrt(variance);};


    public int getStart(){
        return start;
    }

    public int getEnd(){
        return end;
    }


}
