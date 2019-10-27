package de.felixbublitz.simra_rq.etc;

/**
 * A basic tuple data structure
 */

public class Pair {
    private Object data1;
    private Object data2;

    public Pair(Object data1, Object data2){
        this.data1 = data1;
        this.data2 = data2;
    }

    public Object getData1(){
        return data1;
    }

    public Object getData2(){
        return data2;
    }
}