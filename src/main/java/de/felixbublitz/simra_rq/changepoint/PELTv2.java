package de.felixbublitz.simra_rq.changepoint;

import javafx.util.Pair;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PELTv2 extends ChangepointAlgorithm {

    ArrayList<Double> F;
    ArrayList<ArrayList<Integer>> R;
    ArrayList<Integer> cp;
    double K;
    int addcount = 0;

    public PELTv2(double penaltyFactor) {
        super(penaltyFactor);
    }

    @Override
    protected ArrayList<Integer> perform() {
        ArrayList<Integer> changePoints = new ArrayList<Integer>();

        K = -getPenalty();
        F = new ArrayList<Double>();
        R = new ArrayList<ArrayList<Integer>>();
        cp = new ArrayList<Integer>();
        F.add(-getPenalty());
        cp.add(null);
        ArrayList<Integer> a = new ArrayList();
        a.add(0);
        R.add(a);
        R.add(a);



        for(int taus=1; taus<data.size(); taus++){
            Pair<Integer,Double> minp = getmin(taus);
            F.add(minp.getValue());
            cp.add(minp.getKey());
            R.add(getTaus(taus));
        }

        int i = cp.size()-1;
        while(i != 0){
            int cpo = cp.get(i);
            changePoints.add(i);
            i = cpo;
        }

        System.out.println("lol" +addcount);

        return changePoints;
    }


    private ArrayList<Integer> getTaus(int taus){
        ArrayList<Integer> out = new ArrayList<>();

        R.get(taus).add(taus);
        for(Integer tau : R.get(taus)){

            if( F.get(tau) + getCosts(tau+1,taus) + K <= F.get(taus)){
                addcount++;
                out.add(tau);
            }
        }


        return out;
    }


    private Pair<Integer, Double> getmin(int taus){
        double minCosts = Integer.MAX_VALUE;
        Pair<Integer, Double> out = null;

        for(Integer tau : R.get(taus)){
            double currCosts = F.get(tau) + getCosts(tau+1,taus) + getPenalty();
            if(currCosts < minCosts){
                minCosts = currCosts;
                out = new Pair(tau,currCosts);
            }

        }

        return out;
    }
}
