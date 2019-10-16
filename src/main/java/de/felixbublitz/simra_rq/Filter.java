package de.felixbublitz.simra_rq;

import org.apache.commons.math3.Field;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;
import org.apache.commons.math3.linear.*;
import org.apache.commons.math3.transform.TransformUtils;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import java.util.ArrayList;
import java.util.Arrays;

public class Filter {

    enum FilterType {HP, LP};


    private static ArrayList<Double> idft(Complex[] data){
        ArrayList<Double> out = new ArrayList<Double>();
        for (int i=0; i<data.length;i++){
            Complex outtmp = new Complex(0);

            for(int j=0; j<data.length; j++){
                Complex exponent = new Complex(2* Math.PI*i*j/data.length).multiply(Complex.I);
                outtmp= outtmp.add(new Complex(Math.E).pow(exponent).multiply(data[j]));
            }
            Complex a4 = (outtmp.divide(data.length));
            out.add(a4.getReal());
        }
        return out;
    }

    private static Complex[] dft(ArrayList<Double>  data){
        Complex[] out = new Complex[data.size()];
        for (int i=0; i<data.size();i++){
            out[i] = new Complex(0);
            for(int j=0; j<data.size(); j++){
                out[i]= out[i].add(ComplexUtils.polar2Complex(data.get(j), -2*Math.PI*j*i/data.size()));
            }
        }
        return out;
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
        Complex[] y = dft(data);
        FieldMatrix<Complex> dataVector = (new Array2DRowFieldMatrix<Complex>(y)).transpose();
        FieldMatrix<Complex> filter = generateFilterMatrix(data.size(), samplingRate, minFrequency, FilterType.HP);

        FieldMatrix<Complex> yFiltered = dataVector.multiply(filter);

        //debug
        //plotData(getReal(yFiltered.getRow(0)));

        return idft(yFiltered.getRow(0));
    }

    public static ArrayList<Double>  applyLowpass(ArrayList<Double> data, float samplingRate, double maxFrequency){
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");

    }


    private static FieldMatrix<Complex> generateFilterMatrix(int len, double samplingRate, double freq, FilterType type){
        double topFrequency = 1.0/(2.0*samplingRate);
        double frequenceStep = topFrequency/(len/2);
        int filterIndex = (int)(freq/frequenceStep);
        Complex[] filterVector = new Complex[len];

        if(filterIndex >=len){
            throw new java.lang.IllegalArgumentException("Frequency too high");
        }

        if (type == FilterType.HP) {
            Arrays.fill(filterVector, 0, filterIndex, new Complex(0));
            Arrays.fill(filterVector, filterIndex, len, new Complex(1));
        }

        if (type == FilterType.LP) {
            Arrays.fill(filterVector, 0, filterIndex, new Complex(1));
            Arrays.fill(filterVector, filterIndex, len, new Complex(0));
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
