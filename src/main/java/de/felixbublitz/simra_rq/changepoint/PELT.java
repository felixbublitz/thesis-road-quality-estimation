package de.felixbublitz.simra_rq.changepoint;

import java.util.ArrayList;


public class PELT extends ChangepointAlgorithm {

    ArrayList<CPCandidate> changePointCandidates;
    double k;

    public PELT(double penalty) {
        super(penalty);
    }

    @Override
    protected ArrayList<Integer> perform() {

        ArrayList<Integer> changePoints = new ArrayList<Integer>();

        k = getPenalty();

        changePointCandidates = new ArrayList<CPCandidate>();
        ArrayList<Integer> lastChange = new ArrayList<Integer>();
        lastChange.add(0);
        changePointCandidates.add(new CPCandidate(0, -getPenalty()));

        for(int i=1; i<data.size(); i++){

            CPCandidate bestCP = getOptimal(i);

            lastChange.add(bestCP.getPosition());
            cleanChangepointCandidates(bestCP, i);
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


    private void cleanChangepointCandidates(CPCandidate bestCP, int i){
        ArrayList<CPCandidate> cleanedCPs = new ArrayList<CPCandidate>();

        changePointCandidates.add(new CPCandidate(i, 0));

        for(CPCandidate cp : changePointCandidates){
            if(cp.getCost() + getCosts(cp.getPosition()+1,i) + k <= bestCP.getCost()){
                cleanedCPs.add(cp);
            }
        }

        changePointCandidates = cleanedCPs;
    }

    private CPCandidate getOptimal(int i){
        double minCosts = Integer.MAX_VALUE;
        CPCandidate out = null;
        ArrayList<CPCandidate> newChangePointCandidates = new ArrayList<CPCandidate>();

        for(CPCandidate cp : changePointCandidates){
            double currCosts = cp.getCost() + getCosts(cp.getPosition()+1,i) + getPenalty();

            //if(currCosts  <= minCosts){
                //newChangePointCandidates.add(new CPCandidate(cp.getPosition(), cp.getCost(), currCosts - getPenalty()));
            //}
            if(currCosts < minCosts){
                minCosts = currCosts;
                out = new CPCandidate(cp.getPosition(), currCosts);
            }

        }
        //changePointCandidates = newChangePointCandidates;
        return out;
    }



}
