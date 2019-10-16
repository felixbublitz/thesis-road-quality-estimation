package de.felixbublitz.simra_rq.data;

import de.felixbublitz.simra_rq.ListOperation;
import de.felixbublitz.simra_rq.data.simra.SimraData;

import java.util.ArrayList;

public class DataSegment {
    private int start;
    private int end;
    private double variance;

    public DataSegment(int start, int end, ArrayList<Double> data){
        this.start = start;
        this.end = end;
        this.variance = ListOperation.getVariance(data.subList(start, end));
    }

    public double getVariance(){return variance;};
    public int getStart(){
        return start;
    }

    public int getEnd(){
        return end;
    }


}
