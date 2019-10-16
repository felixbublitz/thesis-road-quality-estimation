package de.felixbublitz.simra_rq;

import java.util.ArrayList;
import java.util.List;

public class ListOperation {
    public static double getMean(List<Double> data){
        double values = 0;
        for(Double d : data)
            values +=d;
        return values/data.size();
    }

    public static double getVariance(List<Double> data){
        double m = getMean(data);
        double variance = 0;
        for(Double d : data)
            variance += Math.pow(m-d,2);
        return variance/data.size();
    }

    public static List<Double> getVariances(ArrayList<Double> data){
        ArrayList<Double> variances = new ArrayList<Double>();
        double m = getMean(data);
        for(Double d : data)
            variances.add(Math.pow(m-d,2));

        return variances;
    }

    public static double getSum(List<Double> data){
        double sum = 0;
        for(Double d : data)
            sum += d;
        return sum;
    }

    public static double getStandardDeviation(List<Double> data){
        double m = getMean(data);
        double variance = 0;
        for(Double d : data)
            variance += Math.pow(variance-d,2);
        return variance/data.size();
    }

}
