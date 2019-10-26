package de.felixbublitz.simra_rq;

import de.felixbublitz.simra_rq.database.data.RoughnessData;
import de.felixbublitz.simra_rq.database.Database;
import de.felixbublitz.simra_rq.map.*;
import de.felixbublitz.simra_rq.quality_index.DynamicQualityIndex;
import de.felixbublitz.simra_rq.quality_index.QualityIndex;
import de.felixbublitz.simra_rq.simra.GPSData;
import de.felixbublitz.simra_rq.track.road.Road;
import de.felixbublitz.simra_rq.track.RoadPath;

import java.awt.*;
import java.util.ArrayList;

/**
 * Basic Map View of RoughnessData
 */

public class Display {

    private static Database db;
    private static Map map;
    private final static int WIDTH = 1024;
    private final static int HEIGHT = 768;
    private final static int ZOOM = 7;
    private final static GPSData FOCUS = new GPSData(52.5067614,13.284651);
    private final static Color OVERLAY_COLOR =  new Color(150,150,150,160);


    public static void main(String[] args)
    {
        map = new Map(WIDTH, HEIGHT);
        map.setZoom(ZOOM);
        map.setLocation(FOCUS);
        map.addRectangle(OVERLAY_COLOR);
        addOverlayData();
        map.show();
    }

    /**
     * Draw RoughnessData on Map
     */
    public static void addOverlayData(){
        db = new Database(Options.DATABASE_FILE);
        QualityIndex dQI = new DynamicQualityIndex(db);

        ArrayList<Road> roads = db.getRoads();
        for(Road road: roads){
            Color indicator = getColor(dQI.getQuality(getVariance(road)));
            for(RoadPath path : road.getRoadGeometry().getPaths()){
                map.addPath(path, indicator);
            }
        }
    }

    /**
     * Get a single variance value for a whole road by averaging
     * @param road Road to get variance of
     * @return variance of given road
     */
    private static Double getVariance(Road road){
        ArrayList<RoughnessData> roughnessData = db.getRoughnessData(road);
        if(roughnessData.size() == 0)
            return null;

        double varianceSum = 0;
        for(RoughnessData rd : roughnessData){
            varianceSum += rd.getVariance();
        }
        return varianceSum/roughnessData.size();
    }

    /**
     * Convert Score to color between red, yellow and green
     * @param score score between 0 (worst) and 1 (best)
     * @return color between red (worst) and green (best)
     */
    private static Color getColor(Double score){
        if(score == null)
            return Color.GRAY;
        int r = (int)(Math.max(0,Math.min(1, 2*(1-score))*255));
        int g = (int)(Math.max(0,Math.min(1, 2*score)*255));
        return new Color(r,g,0);
    }

}
