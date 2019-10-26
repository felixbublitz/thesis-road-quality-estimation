package de.felixbublitz.simra_rq.quality.segments;

import de.felixbublitz.simra_rq.changepoint.ChangepointAlgorithm;
import de.felixbublitz.simra_rq.changepoint.implementation.PELT;
import de.felixbublitz.simra_rq.quality.segments.DataSegment;

import java.util.ArrayList;

/**
 * Get segments of data by changepoint algorithms
 */

public class SegmentDetection {

    private static final int DEFAULT_PENALTY = 1;

    /**
     * Get partions of given data by default PELT algorithm
     * @param data data to be partitioned
     * @param samplingRate sampling rate of data
     * @return list of DataSegments
     */
    public static ArrayList<DataSegment> getSegments(ArrayList<Double> data, double samplingRate){
       return getSegments(data,  new PELT(DEFAULT_PENALTY, samplingRate));
    }

    /**
     * Get partitions of data by given changepoint algorithm
     * @param data data to be partitioned
     * @param algo changepoint algorithm
     * @return list of DataSegments
     */
    public static ArrayList<DataSegment> getSegments(ArrayList<Double> data, ChangepointAlgorithm algo){
        ArrayList<Integer> changePoints = algo.getChangepoints(data);
        ArrayList<DataSegment> segments = new ArrayList<DataSegment>();

        if(changePoints.size() == 0){
            segments.add(new DataSegment(0, data.size(), data));
            return segments;
        }

        segments.add(new DataSegment(0, changePoints.get(0), data));
        for(int i=0; i<changePoints.size()-1; i++){
            segments.add(new DataSegment(changePoints.get(i), changePoints.get(i+1), data));
        }
        segments.add(new DataSegment(changePoints.get(changePoints.size()-1), data.size()-1, data));

        return segments;
    }

}
