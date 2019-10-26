package de.felixbublitz.simra_rq;

import de.felixbublitz.simra_rq.quality.DataSegment;
import de.felixbublitz.simra_rq.simra.GPSData;
import de.felixbublitz.simra_rq.simra.SimraData;
import de.felixbublitz.simra_rq.track.*;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.viewer.*;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Stream;

public class DebugHelper {

    public static final boolean DEBUG_ROADS = false;
    public static final boolean DEBUG_ROADS_SEGMENTS = true;

    public static final boolean DEBUG_SEGMENTS = true;
    public static final boolean DEBUG_TRACK = true;
    public static final boolean DEBUG_POS = false;
    public static final boolean DEBUG_ROADS_PATH = true;
    public static final boolean DEBUG_GET_POSITION = true;







    public static void showSegments(ArrayList<Double> filteredMagnitudes, ArrayList<DataSegment> segments){
        if(!DEBUG_SEGMENTS)
            return;
        double[] xData = new double[filteredMagnitudes.size()];
        for( int i = 0; i < filteredMagnitudes.size(); i++ )
            xData[i] = i+1;

        Double[] yData = filteredMagnitudes.toArray(new Double[filteredMagnitudes.size()]);


        //XYChart chart = QuickChart.getChart("Sample Chart", "X", "Y", "y(x)", xData, Stream.of(yData).mapToDouble(Double::doubleValue).toArray());
        XYChart chart = new XYChartBuilder().width(3000).height(400).title("").xAxisTitle("X").yAxisTitle("Y").build();
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        chart.getStyler().setMarkerSize(1);

        chart.addSeries("Data", xData, Stream.of(yData).mapToDouble(Double::doubleValue).toArray() );
        chart.getStyler().setLegendVisible(true);


        //chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.StepArea);

        int i =0;
        for(DataSegment ds : segments) {
            i++;
            chart.addSeries("var "+i+": " + Math.round(ds.getStandardDeviration()*100.0)/100.0  , new double[]{ds.getStart(), ds.getStart()}, new double[]{-0.5,0.5});
        }

        new SwingWrapper(chart).displayChart();

    }


    public static void showOnMap(Road r){

        JXMapViewer mapViewer = new JXMapViewer();

        // Display the viewer in a JFrame
        JFrame frame = new JFrame("JXMapviewer2 Example 2");
        frame.getContentPane().add(mapViewer);
        frame.setSize(1500, 1000);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setVisible(true);


        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);


        // Create a track from the geo-positions
        ArrayList l = new ArrayList();
        ArrayList<GeoPosition> track = new ArrayList<>();

