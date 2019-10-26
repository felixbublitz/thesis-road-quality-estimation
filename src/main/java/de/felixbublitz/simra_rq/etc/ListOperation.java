package de.felixbublitz.simra_rq.etc;

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


    public static double getSum(List data){
        double sum = 0;
        for(Object d : data)
            sum += Double.valueOf(String.valueOf(d));
        return sum;
    }

    public static double getStandardDeviation(List<Double> data){
        double m = getMean(data);
        double variance = 0;
        for(Double d : data)
            variance += Math.pow(m-d,2);
        return Math.sqrt(variance/data.size());
    }

}
