package de.felixbublitz.simra_rq.track.road;

import de.felixbublitz.simra_rq.Options;
import de.felixbublitz.simra_rq.etc.GPSOperation;
import de.felixbublitz.simra_rq.etc.ListOperation;
import de.felixbublitz.simra_rq.simra.GPSData;
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

/**
 * RoadGeometry
 */

public class RoadGeometry {

    private ArrayList<RoadPath> paths;
    private ArrayList<Integer> pathLengths;
    private Road road;
    private final int MAX_ROAD_ANGLE = 90;

    /**
     * Create road geometry
     * @param road road
     */
    public RoadGeometry(Road road){
        this.road = road;
        this.paths = getRoadPaths();
        this.pathLengths = getPathLengths();

        if(this.paths.size() == 0)
            throw new IllegalArgumentException("couldn't find any road path");

    }

    /**
     * Create road geometry by road and road paths
     * @param road road
     * @param paths paths of road
     */
    public RoadGeometry(Road road, ArrayList<RoadPath> paths){
        this.road = road;
        if(paths.size() == 0)
            throw new IllegalArgumentException("road Geometry needs at least one path");
        this.paths = paths;
        this.pathLengths = getPathLengths();
    }

    /**
     * Get length of road
     * @return road length
     */
    public int getLength(){
        int len = 0;
        for(RoadPath rp : paths){
            len+=rp.getLength();
        }
        return len;
    }

    /**
     * Returns lengths of all path of road geometry
     * @return list of lengths
     */
    private ArrayList<Integer> getPathLengths(){
        ArrayList<Integer> lengths = new  ArrayList<Integer>();
        for(RoadPath p : paths){
            lengths.add(p.getLength());
        }
        return lengths;
    }

    /**
     * Returns path that contains given road position
     * @param position road position
     * @return road path
     */
    public RoadPath getPath(int position){
        int pos = 0;
        for(RoadPath path : paths){
            pos += path.getLength();
            if(position < pos){
                return path;
            }
        }
        return null;
    }

    /**
     * Get relative road position on given path
     * @param position absolute road position
     * @param p road path
     * @return relative position
     */
    public int getRelativePosition(int position, RoadPath p){
        int index = paths.indexOf(p);
        int offset = (int)(index>0 ?ListOperation.getSum(pathLengths.subList(0,index-1)) : 0);
        for(int i=0; i<index;i++){
            position -= paths.get(i).getLength();
        }
        return position;
    }

    /**
     * Returns gps point by given road position
     * @param absolutePosition absolute road position
     * @return gps point
     */
    public GPSData getGPSPoint(int absolutePosition){
        RoadPath path = getPath(absolutePosition);
        if(path == null)
            return null;
        return path.getGPSPoint(getRelativePosition(absolutePosition, path));
    }

    /**
     * Returns list of road paths
     * @return list of road paths
     */
    public ArrayList<RoadPath> getPaths(){
        return paths;
    }

    /**
     * Returns road position
     * @param gps gps point
     * @return road position
     */
    public Integer getPosition(GPSData gps){
        for(int i=0; i<paths.size(); i++){
            Integer pos = paths.get(i).getPosition(gps);
            if(pos != null)
                return (int)(i>0 ? ListOperation.getSum(pathLengths.subList(0,i)) : 0) + pos;
        }
        return  null;
    }