        ArrayList<GeoPosition> ctrack = new ArrayList<>();

        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.PINK, Color.BLACK, Color.ORANGE};

        int i=0;
        for(RoadPath p : r.getRoadGeometry().getPaths()){
            track = new ArrayList<>();

            for(RoadNode n: p.getNodes()){
                ctrack.add(new GeoPosition(n.getGPSData().getLatitude(),n.getGPSData().getLongitude()));
                track.add(new GeoPosition(n.getGPSData().getLatitude(),n.getGPSData().getLongitude()));

            }
            RoutePainter routePainter = new RoutePainter(track, colors[i%colors.length], 2);
            l.add(routePainter);
            i++;
        }


        // Set the focus
        mapViewer.zoomToBestFit(new HashSet<GeoPosition>(ctrack), 1);


        CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(l);
        mapViewer.setOverlayPainter(painter);

    }

    public static void showOnMap(TrackSegment t, SimraData sd){
        if(!DEBUG_TRACK)
            return;


        JXMapViewer mapViewer = new JXMapViewer();

        // Display the viewer in a JFrame
        JFrame frame = new JFrame("JXMapviewer2 Example 2");
        frame.getContentPane().add(mapViewer);
        frame.setSize(3000, 1000);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setVisible(true);


        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);

        ArrayList l = new ArrayList();

        // Create a track from the geo-positions



      /*  for(RoadPath p : t.getRoad().getRoadGeometry().getPaths()){
            for(RoadNode n: p.getNodes()){
                track.add(new GeoPosition(n.getGPSData().getLatitude(),n.getGPSData().getLongitude()));

            }
        }
        RoutePainter routePainter = new RoutePainter(track, Color.GRAY, 6);
        l.add(routePainter);*/

        ArrayList<GeoPosition> cTrack = new ArrayList<>();
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.PINK, Color.BLACK, Color.ORANGE};

        int i=0;
        RoadPath p = t.getTrackPath();
        if(p== null)
            return;

            ArrayList<GeoPosition> track = new ArrayList<>();
            for(RoadNode n: p.getNodes()){
                track.add(new GeoPosition(n.getGPSData().getLatitude(),n.getGPSData().getLongitude()));
                cTrack.add(new GeoPosition(n.getGPSData().getLatitude(),n.getGPSData().getLongitude()));

            }
            RoutePainter routePainter = new RoutePainter(track, colors[i%colors.length], 2);
            l.add(routePainter);
            i++;




        track  = new ArrayList<>();
        for(GPSData g : sd.getGPSData()){
            if(g != null) {
                track.add(new GeoPosition(g.getLatitude(), g.getLongitude()));
            }

        }
        routePainter = new RoutePainter(track, Color.GREEN, 1);
        l.add(routePainter);


        // Set the focus
        mapViewer.zoomToBestFit(new HashSet<GeoPosition>(cTrack), 1);



        CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(l);
        mapViewer.setOverlayPainter(painter);
        while(frame.isVisible()) {
            try {
                Thread.sleep(
                        1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    public static void showOnMap(Road r, GPSData ... nodes){
        if(!DEBUG_ROADS_SEGMENTS)
            return;
        JXMapViewer mapViewer = new JXMapViewer();

        // Display the viewer in a JFrame
        JFrame frame = new JFrame("JXMapviewer2 Example 2");
        frame.getContentPane().add(mapViewer);
        frame.setSize(3000, 1000);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setVisible(true);


        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);


        ArrayList l = new ArrayList();
        ArrayList<GeoPosition> track = new ArrayList<>();

        ArrayList<GeoPosition> ctrack = new ArrayList<>();

        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.PINK, Color.BLACK, Color.ORANGE};

        int i=0;
        for(RoadPath p : r.getRoadGeometry().getPaths()){
            track = new ArrayList<>();

            for(RoadNode n: p.getNodes()){
                ctrack.add(new GeoPosition(n.getGPSData().getLatitude(),n.getGPSData().getLongitude()));
                track.add(new GeoPosition(n.getGPSData().getLatitude(),n.getGPSData().getLongitude()));

            }
            RoutePainter routePainter = new RoutePainter(track, colors[i%colors.length], 2);
            l.add(routePainter);
            i++;
        }


        HashSet<Waypoint> waypoints = new HashSet<Waypoint>();
        HashSet<GeoPosition> geo =  new HashSet<GeoPosition>();


        for(i=0; i<nodes.length; i++) {
            GPSData g = nodes[i];
            waypoints.add(new AdvWaipoint(g.getLatitude(), g.getLongitude(), ""+i));
            geo.add(new GeoPosition(g.getLatitude(), g.getLongitude()));
        }

        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();
        WaypointRenderer a = new AdvWaypointRenderer();
        waypointPainter.setRenderer(a);
        waypointPainter.setWaypoints(waypoints);

        RoutePainter routePainter = new RoutePainter(track, Color.RED, 2);

        // Set the focus
        mapViewer.zoomToBestFit(new HashSet<GeoPosition>(ctrack), 1);

        l.add(waypointPainter);
        l.add(routePainter);
        CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(l);
        mapViewer.setOverlayPainter(painter);
        while(frame.isVisible()) {
            try {
                Thread.sleep(
                        1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }



    public static void showOnMap(RoadPath p, GPSData ... nodes){
        if(!DEBUG_ROADS_PATH)
            return;
        JXMapViewer mapViewer = new JXMapViewer();

        // Display the viewer in a JFrame
        JFrame frame = new JFrame("JXMapviewer2 Example 2");
        frame.getContentPane().add(mapViewer);
        frame.setSize(3000, 1000);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setVisible(true);


        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);


        // Create a track from the geo-positions
        ArrayList<GeoPosition> track = new ArrayList<>();

            for(RoadNode n: p.getNodes()){
                track.add(new GeoPosition(n.getGPSData().getLatitude(),n.getGPSData().getLongitude()));

            }


        ArrayList l = new ArrayList();
        HashSet<Waypoint> waypoints = new HashSet<Waypoint>();
        HashSet<GeoPosition> geo =  new HashSet<GeoPosition>();

        for(int i=0; i<nodes.length;i++) {
            GPSData g = nodes[i];
            waypoints.add(new AdvWaipoint(g.getLatitude(), g.getLongitude(), ""+i));
            geo.add(new GeoPosition(g.getLatitude(), g.getLongitude()));
        }

        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();
        WaypointRenderer a = new AdvWaypointRenderer();
        waypointPainter.setRenderer(a);

        waypointPainter.setWaypoints(waypoints);


        RoutePainter routePainter = new RoutePainter(track, Color.RED, 2);

        // Set the focus
        mapViewer.zoomToBestFit(new HashSet<GeoPosition>(track), 1);

        l.add(waypointPainter);
        l.add(routePainter);
        CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(l);
        mapViewer.setOverlayPainter(painter);
        while(frame.isVisible()) {
            try {
                Thread.sleep(
                        1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    public static void showOnMap(GPSData ...gpsData){
        if(!DEBUG_POS)
            return;
        JXMapViewer mapViewer = new JXMapViewer();

        // Display the viewer in a JFrame
        JFrame frame = new JFrame("JXMapviewer2 Example 2");
        frame.getContentPane().add(mapViewer);
        frame.setSize(1500, 1000);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setVisible(true);


        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);
        HashSet<Waypoint> waypoints = new HashSet<Waypoint>();
        HashSet<GeoPosition> geo =  new HashSet<GeoPosition>();

        for(GPSData g : gpsData) {
           waypoints.add(new DefaultWaypoint(new GeoPosition(g.getLatitude(), g.getLongitude())));
           geo.add(new GeoPosition(g.getLatitude(), g.getLongitude()));
        }

        WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();
        waypointPainter.setWaypoints(waypoints);


        // Set the focus
        mapViewer.zoomToBestFit(geo, 1);


        CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(waypointPainter);
        mapViewer.setOverlayPainter(painter);
        while(frame.isVisible()) {
            try {
                Thread.sleep(
                        1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static void showOnMap(Track t, SimraData sd){
        if(!DEBUG_TRACK)
            return;
        JXMapViewer mapViewer = new JXMapViewer();

        // Display the viewer in a JFrame
        JFrame frame = new JFrame("JXMapviewer2 Example 2");
        frame.getContentPane().add(mapViewer);
        frame.setSize(1500, 1000);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        mapViewer.setTileFactory(tileFactory);


        // Create a track from the geo-positions


        ArrayList l = new ArrayList();



        for(TrackSegment ts :  t.getSegments()){
            HashSet<Waypoint> waypoints = null;

            waypoints = new HashSet<Waypoint>();


            Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.PINK, Color.BLACK, Color.ORANGE};

            int i=0;
            RoadPath p = ts.getTrackPath();

            if(p == null)
                return;

                ArrayList<GeoPosition> track = new ArrayList<>();
                for(RoadNode n : p.getNodes()){
                    track.add(new GeoPosition(n.getGPSData().getLatitude(),n.getGPSData().getLongitude()));
                }
                RoutePainter routePainter = new RoutePainter(track, colors[i%colors.length], 6);
                l.add(routePainter);
                i++;




           // ArrayList<GeoPosition> track = new ArrayList<>();
          //  for(GPSData g : ts.getNodes()){
                //track.add(new GeoPosition(g.getLatitude(),g.getLongitude()));
           // }
          //  RoutePainter routePainter = new RoutePainter(track, Color.RED, 4);

            //l.add(routePainter);

         //   WaypointPainter<Waypoint> waypointPainter = new WaypointPainter<Waypoint>();
         //   waypointPainter.setWaypoints(waypoints);
        //    l.add(waypointPainter);
        }





        ArrayList<GeoPosition> track  = new ArrayList<>();
        for(GPSData g : sd.getGPSData()){
            if(g != null) {
                track.add(new GeoPosition(g.getLatitude(), g.getLongitude()));
            }

        }
        RoutePainter routePainter = new RoutePainter(track, Color.GREEN, 1);
        l.add(routePainter);



        // Set the focus
        mapViewer.zoomToBestFit(new HashSet<GeoPosition>(track), 1);


        CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(l);
        mapViewer.setOverlayPainter(painter);

    }

}
