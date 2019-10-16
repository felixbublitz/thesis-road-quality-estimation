package de.felixbublitz.simra_rq.data.simra;

public class AccelerometerData {
    private double accX;
    private double accY;
    private double accZ;

    public AccelerometerData(double accX, double accY, double accZ){
        this.accX = accX;
        this.accY = accY;
        this.accZ = accZ;
    }

    public double getMagnitude(){
        return Math.sqrt(Math.pow(accX,2) + Math.pow(accY,2) + Math.pow(accZ,2));
    }

    public double getAxis(SimraData.Axis a){
        switch (a){
            case X:
                return accX;
            case Y:
                return accY;
            case Z:
                return accZ;
            default:
                return -1;
        }
    }

}