    /**
     * Returns list of gps data list, which represents road
     * @return list of gps node list
     */
    private ArrayList<ArrayList<GPSData>> getNodes(){
        int tries = 0;
        while (tries <= Options.API_TRIES) {
            tries++;
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response = null;

                String[] parameters = {"street=" + road.getName(),
                        "city=" + road.getDistrict(),
                        "addressdetails=1",
                        "dedupe=0",
                        "polygon_geojson=1",
                        "limit=50",
                        "format=jsonv2"
                };

                response = client.send(
                        HttpRequest
                                .newBuilder(new URI(Options.API_SEARCH + "?" + String.join("&", parameters).replace(" ", "%20")))
                                .GET()
                                .build(),
                        HttpResponse.BodyHandlers.ofString()
                );
                int statusCode = response.statusCode();


                if (statusCode == Options.HTTP_INTERNAL_ERROR) {
                    TimeUnit.MILLISECONDS.sleep(Options.API_DELAY);
                    continue;
                }

                if (statusCode != Options.HTTP_SUCCESS)
                    throw new IllegalStateException("API request error: " + statusCode);

                JSONArray json = new JSONArray(response.body());
                ArrayList<ArrayList<GPSData>> rawNodes = new ArrayList<ArrayList<GPSData>>();

                for (int i = 0; i < json.length(); i++) {
                    if (json.getJSONObject(i).getJSONObject("address").has("road") && json.getJSONObject(i).getJSONObject("address").getString("road").equals(road.getName()) &&
                            !json.getJSONObject(i).getJSONObject("address").has("suburb") ||
                            json.getJSONObject(i).getJSONObject("address").getString("suburb").equals(road.getDistrict()) &&
                                    (!json.getJSONObject(i).has("category") ||
                                            !json.getJSONObject(i).getString("category").equals("railway")) &&
                                    (!json.getJSONObject(i).has("type") ||
                                            (json.getJSONObject(i).getString("type").equals("tertiary") ||
                                                    json.getJSONObject(i).getString("type").equals("primary") ||
                                                    json.getJSONObject(i).getString("type").equals("residential") ||
                                                    json.getJSONObject(i).getString("type").equals("trunk") ||
                                                    json.getJSONObject(i).getString("type").equals("secondary") ||
                                                    json.getJSONObject(i).getString("type").equals("service")||
                                                    json.getJSONObject(i).getString("type").equals("living_street")||
                                                    json.getJSONObject(i).getString("type").equals("unclassified")

                                            ))) {

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
                                return null;
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
            throw new IllegalStateException("API not available");

        }
        throw new IllegalStateException("API not available");

    }

    /**
     * Get road paths of road from nominatim
     * @return list of road paths
     */
    private ArrayList<RoadPath>getRoadPaths(){

        ArrayList<ArrayList<GPSData>> groups = getNodes();
        ArrayList<RoadPath> out = new ArrayList<RoadPath>();

        while(groups.size() != 0) {
            int longestItem = 0;
            int longestLength = 0;
            //get longest Segment
            for (int i = 0; i < groups.size(); i++) {
                RoadPath rp = new RoadPath(groups.get(i));
                int len = rp.getLength();
                if (len > longestLength) {
                    longestItem = i;
                    longestLength = len;
                }
            }

            //bring element to front
            ArrayList<GPSData> groups_out = groups.remove(longestItem);
            boolean appendableGroupsExists = true;

            while (appendableGroupsExists) {
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

            out.add(new RoadPath(groups_out));

        }

        return out;
    }

    /**
     * Merge two list of gps nodes if they are connected
     * @param g1 list of gps nodes
     * @param g2 list of gps nodes
     * @return merged list of gps nodes
     */
    private ArrayList<GPSData> appendGroup(ArrayList<GPSData> g1, ArrayList<GPSData> g2){

        ArrayList<GPSData> out = new ArrayList<GPSData>();

        if(g1.get(0).getDistanceTo(g2.get(0)) == 0 && GPSOperation.getAngle(g1.get(1), g1.get(0), g2.get(1)) < MAX_ROAD_ANGLE ) {
            Collections.reverse(g1);
            out.addAll(g1);
            out.remove(out.size() - 1);
            out.addAll(g2);
            return out;
        }
        if(g1.get(0).getDistanceTo(g2.get(g2.size()-1)) == 0  && GPSOperation.getAngle(g1.get(1), g1.get(0), g2.get(g2.size()-2)) < MAX_ROAD_ANGLE) {
            out.addAll(g2);
            out.remove(g2.size() - 1);
            out.addAll(g1);
            return out;
        }
        if(g1.get(g1.size()-1).getDistanceTo(g2.get(0)) == 0  && GPSOperation.getAngle(g1.get(g1.size()-2), g2.get(0), g2.get(1)) < MAX_ROAD_ANGLE) {
            out.addAll(g1);
            out.remove(out.size()-1);
            out.addAll(g2);
            return out;
        }
        if(g1.get(g1.size()-1).getDistanceTo(g2.get(g2.size()-1)) == 0  && GPSOperation.getAngle(g1.get(g1.size()-2),g1.get(g1.size()-1),g2.get(g2.size()-2)) < MAX_ROAD_ANGLE) {
            out.addAll(g1);
            Collections.reverse(g2);
            out.remove(out.size()-1);
            out.addAll(g2);
            return out;
        }
        return null;
    }

}