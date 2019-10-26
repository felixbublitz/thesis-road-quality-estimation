package de.felixbublitz.simra_rq.mapview;

import org.jxmapviewer.viewer.TileFactoryInfo;

    public class AdvancedOSMTileFactoryInfo extends TileFactoryInfo {
        private static final int max = 19;

        public AdvancedOSMTileFactoryInfo() {
            this("OpenStreetMap", "http://tile.openstreetmap.org");
        }

        public AdvancedOSMTileFactoryInfo(String name, String baseURL) {
            super(name, 1, 17, 19, 256, true, true, baseURL, "x", "y", "z");
        }

        public String getTileUrl(int x, int y, int zoom) {
            zoom = 19 - zoom;
            String url = this.baseURL + "/" + zoom + "/" + x + "/" + y + ".png";
            return url;
        }
    }

