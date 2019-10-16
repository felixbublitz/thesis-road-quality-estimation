package de.felixbublitz.simra_rq.changepoint;

import java.util.ArrayList;

public class BinarySegmentation extends ChangepointAlgorithm {
    public BinarySegmentation(int penalty) {
        super(penalty);
    }

    @Override
    protected ArrayList<Integer> perform() {
        return perform(0, data.size()-1);
    }


    private ArrayList<Integer> perform(int s, int t){
        double min = Double.MAX_VALUE;
        int index = -1;
        ArrayList<Integer> changePoints = new ArrayList<Integer>();

        for(int i=s; i<t; i++){
            double cmin = getCosts(s,i) + getCosts(i+1, t) + penalty;
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
