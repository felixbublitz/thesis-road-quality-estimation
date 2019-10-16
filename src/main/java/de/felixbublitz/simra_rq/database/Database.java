package de.felixbublitz.simra_rq.database;
import de.felixbublitz.simra_rq.track.Road;
import java.util.ArrayList;

public class Database {

    public boolean insert(RoughnessData rd){
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }

    public boolean insert(Object data){

        ArrayList<Object> list = (ArrayList<Object>) data;

        if(list.size() == 0)
            throw new java.lang.IllegalArgumentException("no list");


        if(list.get(0).getClass() == AnomalyData.class){
            for(Object a : list){
                insert((AnomalyData) a);
            }
        }

        if(list.get(0).getClass() == RoughnessData.class){
            for(Object r : list){
                insert((RoughnessData)r);
            }
        }

        return false;
    }

    public ArrayList<RoughnessData> getRoughnessData(Road r){
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }

    public ArrayList<AnomalyData> getAnomilieData(Road r){
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }

    public Road getRoad(String name, String district){
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }

    public ArrayList<Road> getRoads(){
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }

    public boolean insert(AnomalyData ad){
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");

    }




}
