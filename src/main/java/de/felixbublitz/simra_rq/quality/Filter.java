package de.felixbublitz.simra_rq.quality;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;
import org.apache.commons.math3.linear.*;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import java.util.ArrayList;
import java.util.Arrays;

public class Filter {


    enum FilterType {HP, LP};
    private static final int THREAD_COUNT = 4;


    private static ArrayList<Double> idft(Complex[] data){
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

    protected static void dft(ArrayList<Double>  data, ArrayList<Complex> out, int s, int t){
        for (int i=s; i<t;i++){
            out.add(i-s, new Complex(0));
            for(int j=0; j<data.size(); j++){
                out.set(i-s, out.get(i-s).add(ComplexUtils.polar2Complex(data.get(j), -2*Math.PI*j*i/data.size())));
            }
        }
    }

    private static Complex[] dft(ArrayList<Double>  data){
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

    public static ArrayList<Double> applyBandpass(ArrayList<Double> data, float samplingRate, double minFrequency, double maxFrequency){
        return applyLowpass(applyHighpass(data, samplingRate, minFrequency), samplingRate, maxFrequency);
    }

    private static double[] getReal(Complex[] array){
       double[] out = new double[array.length];
        for(int i=0; i<array.length; i++){
            out[i] = array[i].getReal();
        }
        return out;
    }

    public static ArrayList<Double> applyHighpass(ArrayList<Double> data, float samplingRate, double minFrequency){
        long startTime = System.currentTimeMillis();

        Complex[] y = dft(data);
        FieldMatrix<Complex> dataVector = (new Array2DRowFieldMatrix<Complex>(y)).transpose();
        FieldMatrix<Complex> filter = generateFilterMatrix(data.size(), samplingRate, minFrequency, FilterType.HP);

        FieldMatrix<Complex> yFiltered = dataVector.multiply(filter);

        //debug
        //plotData(getReal(yFiltered.getRow(0)));
        ArrayList<Double> idft = idft(yFiltered.getRow(0));
        System.out.println("Filter Runtime: " + (System.currentTimeMillis() - startTime) + "ms");
        return idft;
    }

    public static ArrayList<Double>  applyLowpass(ArrayList<Double> data, float samplingRate, double maxFrequency){
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");

    }


    private static FieldMatrix<Complex> generateFilterMatrix(int len, double samplingRate, double freq, FilterType type){
        double topFrequency = 1.0/(2.0*samplingRate);
        double frequenceStep = topFrequency/(len/2);
        int filterIndex = (int)(freq/frequenceStep);
        Complex[] filterVector = new Complex[len/2];

        if(filterIndex >=filterVector.length){
            throw new java.lang.IllegalArgumentException("Frequency too high");
        }

        if (type == FilterType.HP) {
            Arrays.fill(filterVector, 0, filterIndex, new Complex(0));
            Arrays.fill(filterVector, filterIndex, len/2, new Complex(1));
        }

        if (type == FilterType.LP) {
            Arrays.fill(filterVector, 0, filterIndex, new Complex(1));
            Arrays.fill(filterVector, filterIndex, len/2, new Complex(0));
        }

        return MatrixUtils.createFieldDiagonalMatrix(filterVector);
    }


    private static void plotData(double[] y){
         /* DEBUG */
        double[] xData = new double[y.length];
        for( int i = 0; i < y.length; i++ )
            xData[i] = i+1;

        XYChart chart = QuickChart.getChart("Sample Chart", "X", "Y", "y(x)", xData, y);
        new SwingWrapper(chart).displayChart();

    }

}
