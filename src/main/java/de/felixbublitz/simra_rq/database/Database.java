package de.felixbublitz.simra_rq.database;
import de.felixbublitz.simra_rq.simra.GPSData;
import de.felixbublitz.simra_rq.track.Road;
import javafx.util.Pair;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Database {
    private Connection connection;


    public Database(String file){
        connect(file);
        initDatabase();
    }

    private void initDatabase(){
        query("CREATE TABLE IF NOT EXISTS Road_Nodes(id int AUTO_INCREMENT, road_id int, latitude float, longitude float, PRIMARY KEY(id), FOREIGN KEY(road_id) REFERENCES Roads(id));");
        query("CREATE TABLE IF NOT EXISTS Roads(id INTEGER PRIMARY KEY AUTOINCREMENT, name varchar(50), district varchar(50), length int);");
        query("CREATE TABLE IF NOT EXISTS Roughness_Data(id INTEGER PRIMARY KEY AUTOINCREMENT, road_id INTEGER, start INTEGER, end INTEGER, recorded text, variance float, FOREIGN KEY(road_id) REFERENCES Roads(id));");
        query("CREATE TABLE IF NOT EXISTS Anomaly_Data (id INTEGER PRIMARY KEY AUTOINCREMENT, road_id INTEGER, position INTEGER, recorded text, FOREIGN KEY(road_id) REFERENCES Roads(id));");

    };

    private void query(String sql){


        try {
            Statement s = connection.createStatement();
            s.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }



    private void connect(String file) {
        try {
            String url = "jdbc:sqlite:"+file;
            connection = DriverManager.getConnection(url);
            System.out.println("Connection to SQLite has been established.");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    private void close(){
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void insert(RoughnessData rd){
        DBQuery d = new DBQuery(connection).insert("Roughness_Data");
        d.cell("road_id",rd.getRoad().getId());
        d.cell("start", rd.getStart());
        d.cell("end", rd.getEnd());
        d.cell("recorded", rd.getRecorded());
        d.cell("variance", rd.getVariance());
        d.query();
    }

    public void insert(AnomalyData ad){
        DBQuery d = new DBQuery(connection).insert("Roughness_Data");
        d.cell("road_id",ad.getRoad().getId());
        d.cell("position", ad.getPosition());
        d.cell("recorded", ad.getRecorded());
        d.query();
    }

    public ArrayList<RoughnessData> getRoughnessData(Road road){
        ArrayList<RoughnessData> out = new ArrayList<>();
        DBQuery d = new DBQuery(connection).select("Roughness_Data");
        d.cell("road_id").cell("start").cell("end").cell("recorded").cell("variance");
        d.where("road_id", road.getId());
        ArrayList<HashMap<String, String>> result = d.getResult();

        for(HashMap<String,String> r : result){
            try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            out.add(new RoughnessData(road, format.parse(r.get("recorded")), Integer.parseInt(r.get("start")), Integer.parseInt(r.get("end")), Double.parseDouble(r.get("variance"))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return out;

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

    private ArrayList<GPSData> getRodeNodes(int roadID){
        DBQuery d = new DBQuery(connection).select("Road_Nodes").cells("id", "latitude", "longitude").where("road_id", String.valueOf(roadID));
        ArrayList<HashMap<String, String>> results = d.getResult();
        ArrayList<GPSData> out = new ArrayList<>();
        for(HashMap<String, String> r : results){
            out.add(new GPSData(Double.parseDouble(r.get("latitude")), Double.parseDouble(r.get("longitude"))));
        }

        return out;
    }


    public ArrayList<AnomalyData> getAnomalyData(Road road){
        ArrayList<AnomalyData> out = new ArrayList<>();
        DBQuery d = new DBQuery(connection).select("Anomaly_Data");
        d.cell("road_id").cell("position").cell("recorded");
        d.where("road_id", road.getId());
        ArrayList<HashMap<String, String>> result = d.getResult();

        for(HashMap<String,String> r : result){
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                out.add(new AnomalyData(road, format.parse(r.get("recorded")), Integer.parseInt(r.get("position"))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return out;
    }

    public Road getRoad(String name, String district){
        DBQuery d =  new DBQuery(connection).select("roads").cells("id", "name", "district", "length").where("name", name).where("district", district);
        ArrayList<HashMap<String, String>> result = d.getResult();
        if(result.size() == 0)
            return null;
        HashMap<String, String>  r =result.get(0);

        Road road = new Road(Integer.parseInt(r.get("id")), r.get("name"),  r.get("district"),  Integer.parseInt(r.get("length")), getRodeNodes(Integer.parseInt(r.get("id"))));
        return road;
    }

    public void addRoad(Road r){
        DBQuery d = new DBQuery(connection).insert("Roads").cell("name", r.getName()).cell("district", r.getDistrict()).cell("length", String.valueOf(r.getLength()));
        int id = d.query();
    }

    public ArrayList<Road> getRoads(){
        DBQuery d =  new DBQuery(connection).select("roads").cells("id", "name", "district", "length");
        ArrayList<HashMap<String, String>> results = d.getResult();
        ArrayList<Road> out = new ArrayList<>();

        for(HashMap<String, String> r : results) {
            out.add(new Road(Integer.parseInt(r.get("id")), r.get("name"),  r.get("district"),  Integer.parseInt(r.get("length")), getRodeNodes(Integer.parseInt(r.get("id")))));
        }

        return out;
    }





}