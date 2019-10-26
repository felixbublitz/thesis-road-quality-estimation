package de.felixbublitz.simra_rq.track;

import de.felixbublitz.simra_rq.database.Database;
import de.felixbublitz.simra_rq.simra.SimraData;
import de.felixbublitz.simra_rq.track.road.Road;
import org.junit.jupiter.api.Test;

class RoadPathTest {
    @Test
    void t1() {
        Database db = new Database("database.db");
        Road r = new Road(db, "Am Falkenberg", "Bohnsdorf");
        SimraData sd = new SimraData("/home/felix/Documents/SimRa/rides/ride" + 14 + ".csv");

        TrackSegment ts = new TrackSegment(r, sd, 0, 69);
    }



}