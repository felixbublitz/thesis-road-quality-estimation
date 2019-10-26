package de.felixbublitz.simra_rq;

import de.felixbublitz.simra_rq.database.RoughnessData;
import de.felixbublitz.simra_rq.database.Database;
import de.felixbublitz.simra_rq.mapview.*;
import de.felixbublitz.simra_rq.quality_index.DynamicQualityIndex;
import de.felixbublitz.simra_rq.quality_index.QualityIndex;
import de.felixbublitz.simra_rq.simra.GPSData;
import de.felixbublitz.simra_rq.track.Road;
import de.felixbublitz.simra_rq.track.RoadPath;

import java.awt.*;
import java.util.ArrayList;

public class Display {

    private static Database db;
    private static Map map;

    public static void main(String[] args)
    {
        map = new Map(1024, 768);
        map.setZoom(6);
        map.setLocation(new GPSData(52.5067614,13.284651));
        map.addRectangle(new Color(150,150,150,160));
        addOverlayData();
        map.show();
    }

    public static void addOverlayData(){
        db = new Database("database.db");
        QualityIndex dQI = new DynamicQualityIndex(db);

        ArrayList<Road> roads = db.getRoads();
        for(Road road: roads){
            Color indicator = getColor(dQI.getQuality(getVariance(road)));
            for(RoadPath path : road.getRoadGeometry().getPaths()){
                map.addTrack(path, indicator);
            }
        }
    }

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

    private static Color getColor(Double score){
        if(score == null)
            return Color.GRAY;
        int r = (int)(Math.max(0,Math.min(1, 2*(1-score))*255));
        int g = (int)(Math.max(0,Math.min(1, 2*score)*255));
        return new Color(r,g,0);
    }

}
