package de.felixbublitz.simra_rq.changepoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class PELT extends ChangepointAlgorithm {

    List<ArrayList<Integer>> r;
    List<Double> f;
    List<Tuple> futureTau;
    double k = -getPenalty(); //0?

    public PELT(int penalty) {
        super(penalty);
    }

    @Override
    protected ArrayList<Integer> perform() {
        r = new ArrayList<ArrayList<Integer>>();
        f = new ArrayList<Double>();
        futureTau = new ArrayList<Tuple>();
        ArrayList<Integer> changePoints = new ArrayList<Integer>();


        List<Integer> lastChange = new ArrayList<Integer>();
        lastChange.add(0);

        ArrayList<Integer> first = new ArrayList<Integer>();
        first.add(0);
        r.add(first);


        f.add(-getPenalty());

        for(int taust=1; taust<data.size(); taust++){
            int tauone = getCurrentTau(taust);
            lastChange.add(tauone);
            r.add(getNextTaus(taust));
        }

        int i = data.size()-2;

        while(i != 0) {
            int curr = lastChange.get(i);
            if (curr != 0)
                changePoints.add(curr);
            i = curr;
        }
       return changePoints;
    }


    private ArrayList<Integer> getNextTaus(int taust){
        ArrayList<Integer> out = new ArrayList<Integer>();
        futureTau.add(new Tuple(taust, f.get(taust) + getCosts(taust+1,taust)+k));

        for(Tuple t : futureTau){
            if(t.costs <= f.get(taust)){
                out.add(t.tau);
            }
        }
        return out;
    }

    private int getCurrentTau(int taust){
        double min = Double.MAX_VALUE;
        int tauone = 0;

        for(int tau : r.get(taust)){
            double costs = f.get(tau) + getCosts(tau+1, taust);

            if(costs + k <= min)
                futureTau.add(new Tuple(tau, costs+k));
            if(costs + getPenalty() <= min){
                min = costs + getPenalty();
                tauone = tau;
            }
        }
        return tauone;
    }


}
