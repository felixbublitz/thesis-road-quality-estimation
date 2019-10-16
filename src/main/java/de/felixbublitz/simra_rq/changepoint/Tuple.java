package de.felixbublitz.simra_rq.changepoint;

public class Tuple {
    public final int tau;
    public final double costs;
    public Tuple(int tau, double costs) {
        this.tau = tau;
        this.costs = costs;
    }
}