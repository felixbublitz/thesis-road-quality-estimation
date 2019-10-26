package de.felixbublitz.simra_rq.quality.filter;

import de.felixbublitz.simra_rq.fourier.FourierTransform;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;
import org.apache.commons.math3.linear.*;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class to filter data
 */

public class Filter {

    enum FilterType {HIGHPASS_FILTER, LOWPASS_FILTER};

    /**
     * Apply bandpass filter on given data
     * @param data data to be filtered
     * @param samplingRate sampling rate of data
     * @param minFrequency minimal frequency
     * @param maxFrequency maximal frequency
     * @return filtered signal
     */
    public static ArrayList<Double> applyBandpass(ArrayList<Double> data, float samplingRate, double minFrequency, double maxFrequency){
        return applyLowpass(applyHighpass(data, samplingRate, minFrequency), samplingRate, maxFrequency);
    }

    /**
     * Apply lowpass filter on given data
     * @param data data to be filtered
     * @param samplingRate sampling rate of data
     * @param maxFrequency maximal frequency
     * @return filtered signal
     */
    public static ArrayList<Double>  applyLowpass(ArrayList<Double> data, float samplingRate, double maxFrequency){
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }

    /**
     * Apply highpass filter on given data
     * @param data data to be filtered
     * @param samplingRate sampling rate of data
     * @param minFrequency minimal frequency
     * @return filtered signal
     */
    public static ArrayList<Double> applyHighpass(ArrayList<Double> data, float samplingRate, double minFrequency){
        long startTime = System.currentTimeMillis();

        Complex[] y = FourierTransform.dft(data);
        FieldMatrix<Complex> dataVector = (new Array2DRowFieldMatrix<Complex>(y)).transpose();
        FieldMatrix<Complex> filter = generateFilterMatrix(data.size(), samplingRate, minFrequency, FilterType.HIGHPASS_FILTER);

        FieldMatrix<Complex> yFiltered = dataVector.multiply(filter);

        ArrayList<Double> idft = FourierTransform.idft(yFiltered.getRow(0));
        System.out.println("Filter Runtime: " + (System.currentTimeMillis() - startTime) + "ms");
        return idft;
    }

    /**
     * get real parts of complex values
     * @param array list of complex values
     * @return list of real values
     */
    private static double[] getReal(Complex[] array){
       double[] out = new double[array.length];
        for(int i=0; i<array.length; i++){
            out[i] = array[i].getReal();
        }
        return out;
    }

    /**
     * Generate filter matrix
     * @param len length of data
     * @param samplingRate sampling rate of data
     * @param freq filter frequency
     * @param type type of filter
     * @return filter matrix
     */
    private static FieldMatrix<Complex> generateFilterMatrix(int len, double samplingRate, double freq, FilterType type){
        double topFrequency = 1.0/(2.0*samplingRate);
        double frequenceStep = topFrequency/(len/2);
        int filterIndex = (int)(freq/frequenceStep);
        Complex[] filterVector = new Complex[len/2];

        if(filterIndex >=filterVector.length){
            throw new java.lang.IllegalArgumentException("Frequency too high");
        }

        if (type == FilterType.HIGHPASS_FILTER) {
            Arrays.fill(filterVector, 0, filterIndex, new Complex(0));
            Arrays.fill(filterVector, filterIndex, len/2, new Complex(1));
        }

        if (type == FilterType.LOWPASS_FILTER) {
            Arrays.fill(filterVector, 0, filterIndex, new Complex(1));
            Arrays.fill(filterVector, filterIndex, len/2, new Complex(0));
        }

        return MatrixUtils.createFieldDiagonalMatrix(filterVector);
    }


}
