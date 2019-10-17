package de.felixbublitz.simra_rq.track;

import de.felixbublitz.simra_rq.database.Database;
import de.felixbublitz.simra_rq.simra.GPSData;
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

    final int MIN_ROAD_DURATION = 10; //seconds

    final String API_REVERSE_GEOCODING = "http://localhost/nominatim/reverse.php?";
    HashMap<String, Road> roads = new HashMap<String, Road>();
    ArrayList<TrackSegment> segments;
    double samplingRate;
    private Database db;


    public Track(ArrayList<GPSData> gpsData, double samplingRate, Database db){
        segments = new ArrayList<TrackSegment>();
        this.samplingRate = samplingRate;
        this.db = db;

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
        ArrayList<TrackSegment> cleanedList = new ArrayList<TrackSegment>();
        List<TrackSegment> processedSegments = new ArrayList<TrackSegment>();

        for(int i=0; i<segments.size();i++){
            if(!processedSegments.contains(segments.get(i))) {
                for (int j = i + 1; j < segments.size(); j++) {
                    if (!processedSegments.contains(segments.get(j)) && segments.get(i).getRoad().equals(segments.get(j).getRoad()) && segments.get(j).getStart() - segments.get(i).getEnd() < MIN_ROAD_DURATION/samplingRate) {
                        segments.set(i, new TrackSegment(segments.get(i).getRoad(), segments.get(i).getStart(), segments.get(j).getEnd()));
                        processedSegments.add(segments.get(j));
                    }
                }
                processedSegments.add(segments.get(i));
                if(segments.get(i).getLength() > MIN_ROAD_DURATION/samplingRate) {
                    cleanedList.add(segments.get(i));
                }
            }
        }

        segments = cleanedList;

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
            segments.set(segments.indexOf(ts), new TrackSegment(road, ts.getStart(), i));
        }else{
            segments.add(new TrackSegment(road, i,i));
        }
    }



    private Road getRoad(String name, String district){
        //intern
        if(roads.containsKey(name+"#"+district)){
            return roads.get(name+"#"+district);
        }

        //db
        Road r = db.getRoad(name, district);
        if(r != null)
            return r;

        //create new
        r = new Road(name, district);
        roads.put(name+"#"+district, r);

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
                        "zoom=16",
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
