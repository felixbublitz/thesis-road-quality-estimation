package de.felixbublitz.simra_rq.etc;

import de.felixbublitz.simra_rq.simra.GPSData;

import java.util.ArrayList;
import java.util.List;

/**
 * Basic list operations
 */

public class ListOperation {

    /**
     * Get mean of a list
     * @param data data
     * @return mean of list
     */
    public static double getMean(List<Double> data){
        double values = 0;
        for(Double d : data)
            values +=d;
        return values/data.size();
    }

    /**
     * Get variance of a list
     * @param data data
     * @return variance
     */
    public static double getVariance(List<Double> data){
        double m = getMean(data);
        double variance = 0;
        for(Double d : data)
            variance += Math.pow(m-d,2);
        return variance/data.size();
    }

    /**
     * Get sum of a list
     * @param data data
     * @return sum of list
     */
    public static double getSum(List data){
        double sum = 0;
        for(Object d : data)
            sum += Double.valueOf(String.valueOf(d));
        return sum;
    }

    /**
     * Get standard deviation of a list
     * @param data data
     * @return standard deviation of list
     */
    public static double getStandardDeviation(List<Double> data){
        double m = getMean(data);
        double variance = 0;
        for(Double d : data)
            variance += Math.pow(m-d,2);
        return Math.sqrt(variance/data.size());
    }

}
