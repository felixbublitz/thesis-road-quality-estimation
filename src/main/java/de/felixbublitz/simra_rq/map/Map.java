package de.felixbublitz.simra_rq.map;

import de.felixbublitz.simra_rq.map.adapter.SelectionAdapter;
import de.felixbublitz.simra_rq.map.painter.RectPainter;
import de.felixbublitz.simra_rq.map.painter.RoutePainter;
import de.felixbublitz.simra_rq.map.painter.SelectionPainter;
import de.felixbublitz.simra_rq.map.waypoint.CountableWaipointRenderer;
import de.felixbublitz.simra_rq.map.waypoint.CountableWaypoint;
import de.felixbublitz.simra_rq.simra.GPSData;
import de.felixbublitz.simra_rq.track.road.RoadNode;
import de.felixbublitz.simra_rq.track.road.RoadPath;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.cache.FileBasedLocalCache;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.viewer.*;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Basic Street Map View creator based on JXMapv2
 */

public class Map {
    private JXMapViewer mapViewer;
    private int width;
    private int height;
    private ArrayList painter = new ArrayList();

    /**
     * Create a new Map with given window size
     * @param width width
     * @param height height
     */
    public Map(int width, int height){
        this.width = width;
        this.height = height;
        initMap();
    }

    /**
     * Add path layer to map
     * @param path path
     * @param color color
     */
    public void addPath(RoadPath path, Color color){
        ArrayList<GeoPosition> track = new ArrayList<>();

        for(RoadNode n: path.getNodes()){
            track.add(new GeoPosition(n.getGPSData().getLatitude(),n.getGPSData().getLongitude()));
        }
        painter.add(new RoutePainter(track, color, 2));
    }

    /**
     * Add multiple paths to map
     * @param paths list of paths
     * @param color color
     */
    public void addPaths(ArrayList<RoadPath> paths, Color color){
        for(RoadPath path: paths){
            addPath(path, color);
        }
    }

    /**
     * Add GPS Points to map
     * @param nodes one or multiple GPS Data
     */
    public void addNodes(GPSData ...nodes){
        HashSet<Waypoint> waypoints = new HashSet<Waypoint>();
        HashSet<GeoPosition> geo =  new HashSet<GeoPosition>();
        for(int i=0; i<nodes.length; i++) {
            GPSData node = nodes[i];
            waypoints.add(new CountableWaypoint(node.getLatitude(), node.getLongitude(), ""+i));
            geo.add(new GeoPosition(node.getLatitude(), node.getLongitude()));
        }

        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();
        WaypointRenderer a = new CountableWaipointRenderer();
        waypointPainter.setRenderer(a);
        waypointPainter.setWaypoints(waypoints);
    }

    /**
     * Add a rectangle overlay with given color
     * @param color color
     */
    public void addRectangle(Color color){
        painter.add(new RectPainter(color));
    }

    /**
     * show the created map
     */
    public void show(){
        CompoundPainter<JXMapViewer> cPainter = new CompoundPainter<JXMapViewer>(painter);
        mapViewer.setOverlayPainter(cPainter);

        final JFrame frame = new JFrame();
        frame.setLayout(new BorderLayout());
        frame.add(mapViewer);
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        updateWindowTitle(frame, mapViewer);
    }

    /**
     * Set zoom of map
     * @param zoom zoom level
     */
    public void setZoom(int zoom){
        mapViewer.setZoom(6);
    }

    /**
     * set focus of map
     * @param gps gps data
     */
    public void setLocation(GPSData gps){
        mapViewer.setAddressLocation(new GeoPosition(gps.getLatitude(), gps.getLongitude()));
    }

    private static void updateWindowTitle(JFrame frame, JXMapViewer mapViewer)
    {
        double lat = mapViewer.getCenterPosition().getLatitude();
        double lon = mapViewer.getCenterPosition().getLongitude();
        int zoom = mapViewer.getZoom();
    }

    /**
     * init the JXMapv2
     */
    public void initMap(){
        // Create a TileFactoryInfo for OpenStreetMap
        OSMTileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);

        // Setup local file cache
        File cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");
        tileFactory.setLocalCache(new FileBasedLocalCache(cacheDir, false));

        // Setup JXMapViewer
        mapViewer = new JXMapViewer();
        mapViewer.setTileFactory(tileFactory);

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
    }
}
