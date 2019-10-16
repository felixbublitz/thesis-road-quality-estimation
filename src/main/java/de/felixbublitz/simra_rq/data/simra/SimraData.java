package de.felixbublitz.simra_rq.data.simra;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SimraData {
    private ArrayList<Long> timeData;
    private ArrayList<GPSData> gpsData;
    private ArrayList<GyroData> gyroData;
    private ArrayList<AccelerometerData> accelerometerData;
    private int len = 0;

    private enum LineType {UNKNOW, STRUCTURE, DATA};
    public enum Axis{X, Y, Z};


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
                    String[] elements = line.split(",");
                    int len = elements.length;
                    elements = Arrays.copyOf(elements, dataPosition.size());
                    Arrays.fill(elements, len, dataPosition.size(), "");



                    GPSData gps = elements[dataPosition.get("lat")].equals("") ? null : new GPSData(Double.parseDouble(elements[dataPosition.get("lat")]), Double.valueOf(elements[dataPosition.get("lon")]));
                    AccelerometerData acc = new AccelerometerData(Double.parseDouble(elements[dataPosition.get("X")]),
                            Double.valueOf(elements[dataPosition.get("Y")]),
                            Double.valueOf(elements[dataPosition.get("Z")]));
                    gpsData.add(gps);
                    gyroData.add(elements[dataPosition.get("a")].equals("") ? null : new GyroData(Double.parseDouble(elements[dataPosition.get("a")]), Double.parseDouble(elements[dataPosition.get("b")]), Double.parseDouble(elements[dataPosition.get("c")])));
                    accelerometerData.add(acc);
                    timeData.add(Long.parseLong(elements[dataPosition.get("timeStamp")]));
                }
                if(lineType == LineType.STRUCTURE){
                    String[] elements = line.split(",");
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

    public SimraData(ArrayList<Long> timeData, ArrayList<AccelerometerData> accelerometerData, ArrayList<GPSData> gpsData){
        this.timeData = timeData;
        this.accelerometerData = accelerometerData;
        this.gpsData = gpsData;
        this.len = accelerometerData.size();
    }

    public float getSamplingRate(){
        int duration = (int)(timeData.get(len-1) - timeData.get(0));
        return (float)((duration/len)*0.001);
    }

    public ArrayList<RotationData> getRotations(){
        ArrayList<RotationData> rotationData = new ArrayList<RotationData>();
        for(int i=0; i<len;i++) {
            rotationData.set(i, new RotationData(accelerometerData.get(i), gyroData.get(i)));
        }
        return rotationData;
    }

    public ArrayList<Double> getMagnitudes(){
        ArrayList<Double> magnitudes = new ArrayList<Double>();
        for(int i=0; i<len; i++){
            magnitudes.add(accelerometerData.get(i).getMagnitude());
        }
        return magnitudes;
    }

    public ArrayList<GPSData> getGPSData(){
        return gpsData;
    }


    public ArrayList<Double> getDirectedAccelerometerData(Axis a){
        ArrayList<Double> directedAcceleromerData = new ArrayList<Double>();

        for(int i=0; i<len; i++){
            directedAcceleromerData.set(i, getDirectedAccelerometerData(i).getAxis(a));
        }
        return directedAcceleromerData;
    }

    public ArrayList<AccelerometerData> getDirectedAccelerometerData(){
        ArrayList<AccelerometerData> directedAcceleromerData = new ArrayList<AccelerometerData>();

        for(int i=0; i<len; i++){
            directedAcceleromerData.set(i, getDirectedAccelerometerData(i));
        }
        return directedAcceleromerData;
    }

    private AccelerometerData getDirectedAccelerometerData(int index){
        RotationData r = new RotationData(accelerometerData.get(index), gyroData.get(index));
        AccelerometerData acc =  accelerometerData.get(index);

        throw new java.lang.UnsupportedOperationException("Not implemented yet.");

        //return acc;
    }


}
