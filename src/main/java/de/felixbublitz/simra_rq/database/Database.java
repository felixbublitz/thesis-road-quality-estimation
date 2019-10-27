package de.felixbublitz.simra_rq.database;
import de.felixbublitz.simra_rq.database.data.AnomalyData;
import de.felixbublitz.simra_rq.database.data.RoughnessData;
import de.felixbublitz.simra_rq.etc.Pair;
import de.felixbublitz.simra_rq.simra.GPSData;
import de.felixbublitz.simra_rq.track.road.Road;
import de.felixbublitz.simra_rq.track.road.RoadNode;
import de.felixbublitz.simra_rq.track.road.RoadPath;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Connector to save and load quality data from sqlite3 database file
 */

public class Database {
    private Connection connection;

    //database tables
    private final static String TABLE_ROUGHNESS_DATA = "Roughness_Data";
    private final static String TABLE_ANOMALY_DATA = "Anomaly_Data";
    private final static String TABLE_ROADS = "Roads";
    private final static String TABLE_ROAD_NODES = "ROAD_NODES";


    //database columns
    private final static String COLUMN_ID = "id";
    private final static String COLUMN_ROAD_ID = "road_id";
    private final static String COLUMN_PATH_ID = "path_id";
    private final static String COLUMN_RECORDED = "recorded";
    private final static String COLUMN_VARIANCE = "variance";
    private final static String COLUMN_LATITUDE = "latitude";
    private final static String COLUMN_LONGITUDE = "longitude";

    private final static String COLUMN_ROAD_NAME = "name";
    private final static String COLUMN_ROAD_DISTRICT = "district";
    private final static String COLUMN_ROAD_LENGTH = "length";

    private final static String COLUMN_RD_START = "start";
    private final static String COLUMN_RD_END = "end";

    private final static String COLUMN_AD_POSITION = "position";
    private final static String COLUMN_QI_MAX_VARIANCE = "maxV";
    private final static String COLUMN_QI_MIN_VARIANCE = "minV";


    /**
     * Creates new Database connection by database source
     * @param file sqlite3 database file
     */
    public Database(String file){
        connect(file);
        initDatabase();
    }


    /**
     * Insert RoughnessData object in database
     * @param rd RoughnessData object
     */
    public void insert(RoughnessData rd){
        DBQuery d = new DBQuery(connection).insert(TABLE_ROUGHNESS_DATA);
        d.cell(COLUMN_ROAD_ID,rd.getRoad().getId());
        d.cell(COLUMN_RD_START, rd.getStart());
        d.cell(COLUMN_RD_END, rd.getEnd());
        d.cell(COLUMN_RECORDED, rd.getRecordingDate("yyyy-MM-dd"));
        d.cell(COLUMN_VARIANCE, rd.getVariance());
        d.query();
    }

    /**
     * Insert AnomalyData object in database
     * @param ad AnomalyData object
     */
    public void insert(AnomalyData ad){
        DBQuery d = new DBQuery(connection).insert(TABLE_ANOMALY_DATA);
        d.cell(COLUMN_ROAD_ID,ad.getRoad().getId());
        d.cell(COLUMN_AD_POSITION, ad.getPosition());
        d.cell(COLUMN_RECORDED, ad.getRecordingDate("yyyy-MM-dd"));
        d.query();
    }

