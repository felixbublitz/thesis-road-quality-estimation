package de.felixbublitz.simra_rq;

import de.felixbublitz.simra_rq.changepoint.ChangepointAlgorithm;
import de.felixbublitz.simra_rq.changepoint.PELT;
import de.felixbublitz.simra_rq.data.DataSegment;

import java.util.ArrayList;
import java.util.List;

public class SegmentDetection {


    public static ArrayList<DataSegment> getSegments(ArrayList<Double> data){
       return getSegments(data,  new PELT(1));
    }

    public static ArrayList<DataSegment> getSegments(ArrayList<Double> data, ChangepointAlgorithm algo){
        algo.getChangepoints(data);

        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }


}
