package de.felixbublitz.simra_rq.track;

import de.felixbublitz.simra_rq.database.Database;
import de.felixbublitz.simra_rq.simra.GPSData;
import de.felixbublitz.simra_rq.simra.SimraData;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Track {

    public final static int MIN_SEGMENT_DISTANCE = 20; //meter

    final String API_REVERSE_GEOCODING = "http://localhost/nominatim/reverse.php?";
    HashMap<String, Road> roads = new HashMap<String, Road>();
    ArrayList<TrackSegment> segments;
    double samplingRate;
    private Database db;
    private SimraData sd;


    public Track(SimraData sd, Database db){
        long startTime = System.currentTimeMillis();
        segments = new ArrayList<TrackSegment>();
        this.samplingRate = sd.getSamplingRate();
        this.db = db;
        this.sd = sd;
        ArrayList<GPSData> gpsData = sd.getGPSData();

        for (int i =0; i < gpsData.size(); i++){
            if(gpsData.get(i) != null) {
                Map<String, String> identifier = getRoadIdentifier(gpsData.get(i));

                if(identifier != null) {
                    Road road = getRoad(identifier.get("road"), identifier.get("district"));
                    addRoadToTrack(road, i);
                }

            }
        }

        cleanTrack();
        System.out.println("Track Gen Runtime: " + (System.currentTimeMillis() - startTime) + "ms");

    }

    public ArrayList<TrackSegment> getSegments(){
        return segments;
    }

    public TrackSegment getSegment(int x){
        for(TrackSegment s : segments){
            if(s.getStart() <= x && s.getEnd() >= x)
                return s;
        }
        return null;
    }

    private void cleanTrack(){

        List<TrackSegment> processedSegments = new ArrayList<TrackSegment>();

        HashMap<Road, Integer> recorededLen = new HashMap<Road, Integer>();
        ArrayList<TrackSegment> out = new ArrayList<TrackSegment>();

        for(int i=0; i<segments.size();i++){
            Road r = segments.get(i).getRoad();
            if(recorededLen.containsKey(r))
                recorededLen.replace(r, recorededLen.get(r) + segments.get(i).getLength());
            recorededLen.put(r, segments.get(i).getLength());
        }

        ArrayList<TrackSegment> cleanedList = new ArrayList<TrackSegment>();

        for(int i=0; i<segments.size();i++){
            Road r = segments.get(i).getRoad();
            if(recorededLen.get(r) >= MIN_SEGMENT_DISTANCE)
                cleanedList.add(segments.get(i));
        }

        int i=0;

        while(i<cleanedList.size()-1){
            TrackSegment s1 = cleanedList.get(i);
            TrackSegment s2 = cleanedList.get(i+1);
            if(s1.getRoad() == s2.getRoad()){
                cleanedList.set(i+1, new TrackSegment(s1.getRoad(),sd, s1.getStart(), s2.getEnd()));
                i++;
            }else{
                out.add(s1);
                i++;
            }
        }

        segments = out;

    }

    private TrackSegment getSegment(Road r){
        for(TrackSegment ts : segments){
            if(ts.getRoad() == r)
                return ts;
        }
        return null;
    }

    private TrackSegment getLastSegment(){
        if(segments.size() == 0)
            return null;

        return segments.get(segments.size()-1);
    }

    private void addRoadToTrack(Road road, int i){
        TrackSegment ts = getLastSegment();
        if(ts != null && ts.getRoad() == road){
            segments.set(segments.indexOf(ts), new TrackSegment(road,sd, ts.getStart(), i));
        }else{
            segments.add(new TrackSegment(road,sd, i,i));
        }
    }



    private Road getRoad(String name, String district){

        Road r;

        //intern
        r = roads.get(name+"#"+district);

        //db
        if(r == null)
            r = db.getRoad(name, district);

        //create new
        if(r == null)
            r = new Road(db, name, district);

        roads.put(name + "#" + district, r);
        return r;

    }

    private Map<String, String> getRoadIdentifier(GPSData gps) {
        int tries = 0;
        while (tries <= 3) {
            tries++;
            try {
                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response = null;

                String[] parameters = {"lat=" + String.valueOf(gps.getLatitude()),
                        "lon=" + String.valueOf(gps.getLongitude()),
                        "zoom=17",
                        "format=jsonv2"};

                response = client.send(
                        HttpRequest
                                .newBuilder(new URI(API_REVERSE_GEOCODING + String.join("&", parameters)))
                                .GET()
                                .build(),
                        HttpResponse.BodyHandlers.ofString()
                );
                int statusCode = response.statusCode();

                if (statusCode == 500) {
                    continue;
                }


                if (statusCode != 200)
                    return null;

                JSONObject json = new JSONObject(response.body());


                Map<String, String> out = new HashMap<String, String>();


                if(!(json.has("address") && json.has("osm_type") && json.getString("osm_type").equals("way") && json.getJSONObject("address").has("road"))){
                    return null;
                }

                if(json.has("type") && json.getString("type").equals("motorway")){
                    return null;
                }

                if (!json.getJSONObject("address").getString("road").equals("")) {
                    out.put("road", json.getJSONObject("address").getString("road"));
                }

                if (!json.getJSONObject("address").getString("suburb").equals("")) {
                    out.put("district", json.getJSONObject("address").getString("suburb"));
                } else {
                    out.put("district", "");
                }


                return out;

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
