package de.felixbublitz.simra_rq.changepoint;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;

public class PELT extends ChangepointAlgorithm{
    ArrayList<Double> F;
    HashMap<Integer, ArrayList<Integer>> R;
    private ArrayList<Pair<Integer, Double>> optimal;
    double K;

    public PELT(double penalty, double samplingRate) {
        super(penalty, samplingRate);
    }

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
