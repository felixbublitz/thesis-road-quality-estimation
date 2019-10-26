package de.felixbublitz.simra_rq;

import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

public class AdvWaipoint extends DefaultWaypoint {
    private String text;

    public String getText() {
        return text;
    }

    public AdvWaipoint(double latitude, double longitude, String text) {
        super(new GeoPosition(latitude, longitude));
        this.text = text;
    }
}
