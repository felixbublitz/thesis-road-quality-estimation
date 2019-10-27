package de.felixbublitz.simra_rq;

public class Options {

    //File and DB Paths
    public final static String DATABASE_FILE = "database.db";
    public final static String SIMRA_PATH = "/home/felix/Documents/SimRa/rides/ride";
    public final static String SIMRA_EXTENSION = ".csv";

    //Reverse Geocoding API
    public final static String NOMINATIM_API_ENDPOINT = "http://localhost/nominatim";
    public final static String API_SEARCH = NOMINATIM_API_ENDPOINT + "/search";
    public final static String API_REVERSE_GEOCODING = NOMINATIM_API_ENDPOINT + "/reverse.php";
    public final static int API_TRIES = 20;
    public final static int API_DELAY = 100;

    //HTTP Error Codes
    public final static int HTTP_INTERNAL_ERROR = 500;
    public final static int HTTP_SUCCESS = 200;

}
