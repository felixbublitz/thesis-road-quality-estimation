package de.felixbublitz.simra_rq.changepoint;

import javafx.util.Pair;

import java.util.ArrayList;

public class SegmentNeighbourhood extends ChangepointAlgorithm {
    private int kMax;
    private ArrayList<ArrayList<Pair<Integer, Double>>> c;

    public SegmentNeighbourhood(double penalty, int kMax, double penaltyFactor) {
        super(penalty, penaltyFactor);
        this.kMax = kMax;
    }

    @Override
    protected ArrayList<Integer> perform() {
        ArrayList<Integer> changePoints = new ArrayList<Integer>();
        c = new ArrayList<ArrayList<Pair<Integer, Double>>>();

        for(int i=0; i<data.size();i++){
            c.set(0, new ArrayList<Pair<Integer, Double>>());
            c.get(0).set(i, new Pair(0,getCosts(0,i)));
        }

        for(int k=1; k<kMax; k++){
            for(int t=k+1; t<data.size();t++){
                c.get(k).set(t, getMin(k,t));
            }
        }

        double minCosts = Double.MAX_VALUE;
        int currChangepointIndex = 0;

        for(int i=0; i<kMax;i++){
            double currCosts = c.get(i).get(data.size()-1).getValue();
            if(currCosts < minCosts){
                minCosts = currCosts;
                currChangepointIndex = c.get(i).get(data.size()-1).getKey();
            }
        }

        int i = currChangepointIndex;
        while(currChangepointIndex >= 1){
            currChangepointIndex = c.get(currChangepointIndex).get(i).getKey();
            changePoints.add(currChangepointIndex);
            currChangepointIndex = currChangepointIndex-1;
        }

        return changePoints;
    }


    private Pair<Integer, Double> getMin(int k, int t){
        double minCosts = Double.MAX_VALUE;
        Pair out = null;

        for(int pi = k; k<t; pi++){
            double currCosts = c.get(k-1).get(pi).getValue() + getCosts(pi+1, t);
            if(currCosts < minCosts){
                out = new Pair(pi, currCosts);
                minCosts = currCosts;
            }
        }

        return out;
    }
}
