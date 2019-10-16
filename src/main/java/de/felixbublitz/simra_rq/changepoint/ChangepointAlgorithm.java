package de.felixbublitz.simra_rq.changepoint;

import de.felixbublitz.simra_rq.etc.ListOperation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ChangepointAlgorithm {

    protected double mean = 0;
    protected List<Double> variances;
    private double penaltyFactor;
    protected ArrayList<Double> data;

    public ChangepointAlgorithm(int penaltyFactor){
        this.penaltyFactor = penaltyFactor;
    }

    public ArrayList<Integer> getChangepoints(ArrayList<Double> data){
        this.mean = ListOperation.getMean(data);
        this.variances = ListOperation.getVariances(data);
        this.data = data;

        ArrayList<Integer> changePoints = perform();
        Collections.sort(changePoints);

        if(changePoints.get((0)) == 0)
            changePoints.remove(0);
        if(changePoints.get(changePoints.size()-1) == data.size()-1)
            changePoints.remove(changePoints.size()-1);

        return changePoints;
    }

    protected ArrayList<Integer> perform(){
        return null;
    }

    protected double getPenalty(){
        return penaltyFactor * Math.log(data.size());
    }



    protected double getCosts(int s, int t){
        double sig = ListOperation.getVariance(data.subList(s,t));
        double yt = 0;
        if(sig == sig){
            double bloc = Math.log(2*Math.PI*sig);
            yt = ListOperation.getSum(variances.subList(s,t));
            yt = yt/sig + (t-s)*bloc;
        }
        return yt;
    }



}
