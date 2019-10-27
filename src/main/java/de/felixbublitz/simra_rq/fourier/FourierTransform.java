package de.felixbublitz.simra_rq.fourier;

import org.apache.commons.math3.complex.Complex;

import java.util.ArrayList;

/**
 * Methods to solve DFT and IDFT
 */

public class FourierTransform {
    private static final int THREAD_COUNT = 4;

    /**
     * Apply inverse fourier transform on data
     * @param data data to transform by idft
     * @return transformed data
     */
    public static ArrayList<Double> idft(Complex[] data){
        ArrayList<Double> out= new ArrayList<Double>();
        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<DFTRunnable> runnables = new ArrayList<DFTRunnable>();

        int partLength = data.length/THREAD_COUNT;
        for(int i =0; i<THREAD_COUNT; i++){
            int s = i * partLength;
            int t = Math.min((i+1)*partLength, data.length);
            if(i==THREAD_COUNT-1){
                t=data.length;
            }

            DFTRunnable dftRunnable = new DFTRunnable(data, s, t);
            Thread thread = new Thread(dftRunnable);
            threads.add(thread);
            runnables.add(dftRunnable);
            thread.start();
        }

        for(Thread t : threads){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for(DFTRunnable r : runnables){
            out.addAll( r.out);
        }

        return out;
    }

    /**
     * Apply fourier transform on data
     * @param data data to transform by dft
     * @return transformed data
     */
    public static Complex[] dft(ArrayList<Double>  data){
        int len = data.size()/2;
        ArrayList<Complex> out = new  ArrayList<Complex>();
        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<DFTRunnable> runnables = new ArrayList<DFTRunnable>();


        int partLength = len/THREAD_COUNT;
        for(int i =0; i<THREAD_COUNT; i++){

            int s = i * partLength;
            int t = Math.min((i+1)*partLength, len);
            if(i==THREAD_COUNT-1){
                t=len;
            }
            DFTRunnable dftRunnable = new DFTRunnable(data, s, t);
            Thread thread = new Thread(dftRunnable);
            threads.add(thread);
            runnables.add(dftRunnable);

            thread.start();
        }

        for(Thread t : threads){
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for(DFTRunnable r : runnables){
            out.addAll( r.out);
        }

        return (Complex[])( out.toArray(new Complex[out.size()]));
    }

}
