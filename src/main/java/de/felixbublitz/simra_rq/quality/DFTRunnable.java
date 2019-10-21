package de.felixbublitz.simra_rq.quality;

import org.apache.commons.math3.complex.Complex;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class DFTRunnable implements Runnable {


    enum DFTType{DFT, IDFT};
    public int start;
    private int end;
    private DFTType typ;
    Object data;
    ArrayList out;

    public DFTRunnable(ArrayList<Double> data, int start, int end){
        this.typ = DFTType.DFT;
        this.start = start;
        this.end = end;
        this.out = new ArrayList<Double>();
        this.data = new ArrayList<>(data);

    }

    public DFTRunnable(Complex[] data, int start, int end){
        this.typ = DFTType.IDFT;
        this.start = start;
        this.end = end;
        this.out = new ArrayList<Complex>();
        this.data = new Complex[data.length];
        System.arraycopy( data, 0, this.data, 0, data.length );

    }

    @Override
    public void run() {
        switch (typ){
            case DFT:
                Filter.dft((ArrayList)data, out, start, end);
                break;
            case IDFT:
                Filter.idft((Complex[]) data, out, start, end);
                break;
            default:
                return;
        }
    }
}
