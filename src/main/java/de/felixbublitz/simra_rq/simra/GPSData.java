package de.felixbublitz.simra_rq.simra;

/**
 * Representation of GPS Data
 */

public class GPSData {

    private double lat;
    private double lon;

    /**
     * Creates new gps data object
     * @param lat
     * @param lon
     */
    public GPSData(double lat, double lon){
        this.lat = lat;
        this.lon = lon;
    }

    /**
     * Get latitude of gps
     * @return latitude
     */
    public double getLatitude(){
        return lat;
    }

    /**
     * Get longitude of gps
     * @return longitude
     */
    public double getLongitude(){
        return lon;
    }

    /**
     * Get distance from gps point to another gps point
     * @param other other gps point
     * @return distance in meter
     */
    public double getDistanceTo(GPSData other){
        double earthRadius = 6371000;
        double dLat = Math.toRadians(other.lat-this.lat);
        double dLng = Math.toRadians(other.lon-this.lon);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(this.lat)) * Math.cos(Math.toRadians(other.lat)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = (float) (earthRadius * c);

        return dist;
    }

}
