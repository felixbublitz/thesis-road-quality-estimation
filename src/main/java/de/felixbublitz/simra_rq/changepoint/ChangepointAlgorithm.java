package de.felixbublitz.simra_rq.changepoint;

import de.felixbublitz.simra_rq.SegmentDetection;

import java.util.ArrayList;
import java.util.List;

public abstract class ChangepointAlgorithm {

    protected double mean = 0;
    protected List<Double> variances;
    protected double penalty;
    protected ArrayList<Double> data;

    public ChangepointAlgorithm(int penalty){
        this.penalty = penalty;
    }

    public List<Integer> getChangepoints(ArrayList<Double> data){
        this.mean = getMean(data);
        this.variances = getVariances(data);
        this.data = data;

        return perform();
    }

    protected ArrayList<Integer> perform(){
        return null;
    }

    protected double getMean(List<Double> data){
        double values = 0;
        for(Double d : data)
            values +=d;
        return values/data.size();
    }

    protected double getVariance(List<Double> data){
        double m = getMean(data);
        double variance = 0;
        for(Double d : data)
            variance += Math.pow(variance-d,2);
        return variance/data.size();
    }

    protected List<Double> getVariances(ArrayList<Double> data){
        ArrayList<Double> variances = new ArrayList<Double>();
        double m = getMean(data);
        for(Double d : data)
            variances.add(Math.pow(m-d,2));

        return variances;
    }

    protected double getSum(List<Double> data){
        double sum = 0;
        for(Double d : data)
            sum += d;
        return sum;
    }

    protected double getCosts(int s, int t){
        double sig = getVariance(data.subList(s,t));
        double yt = 0;
        if(sig != 0){
            double bloc = Math.log(2*Math.PI*sig);
            yt = getSum(variances.subList(s,t));
            yt = yt/sig + (t-s)*bloc;
        }
        return yt;
    }



}
