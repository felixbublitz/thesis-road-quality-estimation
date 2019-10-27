package de.felixbublitz.simra_rq.etc;

import de.felixbublitz.simra_rq.simra.GPSData;

/**
 * Basic operations on gps data
 */

public class GPSOperation {

    /**
     * Get Angle between gps point g1, g2 and g3
     * @param g1 gps point
     * @param g2 gps point
     * @param g3 gps point
     * @return angle in degrees
     */
    public static float getAngle(GPSData g1, GPSData g2, GPSData g3) {
        float degree = Math.abs((float) Math.toDegrees(Math.atan2(g3.getLongitude() - g2.getLongitude(), g3.getLatitude() - g2.getLatitude()) - Math.atan2(g1.getLongitude() - g2.getLongitude(), g1.getLatitude() - g2.getLatitude())));
        return Math.abs(degree - 180);
    }

}
