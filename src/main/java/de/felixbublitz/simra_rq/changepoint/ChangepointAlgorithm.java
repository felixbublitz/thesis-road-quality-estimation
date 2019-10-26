package de.felixbublitz.simra_rq.changepoint;

import de.felixbublitz.simra_rq.etc.ListOperation;
import java.util.ArrayList;
import java.util.Collections;

/**
 * changepoint algorithm super class
 */

public abstract class ChangepointAlgorithm {

    protected double mean = 0;
    private double penaltyFactor;
    protected ArrayList<Double> data;
    private double samplingRate;

    /**
     * create new changepoint algorithm
     * @param penaltyFactor penalty factor
     * @param samplingRate sampling rate of data
     */
    public ChangepointAlgorithm(double penaltyFactor, double samplingRate){
        this.penaltyFactor = penaltyFactor;
        this.samplingRate = samplingRate;
    }

    /**
     * Get all changepoints of given data
     * @param data data to analyse for changepoints
     * @return founded changepoints
     */
    public ArrayList<Integer> getChangepoints(ArrayList<Double> data){
        this.mean = ListOperation.getMean(data);
        this.data = data;

        long startTime = System.currentTimeMillis();
        ArrayList<Integer> changePoints = perform();
        System.out.println("CP Runtime: " + (System.currentTimeMillis() - startTime) + "ms");
        Collections.sort(changePoints);

        if(changePoints.get((0)) == 0)
            changePoints.remove(0);
        if(changePoints.get(changePoints.size()-1) == data.size()-1)
            changePoints.remove(changePoints.size()-1);

        return changePoints;
    }

    /**
     * perform the changepoint algorithm
     * @return found changepoints
     */

    protected ArrayList<Integer> perform(){
        return null;
    }

    /**
     * Get the penalty
     * @return penalty
     */
    protected double getPenalty(){
        return (1/samplingRate) * penaltyFactor ;
    }


    /**
     * Get costs of partition data in segment s to t
     * @param s start point
     * @param t end point
     * @return costs
     */
    protected double getCosts(int s, int t) {

        if(t<=s){
            return 0;
        }
        double sum = 0;
        double mu = ListOperation.getMean(data.subList(s, t));
        double var = ListOperation.getVariance(data.subList(s, t));

        for (int i = s; i < t; i++) {
            double p1 = Math.abs(data.get(i) - mu);
            double p2 = Math.sqrt(var);
            sum +=Math.abs(p1-p2);
        }

        if(var == 0){
            return 0;
        }

        sum = sum/Math.sqrt(var);
        return sum;
    }

}
