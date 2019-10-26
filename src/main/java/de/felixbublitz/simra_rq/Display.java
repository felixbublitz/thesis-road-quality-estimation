package de.felixbublitz.simra_rq;

import de.felixbublitz.simra_rq.database.AnomalyData;
import de.felixbublitz.simra_rq.database.RoughnessData;
import de.felixbublitz.simra_rq.database.Database;
import de.felixbublitz.simra_rq.mapview.AdvancedOSMTileFactoryInfo;
import de.felixbublitz.simra_rq.mapview.SelectionAdapter;
import de.felixbublitz.simra_rq.mapview.SelectionPainter;
import de.felixbublitz.simra_rq.track.Road;
import de.felixbublitz.simra_rq.track.RoadNode;
import de.felixbublitz.simra_rq.track.RoadPath;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.cache.FileBasedLocalCache;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import javax.xml.crypto.Data;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;

public class Display {

    private static Database db;

    public static void addMarker(JXMapViewer mapViewer){
        ArrayList l = new ArrayList();
        l.add(new RectPainter(new Color(150,150,150,160)));
        db = new Database("database.db");

        ArrayList<Road> roads = db.getRoads();
        for(Road road: roads){
            ArrayList<RoughnessData> roughnessData = db.getRoughnessData(road);
            addRoad(road, roughnessData, l);
        }


        CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(l);
        mapViewer.setOverlayPainter(painter);
    }

    private static void addRoad(Road r, ArrayList<RoughnessData> roughnessData, ArrayList l){
        double varianceSum = 0;
        for(RoughnessData rd : roughnessData){
            varianceSum += rd.getVariance();
        }

        QualityIndex dynamicQI = new DynamicQualityIndex(db);

        double variance = varianceSum/roughnessData.size();

        if(roughnessData.size() == 0)
            return;

        for(RoadPath p : r.getRoadGeometry().getPaths()){
            ArrayList<GeoPosition> track = new ArrayList<>();

            for(RoadNode n: p.getNodes()){
                track.add(new GeoPosition(n.getGPSData().getLatitude(),n.getGPSData().getLongitude()));

            }
            RoutePainter routePainter = new RoutePainter(track, getColor(dynamicQI.getQuality(variance)), 2);
            l.add(routePainter);
        }
    }

    private static Color getColor(double score){
        int r = (int)(Math.max(0,Math.min(1, 2*(1-score))*255));
        int g = (int)(Math.max(0,Math.min(1, 2*score)*255));
        return new Color(r,g,0);
    }


    public static void main(String[] args)
    {
        // Create a TileFactoryInfo for OpenStreetMap
        AdvancedOSMTileFactoryInfo info = new AdvancedOSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);

        // Setup local file cache
        File cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");
        tileFactory.setLocalCache(new FileBasedLocalCache(cacheDir, false));

        // Setup JXMapViewer
        final JXMapViewer mapViewer = new JXMapViewer();
        mapViewer.setTileFactory(tileFactory);

        GeoPosition frankfurt = new GeoPosition(52.5048, 13.4056);

        // Set the focus
        mapViewer.setZoom(6);
        mapViewer.setAddressLocation(frankfurt);

        // Add interactions
        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);

        mapViewer.addMouseListener(new CenterMapListener(mapViewer));

        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));

        mapViewer.addKeyListener(new PanKeyListener(mapViewer));

        // Add a selection painter
        SelectionAdapter sa = new SelectionAdapter(mapViewer);
        SelectionPainter sp = new SelectionPainter(sa);
        mapViewer.addMouseListener(sa);
        mapViewer.addMouseMotionListener(sa);
        mapViewer.setOverlayPainter(sp);
        addMarker(mapViewer);

        // Display the viewer in a JFrame
        final JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.add(new JLabel("Use left mouse button to pan, mouse wheel to zoom and right mouse to select"), BorderLayout.NORTH);
        frame.add(mapViewer);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        mapViewer.addPropertyChangeListener("zoom", new PropertyChangeListener()
        {
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                updateWindowTitle(frame, mapViewer);
            }
        });

        mapViewer.addPropertyChangeListener("center", new PropertyChangeListener()
        {
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                updateWindowTitle(frame, mapViewer);
            }
        });

        updateWindowTitle(frame, mapViewer);
    }

    protected static void updateWindowTitle(JFrame frame, JXMapViewer mapViewer)
    {
        double lat = mapViewer.getCenterPosition().getLatitude();
        double lon = mapViewer.getCenterPosition().getLongitude();
        int zoom = mapViewer.getZoom();

        frame.setTitle(String.format("JXMapviewer2 Example 3 (%.2f / %.2f) - Zoom: %d", lat, lon, zoom));
    }


}
