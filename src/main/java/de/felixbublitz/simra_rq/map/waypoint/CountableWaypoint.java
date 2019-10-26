package de.felixbublitz.simra_rq.map.waypoint;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

public class CountableWaypoint extends DefaultWaypoint {
    private String text;

    public String getText() {
        return text;
    }

    public CountableWaypoint(double latitude, double longitude, String text) {
        super(new GeoPosition(latitude, longitude));
        this.text = text;
    }
}
