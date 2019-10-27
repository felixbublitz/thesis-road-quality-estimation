package de.felixbublitz.simra_rq.changepoint.implementation;

import de.felixbublitz.simra_rq.changepoint.ChangepointAlgorithm;
import de.felixbublitz.simra_rq.etc.Pair;

import java.util.ArrayList;

/**
 * Segment Neighbourhood changepoint algorithm
 */

public class SegmentNeighbourhood extends ChangepointAlgorithm {
    private int kMax;
    private ArrayList<ArrayList<Pair>> c;

    public SegmentNeighbourhood(double penalty, int kMax, double penaltyFactor) {
        super(penalty, penaltyFactor);
        this.kMax = kMax;
    }

    /**
     * find changepoints
     * @return found changepoints
     */
    @Override
    protected ArrayList<Integer> perform() {
        ArrayList<Integer> changePoints = new ArrayList<Integer>();
        c = new ArrayList<ArrayList<Pair>>();

        for(int i=0; i<data.size();i++){
            c.set(0, new ArrayList<Pair>());
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
            double currCosts = (double)c.get(i).get(data.size()-1).getData2();
            if(currCosts < minCosts){
                minCosts = currCosts;
                currChangepointIndex = (int)c.get(i).get(data.size()-1).getData1();
            }
        }

        int i = currChangepointIndex;
        while(currChangepointIndex >= 1){
            currChangepointIndex = (int)c.get(currChangepointIndex).get(i).getData1();
            changePoints.add(currChangepointIndex);
            currChangepointIndex = currChangepointIndex-1;
        }

        return changePoints;
    }


    private Pair getMin(int k, int t){
        double minCosts = Double.MAX_VALUE;
        Pair out = null;

        for(int pi = k; k<t; pi++){
            double currCosts = (double)c.get(k-1).get(pi).getData2() + getCosts(pi+1, t);
            if(currCosts < minCosts){
                out = new Pair(pi, currCosts);
                minCosts = currCosts;
            }
        }

        return out;
    }
}
