package de.felixbublitz.simra_rq.simra;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Representation of data recorded by Simra App
 */

public class SimraData {
    private ArrayList<Long> timeData;
    private ArrayList<GPSData> gpsData;
    private ArrayList<GyroData> gyroData;
    private ArrayList<AccelerometerData> accelerometerData;
    private int len = 0;
    private enum LineType {UNKNOW, STRUCTURE, DATA};
    public enum Axis{X, Y, Z};

    private final static String IDENTIFIER_LATITUDE = "lat";
    private final static String IDENTIFIER_LONGITUDE = "lat";
    private final static String IDENTIFIER_ACCELEROMETER_X = "X";
    private final static String IDENTIFIER_ACCELEROMETER_Y = "Y";
    private final static String IDENTIFIER_ACCELEROMETER_Z = "Z";
    private final static String IDENTIFIER_GYROSCOPE_X = "a";
    private final static String IDENTIFIER_GYROSCOPE_Y = "b";
    private final static String IDENTIFIER_GYROSCOPE_Z = "c";
    private final static String IDENTIFIER_TIME = "timeStamp";
    private final static String DELIMITER = ",";
    private final static double MS_TO_SEC = 0.001;

    /**
     * Returns a new SimraData object by giving data as argument
     * @param timeData
     * @param accelerometerData
     * @param gpsData
     * @param gyroscopeData
     */
    public SimraData(ArrayList<Long> timeData, ArrayList<AccelerometerData> accelerometerData, ArrayList<GPSData> gpsData, ArrayList<GyroData> gyroscopeData){
        this.timeData = timeData;
        this.accelerometerData = accelerometerData;
        this.gyroData = gyroscopeData;
        this.gpsData = gpsData;
        this.len = accelerometerData.size();
    }

    /**
     * Returns a new SimraData object from a local csv file
     *
     * @param path the location of the csv file to load
     */
    public SimraData(String path){
        BufferedReader br;
        String line;
        String versionKey;
        LineType lineType = LineType.UNKNOW;
        Map<String, Integer> dataPosition = new HashMap<String, Integer>();
        accelerometerData = new ArrayList<AccelerometerData>();
        gpsData = new ArrayList<GPSData>();
        gyroData = new ArrayList<GyroData>();
        timeData = new ArrayList<Long>();

        try {
            br = new BufferedReader(new FileReader(path));
            versionKey = br.readLine();

            while ((line = br.readLine()) != null) {
                if(lineType == LineType.DATA){
                    String[] elements = line.split(DELIMITER);
                    int len = elements.length;
                    elements = Arrays.copyOf(elements, dataPosition.size());
                    Arrays.fill(elements, len, dataPosition.size(), "");

                    GPSData gps = elements[dataPosition.get(IDENTIFIER_LATITUDE)].equals("") ? null : new GPSData(Double.parseDouble(elements[dataPosition.get(IDENTIFIER_LATITUDE)]), Double.valueOf(elements[dataPosition.get(IDENTIFIER_LONGITUDE)]));
                    AccelerometerData acc = new AccelerometerData(Double.parseDouble(elements[dataPosition.get(IDENTIFIER_ACCELEROMETER_X)]),
                            Double.valueOf(elements[dataPosition.get(IDENTIFIER_ACCELEROMETER_Y)]),
                            Double.valueOf(elements[dataPosition.get(IDENTIFIER_ACCELEROMETER_Z)]));
                    gpsData.add(gps);
                    gyroData.add( !dataPosition.containsKey(IDENTIFIER_GYROSCOPE_X) || elements[dataPosition.get(IDENTIFIER_GYROSCOPE_X)].equals("") ? null : new GyroData(Double.parseDouble(elements[dataPosition.get(IDENTIFIER_GYROSCOPE_X)]), Double.parseDouble(elements[dataPosition.get(IDENTIFIER_GYROSCOPE_Y)]), Double.parseDouble(elements[dataPosition.get(IDENTIFIER_GYROSCOPE_Z)])));
                    accelerometerData.add(acc);
                    timeData.add(Long.parseLong(elements[dataPosition.get(IDENTIFIER_TIME)]));
                }
                if(lineType == LineType.STRUCTURE){
                    String[] elements = line.split(DELIMITER);
                    for(int i=0; i<elements.length;i++){
                        dataPosition.put(elements[i],i);
                    }
                    lineType = LineType.DATA;
                }
                if(line.equals(versionKey)){
                    lineType = LineType.STRUCTURE;
                }
            }
        }catch (IOException e){
            return;
        }
        len = accelerometerData.size();
    }

