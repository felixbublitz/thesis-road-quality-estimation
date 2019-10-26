package de.felixbublitz.simra_rq.track.road;

import de.felixbublitz.simra_rq.database.Database;
import de.felixbublitz.simra_rq.simra.GPSData;
import de.felixbublitz.simra_rq.simra.SimraData;
import de.felixbublitz.simra_rq.track.RoadPath;

import java.util.ArrayList;

public class Road {
    private int id;
    private String name;
    private String district;
    private RoadGeometry geometry;

    public Road(int id, String name, String district, int length, RoadGeometry genometry) {
        this.id = id;
        this.name = name;
        this.district = district;
        this.geometry = genometry;
    }
    public Road(int id, String name, String district, int length, ArrayList<RoadPath> roadPaths) {
        this.id = id;
        this.name = name;
        this.district = district;
        this.geometry = new RoadGeometry(this, roadPaths);
    }
    public Road(Database db, String name, String district){
        this.name = name;
        this.district = district;
        this.geometry = new RoadGeometry(this);
        this.id = db.addRoad(this);

        System.out.println("Road " + name + " initiated: " + getLength() + " Meter");
    }

    public int getId() {
        return id;
    }
    public Integer getPosition(GPSData gps){
        return geometry.getPosition(gps);
    }
    public RoadPath getPath(Integer start, Integer end){
        return geometry.getPath(start, end);
    }
    public RoadPath getPath(SimraData sd, Integer i){
        Integer pos = geometry.getPosition(sd.getGPSData(i, true));
        if(pos == null)
            return null;
        return geometry.getPath(pos);
    }


    public int getLength(){
        return geometry.getLength();
    }
    public GPSData getGPSPoint(int position){
        return geometry.getGPSPoint(position);
    }
    public String getName(){
        return name;
    }
    public String getDistrict(){
        return district;
    }
    public RoadGeometry getRoadGeometry(){
        return geometry;
    }

}
