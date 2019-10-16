package de.felixbublitz.simra_rq;

import de.felixbublitz.simra_rq.data.DataSegment;

import java.util.ArrayList;

public class AnomolieDetection {

    public static ArrayList<Integer> getPeaks(ArrayList<Double> data, ArrayList<DataSegment> segments, double threshold, int eliminationDistance){
        double standardDeviation = ListOperation.getStandardDeviation(data);
        ArrayList<Integer> peakCandidates = new ArrayList<Integer>();
        ArrayList<Integer> peaks = new ArrayList<Integer>();

        for(DataSegment segment : segments){
            for(int i=segment.getStart(); i<segment.getEnd(); i++){
                if(data.get(i) > threshold*standardDeviation)
                    peakCandidates.add(i);
            }

            while(peakCandidates.size() != 0){
                int largest = getLargestPeak(data, peakCandidates);
                peaks.add(largest);
                cleanPeakCandidates(peakCandidates, largest, eliminationDistance);
            }

        }

        return peaks;

    }

    private static void cleanPeakCandidates(ArrayList<Integer> peakCandidates, int largest, int eliminationDistance){
        int i = 0;

        while(i < peakCandidates.size()){
            if(Math.abs(peakCandidates.get(i) - largest) < eliminationDistance){
                peakCandidates.remove(i);
            }else{
                i++;
            }

        }
    }

    private static int getLargestPeak(ArrayList<Double> data, ArrayList<Integer> peakCandidates){
        double max = 0;
        int index = 0;
        for(int i=0; i<data.size();i++){
            if(data.get(i)> max){
                max = data.get(i);
                index = i;
            }
        }
        return index;
    }

    public static ArrayList<Integer> getEvasions(ArrayList<Double> data){
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }


}
