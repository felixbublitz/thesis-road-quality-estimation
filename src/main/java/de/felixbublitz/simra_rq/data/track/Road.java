package de.felixbublitz.simra_rq.data.track;

import de.felixbublitz.simra_rq.data.simra.GPSData;
import eu.jacquet80.minigeo.MapWindow;
import eu.jacquet80.minigeo.Point;
import eu.jacquet80.minigeo.Segment;
import org.json.JSONArray;

import java.awt.*;
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
    private int length;
    private ArrayList<GPSData> nodes;

    final int MAX_ROAD_ANGLE =90;

    private final String API_SEARCH = "http://localhost/nominatim/search?";



    public Road(String name, String district){
        this.name = name;
        this.district = district;
        this.nodes = getNodes();
        this.length = getLengt();

        System.out.println("Road " + name + " initiated: " + length + " Meter");
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


    public int getPosition(GPSData gps){
        return -1;
    };


    private int getLengt(){
        int len = 0;
        for(int i =0; i<nodes.size()-1;i++){
            len+=nodes.get(i).getDistanceTo(nodes.get(i+1));
        }

        return (int)Math.round(len*0.1)*10;
    };

    private ArrayList<GPSData> getNodes(){
        return unwrapNodeGroups(getNodesFromNominatim());
    }


    private ArrayList<GPSData> unwrapNodeGroups(ArrayList<ArrayList<GPSData>> groups){
        int longestItem = 0;
        int longestLength = 0;


        //get longest Segment
        for(int i=0; i<groups.size();i++){
            int len = getSegmentLength(groups.get(i));
            if(len > longestLength){
                longestItem = i;
                longestLength = len;
            }
        }

        //bring element to front
        ArrayList<GPSData> groups_out = groups.remove(longestItem);
        boolean appendableGroupsExists = true;

        while(appendableGroupsExists) {
            appendableGroupsExists = false;

            for (int i = 0; i < groups.size(); i++) {
                ArrayList<GPSData> new_group = appendGroup(groups_out, groups.get(i));
                if (new_group != null) {
                    groups_out = new_group;
                    groups.remove(i);
                    appendableGroupsExists = true;
                    break;
                }
            }


        }

        return groups_out;

    }

    private ArrayList<GPSData> appendGroup(ArrayList<GPSData> g1, ArrayList<GPSData> g2){

        ArrayList<GPSData> out = new ArrayList<GPSData>();

        if(name.equals("Lützowufer")){
            int c = 2;
        }

        if(g1.get(0).getDistanceTo(g2.get(0)) == 0 && getAngle(g1.get(1), g1.get(0), g2.get(1)) < MAX_ROAD_ANGLE ) {
            Collections.reverse(g1);
            out.addAll(g1);
            out.remove(out.size() - 1);
            out.addAll(g2);
            return out;
        }
        if(g1.get(0).getDistanceTo(g2.get(g2.size()-1)) == 0  && getAngle(g1.get(1), g1.get(0), g2.get(g2.size()-2)) < MAX_ROAD_ANGLE) {
            out.addAll(g2);
            out.remove(g2.size() - 1);
            out.addAll(g1);
            return out;
        }
        if(g1.get(g1.size()-1).getDistanceTo(g2.get(0)) == 0  && getAngle(g1.get(g1.size()-2), g2.get(0), g2.get(1)) < MAX_ROAD_ANGLE) {
            out.addAll(g1);
            out.remove(out.size()-1);
            out.addAll(g2);
            return out;
        }
        if(g1.get(g1.size()-1).getDistanceTo(g2.get(g2.size()-1)) == 0  && getAngle(g1.get(g1.size()-2),g1.get(g1.size()-1),g2.get(g2.size()-2)) < MAX_ROAD_ANGLE) {
            out.addAll(g1);
            Collections.reverse(g2);
            out.remove(out.size()-1);
            out.addAll(g2);
            return out;
        }

        return null;

    }


    private int getSegmentLength(ArrayList<GPSData> segment){
        int len = 0;
        for(int i=0; i<segment.size()-1;i++){
            len += segment.get(i).getDistanceTo(segment.get(i+1));
        }

        return len;
    }

    private float getAngle(GPSData g1, GPSData g2,  GPSData g3){
        float degree = (float)Math.toDegrees(Math.atan2(g3.getLongitude() - g2.getLongitude(), g3.getLatitude() - g2.getLatitude()) - Math.atan2(g1.getLongitude() - g2.getLongitude(), g1.getLatitude() - g2.getLatitude()));

        if(name.equals("Lützowufer"))
            System.out.println(degree);

        return Math.abs(degree-180);
    }

    private ArrayList<ArrayList<GPSData>> getNodesFromNominatim(){
        int tries = 0;
        while (tries <= 20) {
            tries++;
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response = null;

                String[] parameters = {"street=" + name,
                        "city=" + district,
                        "addressdetails=1",
                        "dedupe=0",
                        "polygon_geojson=1",
                        "limit=50",
                        "format=jsonv2"
                };

                int c = 2;
                response = client.send(
                        HttpRequest
                                .newBuilder(new URI(API_SEARCH + String.join("&", parameters).replace(" ", "%20")))
                                .GET()
                                .build(),
                        HttpResponse.BodyHandlers.ofString()
                );
                int statusCode = response.statusCode();


                if (statusCode == 500) {
                    TimeUnit.MILLISECONDS.sleep(100);
                    continue;
                }

                if (statusCode != 200)
                    return null;


                JSONArray json = new JSONArray(response.body());
                ArrayList<ArrayList<GPSData>> rawNodes = new ArrayList<ArrayList<GPSData>>();


                for (int i = 0; i < json.length(); i++) {
                    if (json.getJSONObject(i).getJSONObject("address").has("road") && json.getJSONObject(i).getJSONObject("address").getString("road").equals(name) &&
                            !json.getJSONObject(i).getJSONObject("address").has("suburb") ||
                            json.getJSONObject(i).getJSONObject("address").getString("suburb").equals(district) &&
                                    (!json.getJSONObject(i).has("category") ||
                                            json.getJSONObject(i).getString("category") != "railway") &&
                                    (!json.getJSONObject(i).has("type") ||
                                            json.getJSONObject(i).getString("type") != "platform")) {

                        JSONArray nodes = json.getJSONObject(i).getJSONObject("geojson").getJSONArray("coordinates");

                        if (!(nodes.get(0) instanceof JSONArray)) {
                            JSONArray newNodes = new JSONArray();
                            newNodes.put(nodes);
                            nodes = newNodes;
                        }

                        if (nodes.getJSONArray(0).get(0) instanceof JSONArray) {
                            nodes = nodes.getJSONArray(0);
                        }


                        ArrayList<GPSData> group = new ArrayList<GPSData>();
                        for (int j = 0; j < nodes.length(); j++) {
                            try {
                                GPSData gps = new GPSData(nodes.getJSONArray(j).getDouble(1), nodes.getJSONArray(j).getDouble(0));
                                group.add(gps);
                            } catch (Exception e) {
                                int a = 2;
                            }

                        }
                        rawNodes.add(group);
                    }
                }

                return rawNodes;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            return null;
        }
        return null;
    }
}
