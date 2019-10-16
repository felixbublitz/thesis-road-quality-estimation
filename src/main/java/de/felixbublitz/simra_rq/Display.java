package de.felixbublitz.simra_rq;

import de.felixbublitz.simra_rq.database.AnomalyData;
import de.felixbublitz.simra_rq.database.RoughnessData;
import de.felixbublitz.simra_rq.database.Database;
import de.felixbublitz.simra_rq.track.Road;

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
