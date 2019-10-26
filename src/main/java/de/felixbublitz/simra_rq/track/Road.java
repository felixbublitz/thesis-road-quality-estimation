package de.felixbublitz.simra_rq.track;

import de.felixbublitz.simra_rq.DebugHelper;
import de.felixbublitz.simra_rq.database.Database;
import de.felixbublitz.simra_rq.simra.GPSData;
import de.felixbublitz.simra_rq.simra.SimraData;
import org.json.JSONArray;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

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

        if(DebugHelper.DEBUG_ROADS)
            DebugHelper.showOnMap(this);
    }
    public Road(int id, String name, String district, int length, ArrayList<RoadPath> roadPaths) {
        this.id = id;
        this.name = name;
        this.district = district;
        this.geometry = new RoadGeometry(this, roadPaths);

        if(DebugHelper.DEBUG_ROADS)
            DebugHelper.showOnMap(this);
    }
    public Road(Database db, String name, String district){
        this.name = name;
        this.district = district;
        this.geometry = new RoadGeometry(this);
        this.id = db.addRoad(this);

        if(DebugHelper.DEBUG_ROADS)
            DebugHelper.showOnMap(this);

        System.out.println("Road " + name + " initiated: " + getLength() + " Meter");



        /*MapWindow window = new MapWindow();
        for(int i=0;i<nodes.size()-1;i++){

            window.addSegment( new Segment( new Point(nodes.get(i).getLatitude(), nodes.get(i).getLongitude()), new Point(nodes.get(i+1).getLatitude(), nodes.get(i+1).getLongitude()), Color.RED));
            System.out.println(nodes.get(i).getLatitude()+","+ nodes.get(i).getLongitude());
        }

        window.setVisible(true);
*/
       /* try {
            TimeUnit.SECONDS.sleep(2);
            if(name.equals("Lützowufer"))
            TimeUnit.MINUTES.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/

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
   /* public int getPosition(GPSData px){
        double shortest_dist = Integer.MAX_VALUE;
        double len = -1;
        int max_dist_from_track = 50; //meter
        double tempLength = 0;


        for(int i=0; i<nodes.size()-1;i++){
            GPSData p1 = nodes.get(i);
            GPSData p2 = nodes.get(i+1);

            double xDist = p1.getLatitude()-p2.getLatitude();
            double yDist = p1.getLongitude()-p2.getLongitude();

            if(xDist == 0)
                continue;

            double m1 = yDist/xDist;
            double b1 = -((m1*p1.getLatitude())-p1.getLongitude());

            if (m1 == 0)
                continue;

            double m2 = -1/m1;
            double b2 = -(m2*px.getLatitude()-px.getLongitude());
            double ix = (b1-b2)/(m2-m1);
            double iy = m1*ix+b1;
            GPSData intersect = new GPSData(ix, iy);

            double dist = Math.sqrt(Math.pow(px.getLatitude()-intersect.getLatitude(),2) + Math.pow(px.getLongitude()-intersect.getLongitude(),2));

            if (dist < shortest_dist && intersect.getLatitude() <= Math.max(p1.getLatitude(), p2.getLatitude()) && intersect.getLatitude() >= Math.min(p1.getLatitude(), p2.getLatitude()) && intersect.getDistanceTo(px) <= max_dist_from_track){
                shortest_dist = dist;
                len = tempLength + p1.getDistanceTo(intersect);
            }
            tempLength += p1.getDistanceTo(p2);
        }
        if(len == -1) {
            len = 0;
            shortest_dist = Integer.MAX_VALUE;
            tempLength = 0;
            for (int i = 1; i < nodes.size(); i++) {
                if (px.getDistanceTo(nodes.get(i)) < shortest_dist) {
                    shortest_dist = px.getDistanceTo(nodes.get(i));
                    len = tempLength + nodes.get(i).getDistanceTo(nodes.get(i - 1));
                }else{
                    if (i != 0)
                    tempLength += nodes.get(i).getDistanceTo(nodes.get(i - 1));
                }
            }
        }

        DebugHelper.showOnMap(px, getGPSPoint((int)len));

        return (int)Math.round((int)len*0.1)*10;


    };*/


}
