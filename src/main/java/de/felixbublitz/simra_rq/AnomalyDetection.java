package de.felixbublitz.simra_rq;

import de.felixbublitz.simra_rq.data.DataSegment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class AnomalyDetection {

    public static ArrayList<Integer> getPeaks(ArrayList<Double> data, ArrayList<DataSegment> segments, double threshold, int eliminationDistance){
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
                int largest = getLargestPeak(data, peakCandidates);
                peaks.add(peakCandidates.get(largest));
                cleanPeakCandidates(peakCandidates, largest, eliminationDistance);
            }

        }

        Collections.sort(peaks);

        return peaks;

    }

    private static void cleanPeakCandidates(ArrayList<Integer> peakCandidates, int largest, int eliminationDistance){
        int i = 0;
        double lvalue = peakCandidates.get(largest);

        while(i < peakCandidates.size()){
            if(Math.abs(peakCandidates.get(i) - lvalue) < eliminationDistance){
                peakCandidates.remove(i);
            }else{
                i++;
            }

        }
    }

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

    public static ArrayList<Integer> getEvasions(ArrayList<Double> data){
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }


}
