package de.felixbublitz.simra_rq.changepoint;

import de.felixbublitz.simra_rq.etc.ListOperation;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ChangepointAlgorithm {

    protected double mean = 0;
    protected List<Double> variances;
    private double penaltyFactor;
    protected ArrayList<Double> data;

    public ChangepointAlgorithm(double penaltyFactor){
        this.penaltyFactor = penaltyFactor;
    }

    public ArrayList<Integer> getChangepoints(ArrayList<Double> data){
        this.mean = ListOperation.getMean(data);
        this.data = data;

        long startTime = System.currentTimeMillis();
        ArrayList<Integer> changePoints = perform();
        System.out.println("CP Runtime: " + (System.currentTimeMillis() - startTime) + "ms");
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
        return penaltyFactor * ListOperation.getVariance(data)*Math.log(data.size());
    }



/*
    public double getCosts(int s, int t){

        if(t < s){
            return 0;
        }

        double mean = ListOperation.getMean(data);
        double varST = ListOperation.getVariance(data.subList(s,t));


        double standarddevST = ListOperation.getStandardDeviation(data.subList(s,t));

        double sum = 0;

        for(int i=s; i<t;i++){
            sum += Math.pow(data.get(i)-mean,2);
        }
        int len = t-s;


        return -2*((-len/2) * ln(2*Math.PI*varST)-(sum/2*varST));
    }

    private double ln(double l){
        return Math.log(l)/Math.log(Math.E);
    }
*/

    protected double getCosts(int s, int t) {

        if(t<=s){
            return 0;
        }

        double sum = 0;
        double mu = ListOperation.getMean(data.subList(s, t));
        double var = ListOperation.getVariance(data.subList(s, t));

        for (int i = s; i < t; i++) {
            double p1 = Math.abs(data.get(i) - mu);
            double p2 = Math.abs(var);
            sum +=0.08*Math.abs(p1-p2);
        }

        if(var == 0){
            return 0;
        }


      //  sum = sum/var;

        return sum;
    }

/*

    protected double getCosts(int s, int t){
       if(t<=s){
           return 0;
       }

        double sum = 0;
        double mu = ListOperation.getMean(data);
        double var = ListOperation.getVariance(data.subList(s,t));


        if(var == 0)
            return 0;

        for(int i=s;i<t; i++){
            sum += (Math.pow(data.get(i)-mu,2) /var) + Math.log(2 * Math.PI * var);
        }


        return sum;
    }
*/


}
