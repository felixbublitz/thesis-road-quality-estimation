package de.felixbublitz.simra_rq.fourier;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;

import java.util.ArrayList;

/**
 * Thread to solve dft/idft
 */

public class DFTRunnable implements Runnable {


    enum DFTType{DFT, IDFT};
    public int start;
    private int end;
    private DFTType typ;
    Object data;
    ArrayList out;

    /**
     * Returns new Runnable to produce dft from data between start and end value
     * @param data data to be transformed
     * @param start start index
     * @param end end index
     */
    public DFTRunnable(ArrayList<Double> data, int start, int end){
        this.typ = DFTType.DFT;
        this.start = start;
        this.end = end;
        this.out = new ArrayList<Double>();
        this.data = new ArrayList<>(data);

    }

    /**
     *
     * Returns new Runnable to produce idft from data between start and end value
     * @param data data to be transformed
     * @param start start index
     * @param end end index
     */
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
                dft((ArrayList)data, out, start, end);
                break;
            case IDFT:
                idft((Complex[]) data, out, start, end);
                break;
            default:
                return;
        }
    }

    /**
     * Transform part from s to t of data by idft
     * @param data data to be transformed
     * @param out transformed data
     * @param s start index
     * @param t end index
     */
    protected static void idft(Complex[] data, ArrayList<Double> out, int s, int t){
        for (int i=s; i<t;i++){
            Complex outtmp = new Complex(0);

            for(int j=0; j<data.length; j++){
                Complex exponent = new Complex(2* Math.PI*i*j/data.length).multiply(Complex.I);
                outtmp= outtmp.add(new Complex(Math.E).pow(exponent).multiply(data[j]));
            }
            Complex a4 = (outtmp.divide(data.length));
            out.add((2*i)-2*s, a4.getReal());
            out.add((2*i+1)-2*s,  a4.getReal());
        }

    }

    /**
     * Transform part from s to t of data by dft
     * @param data data to be transformed
     * @param out transformed data
     * @param s start index
     * @param t end index
     */
    protected static void dft(ArrayList<Double>  data, ArrayList<Complex> out, int s, int t){
        for (int i=s; i<t;i++){
            out.add(i-s, new Complex(0));
            for(int j=0; j<data.size(); j++){
                out.set(i-s, out.get(i-s).add(ComplexUtils.polar2Complex(data.get(j), -2*Math.PI*j*i/data.size())));
            }
        }
    }

}
