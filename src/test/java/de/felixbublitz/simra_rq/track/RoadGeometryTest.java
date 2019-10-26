package de.felixbublitz.simra_rq.track;

import de.felixbublitz.simra_rq.database.Database;
import de.felixbublitz.simra_rq.simra.GPSData;
import de.felixbublitz.simra_rq.simra.SimraData;
import de.felixbublitz.simra_rq.track.road.Road;
import org.junit.jupiter.api.Test;

class RoadGeometryTest {

    @Test
    void getPosition() {
        Database db = new Database("database.db");
        Road r = new Road(db, "In den Ministergärten","Mitte");
        SimraData dataset = new SimraData("/home/felix/Documents/SimRa/rides/ride" + 17+ ".csv");

        r.getPosition(dataset.getGPSData(2045, true));

        r.getPosition(dataset.getGPSData(2045, true));

        r.getPosition(dataset.getGPSData(2045, true));
        r.getPosition(dataset.getGPSData(2356, true));

        int pos = r.getPosition(new GPSData(52.51232293,13.3829604));

        GPSData out = r.getGPSPoint(pos);
        int a = 2;

    }
}