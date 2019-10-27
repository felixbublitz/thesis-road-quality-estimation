package de.felixbublitz.simra_rq.track.road;

import de.felixbublitz.simra_rq.database.Database;

import java.util.ArrayList;

/**
 *
 */
public class Road {
    private int id;
    private String name;
    private String district;
    private RoadGeometry geometry;

    /**
     * Create road by given information
     * @param id roadID
     * @param name name of road
     * @param district district of road
     * @param genometry geometry data
     */
    public Road(int id, String name, String district, RoadGeometry genometry) {
        this.id = id;
        this.name = name;
        this.district = district;
        this.geometry = genometry;
    }

    /**
     * Create road by given information
     * @param id roadID
     * @param name name of road
     * @param district district of road
     * @param length length of road
     * @param roadPaths list of RoadPaths that represent road geometry
     */
    public Road(int id, String name, String district, int length, ArrayList<RoadPath> roadPaths) {
        this.id = id;
        this.name = name;
        this.district = district;
        this.geometry = new RoadGeometry(this, roadPaths);
    }

    /**
     * Generate road and save in db
     * @param db databse
     * @param name name of road
     * @param district district of road
     */
    public Road(Database db, String name, String district){
        this.name = name;
        this.district = district;
        this.geometry = new RoadGeometry(this);
        this.id = db.insert(this);
        System.out.println("Road " + name + " initiated: " + geometry.getLength() + " Meter");
    }

    /**
     * Returns id of road
     * @return road id
     */
    public int getId() {
        return id;
    }

    /**
     * Returns name of road
     * @return road name
     */
    public String getName(){
        return name;
    }

    /**
     * Returns district of road
     * @return road district
     */
    public String getDistrict(){
        return district;
    }

    /**
     * Returns road geometry object
     * @return geometry
     */
    public RoadGeometry getRoadGeometry(){
        return geometry;
    }

}
