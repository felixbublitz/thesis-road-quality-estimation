package de.felixbublitz.simra_rq.simra;

/**
 * Representation of Accelerometer Data
 */

public class AccelerometerData {
    private double accX;
    private double accY;
    private double accZ;

    /**
     * Create new AccelerometerData object
     * @param accX
     * @param accY
     * @param accZ
     */
    public AccelerometerData(double accX, double accY, double accZ){
        this.accX = accX;
        this.accY = accY;
        this.accZ = accZ;
    }

    /**
     * Get magnitude of acceleration
     * @return
     */
    public double getMagnitude(){
        return Math.sqrt(Math.pow(accX,2) + Math.pow(accY,2) + Math.pow(accZ,2));
    }

    /**
     * Get acceleration of given axis
     * @param a
     * @return
     */
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
