package de.felixbublitz.simra_rq.quality.anomaly;

import de.felixbublitz.simra_rq.etc.ListOperation;
import de.felixbublitz.simra_rq.quality.segments.DataSegment;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Detection of Anomalies
 */

public class AnomalyDetection {

    /**
     * Detect anomalies on segments of data by finding swings
     * @param data data to analyse for peaks
     * @param segments partitions of data by variance
     * @param threshold threshold value
     * @param eliminationDistance elimination distance in seconds around anomaly
     * @return
     */
    public static ArrayList<Integer> getPeaks(ArrayList<Double> data, ArrayList<DataSegment> segments, double threshold, int eliminationDistance){
        long startTime = System.currentTimeMillis();
        double variance = ListOperation.getVariance(data);
        double mean = ListOperation.getMean(data);

        ArrayList<Integer> peakCandidates = new ArrayList<Integer>();
        ArrayList<Integer> peaks = new ArrayList<Integer>();

        for(DataSegment segment : segments){
            for(int i=segment.getStart(); i<segment.getEnd(); i++){
                if(Math.abs(data.get(i) - mean) - threshold*variance > 0)
                    peakCandidates.add(i);
            }

            while(peakCandidates.size() != 0){
                int largestPeakIndex = getLargestPeak(data, peakCandidates);
                peaks.add(peakCandidates.get(largestPeakIndex));
                cleanPeakCandidates(peakCandidates, largestPeakIndex, eliminationDistance);
            }

        }

        Collections.sort(peaks);

        System.out.println("AD Runtime: " + (System.currentTimeMillis() - startTime) + "ms");
        return peaks;

    }

    /**
     * delete all peaks from peak candidates list, which are within the elimination area
     * @param peakCandidates all peak candidates
     * @param peakIndex index of a peak
     * @param eliminationDistance elimination distance in seconds around anomaly
     */
    private static void cleanPeakCandidates(ArrayList<Integer> peakCandidates, int peakIndex, int eliminationDistance){
        int i = 0;
        double lvalue = peakCandidates.get(peakIndex);

        while(i < peakCandidates.size()){
            if(Math.abs(peakCandidates.get(i) - lvalue) < eliminationDistance){
                peakCandidates.remove(i);
            }else{
                i++;
            }

        }
    }

    /**
     *
     * @param data
     * @param peakCandidates
     * @return
     */
    private static int getLargestPeak(ArrayList<Double> data, ArrayList<Integer> peakCandidates){
        double max = 0;
        int index = 0;
        for(int i=0; i<peakCandidates.size();i++){
            if(data.get(peakCandidates.get(i))> max){
                max = data.get(peakCandidates.get(i));
                index = i;
            }
        }
        return index;
    }

    /**
     * Detect anomalies by evasion movements
     * @param data
     * @return
     */
    public static ArrayList<Integer> getEvasions(ArrayList<Double> data){
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }


}
