package de.felixbublitz.simra_rq;
import de.felixbublitz.simra_rq.data.db.AnomolieData;
import de.felixbublitz.simra_rq.data.db.RoughnessData;
import de.felixbublitz.simra_rq.data.track.Road;
import java.util.ArrayList;

public class Database {

    public boolean insert(RoughnessData rd){
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }

    public boolean insert(Object data){

        ArrayList<Object> list = (ArrayList<Object>) data;

        if(list.size() == 0)
            throw new java.lang.IllegalArgumentException("no list");


        if(list.get(0).getClass() == AnomolieData.class){
            for(Object a : list){
                insert((AnomolieData) a);
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

    public ArrayList<AnomolieData> getAnomilieData(Road r){
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }

    public Road getRoad(String name, String district){
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }

    public ArrayList<Road> getRoads(){
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");
    }

    public boolean insert(AnomolieData ad){
        throw new java.lang.UnsupportedOperationException("Not implemented yet.");

    }




}