    /**
     * Insert List of AnomalyData or RougnessData in database
     * @param data list of data
     * @return true if input is valid
     */
    public boolean insert(Object data){

        ArrayList<Object> list = (ArrayList<Object>) data;

        if(list.size() == 0)
            return true;

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

    /**
     * Insert road in database and returns road id
     * @param r road to be inserted
     * @return road id
     */
    public int insert(Road r){
        DBQuery d = new DBQuery(connection).insert(TABLE_ROADS).cell(COLUMN_ROAD_NAME, r.getName()).cell(COLUMN_ROAD_DISTRICT, r.getDistrict()).cell(COLUMN_ROAD_LENGTH, String.valueOf(r.getRoadGeometry().getLength()));
        int roadID = d.query();

        ArrayList<RoadPath> paths = r.getRoadGeometry().getPaths();
        for(int i=0; i< paths.size(); i++){
            for(RoadNode node : paths.get(i).getNodes()){
                DBQuery dn = new DBQuery(connection).insert(TABLE_ROAD_NODES);
                dn.cell(COLUMN_ROAD_ID, roadID);
                dn.cell(COLUMN_PATH_ID, i);
                dn.cell(COLUMN_LATITUDE, node.getGPSData().getLatitude());
                dn.cell(COLUMN_LONGITUDE, node.getGPSData().getLongitude());
                dn.query();
            }
        }

        return roadID;
    }

    /**
     * Get list of RoughnessData for specific road
     * @param road road to get roughness data
     * @return list of all roughness data records
     */
    public ArrayList<RoughnessData> getRoughnessData(Road road){
        ArrayList<RoughnessData> out = new ArrayList<>();
        DBQuery d = new DBQuery(connection).select(TABLE_ROUGHNESS_DATA);
        d.cell(COLUMN_ROAD_ID).cell(COLUMN_RD_START).cell(COLUMN_RD_END).cell(COLUMN_RECORDED).cell(COLUMN_VARIANCE);
        d.where(COLUMN_ROAD_ID, road.getId());
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

    /**
     * Git list of AnomalyData for specific road
     * @param road road to get anomaly data
     * @return list of all anomaly data records
     */
    public ArrayList<AnomalyData> getAnomalyData(Road road){
        ArrayList<AnomalyData> out = new ArrayList<>();
        DBQuery d = new DBQuery(connection).select(TABLE_ANOMALY_DATA);
        d.cell(COLUMN_ROAD_ID).cell(COLUMN_AD_POSITION).cell(COLUMN_RECORDED);
        d.where(COLUMN_ROAD_ID, road.getId());
        ArrayList<HashMap<String, String>> result = d.getResult();

        for(HashMap<String,String> r : result){
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                out.add(new AnomalyData(road, format.parse(r.get(COLUMN_RECORDED)), Integer.parseInt(r.get(COLUMN_AD_POSITION))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return out;
    }

    /**
     * Get quality index key figures
     * @param identifier identifier of qi
     * @return key figures
     */
    public Pair getQualityIndex(String identifier){
        DBQuery d =  new DBQuery(connection).select("QI_" + identifier).cells(COLUMN_QI_MAX_VARIANCE, COLUMN_QI_MIN_VARIANCE);
        ArrayList<HashMap<String, String>> result = d.getResult();
        if(result.size() == 0)
            return null;
        HashMap<String, String>  r =result.get(0);

        return new Pair(Double.parseDouble(r.get(COLUMN_QI_MIN_VARIANCE)), Double.parseDouble(r.get(COLUMN_QI_MAX_VARIANCE)));

    }

    /**
     * Get a road from database
     * @param name name of road
     * @param district distric of road
     * @return road
     */
    public Road getRoad(String name, String district){
        DBQuery d =  new DBQuery(connection).select(TABLE_ROADS).cells(COLUMN_ID, COLUMN_ROAD_NAME, COLUMN_ROAD_DISTRICT, COLUMN_ROAD_LENGTH).where(COLUMN_ROAD_NAME, name).where(COLUMN_ROAD_DISTRICT, district);
        ArrayList<HashMap<String, String>> result = d.getResult();
        if(result.size() == 0)
            return null;
        HashMap<String, String>  r =result.get(0);

        Road road = new Road(Integer.parseInt(r.get(COLUMN_ID)), r.get(COLUMN_ROAD_NAME),  r.get(COLUMN_ROAD_DISTRICT),  Integer.parseInt(r.get(COLUMN_ROAD_LENGTH)), getRoadPaths(Integer.parseInt(r.get(COLUMN_ID))));
        return road;
    }

    /**
     * Get all available roads form database
     * @return list of roads
     */
    public ArrayList<Road> getRoads(){
        DBQuery d =  new DBQuery(connection).select(TABLE_ROADS).cells(COLUMN_ID, COLUMN_ROAD_NAME, COLUMN_ROAD_DISTRICT, COLUMN_ROAD_LENGTH);
        ArrayList<HashMap<String, String>> results = d.getResult();
        ArrayList<Road> out = new ArrayList<>();

        for(HashMap<String, String> r : results) {
            out.add(new Road(Integer.parseInt(r.get(COLUMN_ID)), r.get(COLUMN_ROAD_NAME),  r.get(COLUMN_ROAD_DISTRICT),  Integer.parseInt(r.get(COLUMN_ROAD_LENGTH)), getRoadPaths(Integer.parseInt(r.get(COLUMN_ID)))));
        }

        return out;
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
    private ArrayList<RoadPath> getRoadPaths(int roadID){
        DBQuery d = new DBQuery(connection).select("Road_Nodes").cells("id", "path_id", "latitude", "longitude").where("road_id", String.valueOf(roadID));
        ArrayList<HashMap<String, String>> results = d.getResult();
        ArrayList<RoadPath> out = new ArrayList<>();
        ArrayList<GPSData> nodes = new ArrayList<>();
        RoadPath curr;
        int lastPathId = 0;

        for(HashMap<String, String> r : results){
            GPSData currNode = new GPSData(Double.parseDouble(r.get("latitude")), Double.parseDouble(r.get("longitude")));
            int currPathId = Integer.parseInt(r.get("path_id"));
            if(nodes.size() != 0 && currPathId != lastPathId){
                lastPathId = currPathId;
                out.add(new RoadPath(nodes));
                nodes.clear();
            }
            nodes.add(currNode);
        }
        if(nodes.size() != 0)
            out.add(new RoadPath(nodes));

        return out;
    }


}
