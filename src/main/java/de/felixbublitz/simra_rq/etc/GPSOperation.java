package de.felixbublitz.simra_rq.etc;

import de.felixbublitz.simra_rq.simra.GPSData;

public class GPSOperation {

    public static float getAngle(GPSData g1, GPSData g2, GPSData g3) {
        float degree = Math.abs((float) Math.toDegrees(Math.atan2(g3.getLongitude() - g2.getLongitude(), g3.getLatitude() - g2.getLatitude()) - Math.atan2(g1.getLongitude() - g2.getLongitude(), g1.getLatitude() - g2.getLatitude())));
        return Math.abs(degree - 180);
    }

}
