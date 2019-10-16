package de.felixbublitz.simra_rq.data.track;

import de.felixbublitz.simra_rq.data.simra.GPSData;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Track {

    final String API_REVERSE_GEOCODING = "http://localhost/nominatim/reverse.php?";
    HashMap<String, Road> roads = new HashMap<String, Road>();
    ArrayList<TrackSegment> segments;


    public Track(ArrayList<GPSData> gpsData){
        for (GPSData gps : gpsData){
            if(gps != null) {
                Map<String, String> identifier = getRoadIdentifier(gps);

                if(identifier != null) {
                    Road road = getRoad(identifier.get("road"), identifier.get("district"));
                }

            }
        }
    }



    private Road getRoad(String name, String district){
        if(roads.containsKey(name+"#"+district)){
            return roads.get(name+"#"+district);
        }

        //db

        Road r = new Road(name, district);
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
