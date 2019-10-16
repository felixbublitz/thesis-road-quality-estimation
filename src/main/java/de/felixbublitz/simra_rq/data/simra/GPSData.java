package de.felixbublitz.simra_rq.data.simra;

public class GPSData {

    private double lat;
    private double lon;

    public GPSData(double lat, double lon){
        this.lat = lat;
        this.lon = lon;
    }

    public double getLatitude(){
        return lat;
    }

    public double getLongitude(){
        return lon;
    }


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