    /**
     * Get length of SimraData
     * @return length of SimraData
     */
    public int getLength(){
        return len;
    }

    /**
     * Get time when recorded
     * @return Date of record
     */
    public Date getRecordingDate(){
        return new Date((long)timeData.get(0));
    }

    /**
     * Get formated string of recording time
     * @param format format of output string
     * @return formated recording time string
     */
    public String getRecordingDate(String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(new Date((long)timeData.get(0)));
    }

    /**
     * Get sampling rate
     * @return sampling rate in ms
     */
    public float getSamplingRate(){
        int duration = (int)(timeData.get(len-1) - timeData.get(0));
        return (float)((duration/len)*MS_TO_SEC);
    }

    /**
     * Get rotation data
     * @return rotation data
     */
    public ArrayList<RotationData> getRotations(){
        ArrayList<RotationData> rotationData = new ArrayList<RotationData>();
        for(int i=0; i<len;i++) {
            rotationData.set(i, new RotationData(accelerometerData.get(i), gyroData.get(i)));
        }
        return rotationData;
    }

    /**
     * Get magnitudes of accelerometer data
     * @return list of magnitudes of accelerometer data
     */
    public ArrayList<Double> getMagnitudes(){
        ArrayList<Double> magnitudes = new ArrayList<Double>();
        for(int i=0; i<len; i++){
            magnitudes.add(accelerometerData.get(i).getMagnitude());
        }
        return magnitudes;
    }

    /**
     * Get list of all gps data
     * @return gps data list
     */
    public ArrayList<GPSData> getGPSData(){
        return gpsData;
    }

    /**
     * Get list of all gps data
     * @param interpolate interpolate missing values
     * @return gps data list
     */
    public ArrayList<GPSData> getGPSData(boolean interpolate){
        if(!interpolate)
            return getGPSData();

        ArrayList out = new ArrayList();
        for(int i=0; i<len;i++){
            out.add(getGPSData(i, true));
        }
        return out;
    }

    /** Get gps data at given point
     * @param index index of gps data
     * @param interpolate interpolate if missing
     * @return gps value at given point
     */
    public GPSData getGPSData(int index, boolean interpolate){
        GPSData curr = gpsData.get(index);
        if(curr == null){
            GPSData last = null;
            GPSData next = null;
            int lastI = 0;
            int nextI = 0;
            int i = index;
            while(last == null){
                last = gpsData.get(i--);
                lastI = i;
            }
            i=index;
            while(next == null){
                next = gpsData.get(i++);
                nextI = i;
            }
            double latDist =  next.getLatitude() - last.getLatitude();
            double lonDist = next.getLongitude() - last.getLongitude();
            double progress = (index-lastI)*1.0/ (nextI-lastI)*1.0;

            return new GPSData(last.getLatitude() + progress*latDist, last.getLongitude() + progress * lonDist);
        }else{
            return curr;
        }
    }

    /** Get gps data at given point
     * @param index index of gps data
     * @return gps value at given point
     */
    public GPSData getGPSData(int index){
        return gpsData.get(index);
    }

    /**
     * Get list of specific axis of directed accelerometer data
     * @param a Axis to get
     * @return list of accelerometer data of given axis
     */
    public ArrayList<Double> getDirectedAccelerometerData(Axis a){
        ArrayList<Double> directedAcceleromerData = new ArrayList<Double>();

        for(int i=0; i<len; i++){
            directedAcceleromerData.add(getDirectedAccelerometerData(i).getAxis(a));
        }
        return directedAcceleromerData;
    }

    /**
     * Get list of directed accelerometer data
     * @return get list of directed accelerometer data
     */
    public ArrayList<AccelerometerData> getDirectedAccelerometerData(){
        ArrayList<AccelerometerData> directedAcceleromerData = new ArrayList<AccelerometerData>();

        for(int i=0; i<len; i++){
            directedAcceleromerData.add(getDirectedAccelerometerData(i));
        }
        return directedAcceleromerData;
    }

    /**
     * Get directed accelerometer data at given point
     * @param index index of accelerometer data
     * @return directed accelerometer data at given index
     */
    private AccelerometerData getDirectedAccelerometerData(int index){
        RotationData r = new RotationData(accelerometerData.get(index), gyroData.get(index));
        AccelerometerData acc =  accelerometerData.get(index);

        throw new java.lang.UnsupportedOperationException("Not implemented yet.");

    }


}
