package de.felixbublitz.simra_rq.changepoint.implementation;

import de.felixbublitz.simra_rq.changepoint.ChangepointAlgorithm;
import de.felixbublitz.simra_rq.etc.Pair;

import java.util.ArrayList;

/**
 * Optimal Partitioning changepoint algorithm
 */

public class OptimalPartitioning extends ChangepointAlgorithm {
    private ArrayList<Pair> optimal;

    ArrayList<Double> F;

    public OptimalPartitioning(double penalty, double samplingRate) {
        super(penalty, samplingRate);
    }

    /**
     * find changepoints
     * @return found changepoints
     */
    @Override
    protected ArrayList<Integer> perform() {
        ArrayList<Integer> changePoints = new ArrayList<Integer>();
        F = new ArrayList<>();
        F.add(-getPenalty());

       optimal = new ArrayList<Pair>();
       optimal.add(new Pair(0,-getPenalty()));


        for(int k=1; k<data.size();k++){
            Pair curr_opt = getOptimal(k);
            F.add((double)curr_opt.getData2());
            optimal.add(curr_opt);
        }

        int i = data.size()-1;

        while(i != 0){
            int curr = (int)optimal.get(i).getData1();
            changePoints.add(i);
            i=curr;
        }

        return changePoints;
    }

    /**
     * Get optimal changepoint for current positiion
     * @param k current position
     * @return optimal changepoint and costs for the partition
     */
    private Pair getOptimal(int k){
        double minCosts = Double.MAX_VALUE;
        Pair out = null;

        for(int i=0; i<k;i++){

            double currCosts = F.get(i) + getCosts(i+1,k) + getPenalty();

            if(currCosts < minCosts){
                minCosts = currCosts;
                out = new Pair(i, minCosts);
            }
        }
        return out;
    }
}
