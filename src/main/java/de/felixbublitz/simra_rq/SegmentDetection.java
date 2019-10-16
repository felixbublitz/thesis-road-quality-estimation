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
        ArrayList<Integer> changePoints = algo.getChangepoints(data);
        ArrayList<DataSegment> segments = new ArrayList<DataSegment>();

        segments.add(new DataSegment(0, changePoints.get(0), data));
        for(int i=0; i<changePoints.size()-1; i++){
            segments.add(new DataSegment(changePoints.get(i), changePoints.get(i+1), data));
        }
        segments.add(new DataSegment(changePoints.get(changePoints.size()-1), data.size()-1, data));

        return segments;
    }





}
