package de.felixbublitz.simra_rq.changepoint.implementation;

import de.felixbublitz.simra_rq.changepoint.ChangepointAlgorithm;
import javafx.util.Pair;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * PELT changepoint algorithm
 */

public class PELT extends ChangepointAlgorithm {
    ArrayList<Double> F;
    HashMap<Integer, ArrayList<Integer>> R;
    private ArrayList<Pair<Integer, Double>> optimal;
    double K;

    /**
     * Create new Pelt Algorithm
     * @param penalty penalty factor
     * @param samplingRate sampling rate of data
     */
    public PELT(double penalty, double samplingRate) {
        super(penalty, samplingRate);
    }

    /**
     * find changepoints
     * @return found changepoints
     */
    @Override
    protected ArrayList<Integer> perform() {
        ArrayList<Integer> changePoints = new ArrayList<Integer>();

        R = new HashMap<Integer, ArrayList<Integer>>();
        F = new ArrayList<>();
        F.add(-getPenalty());
        K = 0;
        ArrayList<Integer> r1 = new ArrayList<>();
        r1.add(0);
        R.put(1, r1);

        optimal = new ArrayList<Pair<Integer, Double>>();
        optimal.add(new Pair(0,-getPenalty()));

        for(int k=1; k<data.size();k++){
            Pair<Integer,Double> curr_opt = getOptimal(k);
            F.add(curr_opt.getValue());
            optimal.add(curr_opt);
            R.put(k+1, getTaus(k));
        }

        int i = data.size()-1;

        while(i != 0){
            int curr = optimal.get(i).getKey();
            changePoints.add(i);
            i=curr;
        }

        return changePoints;
    }

    /**
     * Get list of possible changepoints to analyse for next step
     * @param taus current changepoint
     * @return list of possible changepoints for next step
     */
    private ArrayList<Integer> getTaus(int taus){
        ArrayList<Integer> out = new ArrayList<>();

        for(Integer tau : R.get(taus)){
            if( F.get(tau) + getCosts(tau+1,taus) + K <= F.get(taus)){
                out.add(tau);
            }
        }
        out.add(taus);


        return out;
    }

    /**
     * Get optimal changepoint at current point
     * @param k current point
     * @return best changepoint for k
     */
    private Pair<Integer, Double> getOptimal(int k){
        double minCosts = Double.MAX_VALUE;
        Pair out = null;

        for(int i : R.get(k)){
            double currCosts = F.get(i) + getCosts(i+1,k) + getPenalty();
            if(currCosts < minCosts){
                minCosts = currCosts;
                out = new Pair<Integer,Double>(i, minCosts);
            }
        }
        return out;
    }

}
