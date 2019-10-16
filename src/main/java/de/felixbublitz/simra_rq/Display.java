package de.felixbublitz.simra_rq;

import de.felixbublitz.simra_rq.data.db.AnomalyData;
import de.felixbublitz.simra_rq.data.db.RoughnessData;
import de.felixbublitz.simra_rq.data.track.Road;

import java.util.ArrayList;

public class Display {

    public static void main(String[] args){
        Database db = new Database();

        ArrayList<Road> roads = db.getRoads();

        for(Road road:roads) {
            ArrayList<RoughnessData> rd = db.getRoughnessData(road);
            ArrayList<AnomalyData> ad = db.getAnomilieData(road);
        }

        //display
    }



}
