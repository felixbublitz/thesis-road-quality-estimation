package de.felixbublitz.simra_rq.changepoint;

public class CPCandidate {
    private int position;
    private double cost;

    public int getPosition() {
        return position;
    }

    public double getCost() {
        return cost;
    }


    public CPCandidate(int position, double cost) {
        this.position = position;
        this.cost = cost;
    }
}
