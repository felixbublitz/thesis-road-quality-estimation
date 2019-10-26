package de.felixbublitz.simra_rq.changepoint.implementation;

import de.felixbublitz.simra_rq.changepoint.ChangepointAlgorithm;

import java.util.ArrayList;

/**
 * Binary Segmentation changepoint algorithm
 */

public class BinarySegmentation extends ChangepointAlgorithm {
    public BinarySegmentation(double penalty, double samplingRate) {
        super(penalty, samplingRate);
    }


    /**
     * find changepoints
     * @return found changepoints
     */
    @Override
    protected ArrayList<Integer> perform() {
        return perform(0, data.size()-1);
    }


    /**
     * find changepoints between s and t
     * @param s start index
     * @param t end index
     * @return found changepoints
     */
    private ArrayList<Integer> perform(int s, int t){
        double min = Double.MAX_VALUE;
        int index = -1;
        ArrayList<Integer> changePoints = new ArrayList<Integer>();

        for(int i=s; i<t; i++){

            double cmin = getCosts(s,i) + getCosts(i+1, t) + getPenalty();
            if(cmin < min && cmin < getCosts(s,t)){
                min = cmin;
                index = i;
            }
        }

        if(index != -1){
            changePoints.addAll(perform(s, index));
            changePoints.addAll(perform(index+1, t));
        }else{
            ArrayList<Integer> n = new ArrayList<Integer>();
            n.add(t);
            return n;
        }

        return changePoints;
    }
}
