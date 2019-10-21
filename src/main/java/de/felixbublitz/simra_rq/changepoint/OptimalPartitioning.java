package de.felixbublitz.simra_rq.changepoint;

import javafx.util.Pair;
import java.util.ArrayList;

public class OptimalPartitioning extends ChangepointAlgorithm {
    private ArrayList<Pair<Integer, Double>> optimal;

    ArrayList<Double> F;


    public OptimalPartitioning(double penalty) {
        super(penalty);
    }

    @Override
    protected ArrayList<Integer> perform() {
        ArrayList<Integer> changePoints = new ArrayList<Integer>();
        F = new ArrayList<>();
        F.add(-getPenalty());

       optimal = new ArrayList<Pair<Integer, Double>>();
       optimal.add(new Pair(0,-getPenalty()));


        for(int k=1; k<data.size();k++){
            Pair<Integer,Double> curr_opt = getOptimal(k);
            F.add(curr_opt.getValue());
            optimal.add(curr_opt);
        }

        int i = data.size()-1;

        while(i != 0){
            int curr = optimal.get(i).getKey();
            changePoints.add(i);
            i=curr;
        }

        return changePoints;
    }


    private Pair<Integer, Double> getOptimal(int k){
        double minCosts = Double.MAX_VALUE;
        Pair out = null;

        for(int i=0; i<k;i++){

            double currCosts = F.get(i) + getCosts(i+1,k) + getPenalty();

            if(currCosts < minCosts){
                minCosts = currCosts;
                out = new Pair<Integer,Double>(i, minCosts);
            }
        }
        return out;
    }
}
