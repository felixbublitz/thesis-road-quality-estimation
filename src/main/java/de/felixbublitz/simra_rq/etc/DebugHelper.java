package de.felixbublitz.simra_rq.etc;

import de.felixbublitz.simra_rq.map.Map;
import de.felixbublitz.simra_rq.quality.segments.DataSegment;
import de.felixbublitz.simra_rq.simra.GPSData;
import de.felixbublitz.simra_rq.simra.SimraData;
import de.felixbublitz.simra_rq.track.*;
import de.felixbublitz.simra_rq.track.road.Road;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Stream;

public class DebugHelper {


    public static void showSegments(ArrayList<Double> filteredMagnitudes, ArrayList<DataSegment> segments){

        double[] xData = new double[filteredMagnitudes.size()];
        for( int i = 0; i < filteredMagnitudes.size(); i++ )
            xData[i] = i+1;

        Double[] yData = filteredMagnitudes.toArray(new Double[filteredMagnitudes.size()]);

        XYChart chart = new XYChartBuilder().width(3000).height(400).title("").xAxisTitle("X").yAxisTitle("Y").build();
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        chart.getStyler().setMarkerSize(1);

        chart.addSeries("Data", xData, Stream.of(yData).mapToDouble(Double::doubleValue).toArray() );
        chart.getStyler().setLegendVisible(true);

        int i =0;
        for(DataSegment ds : segments) {
            i++;
            chart.addSeries("var "+i+": " + Math.round(ds.getStandardDeviration()*100.0)/100.0  , new double[]{ds.getStart(), ds.getStart()}, new double[]{-0.5,0.5});
        }

        new SwingWrapper(chart).displayChart();

    }


    public static void showOnMap(Road r){
        Map map = new Map(3000,1000);
        map.addPaths(r.getRoadGeometry().getPaths(), Color.RED);
        map.show();
    }

    public static void showOnMap(TrackSegment t, SimraData sd){
        Map map = new Map(3000,1000);
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.PINK, Color.BLACK, Color.ORANGE};

        RoadPath path = t.getTrackPath();
        if(path== null)
            return;
        map.addPath(path, Color.RED);
        map.addPath(new RoadPath(sd.getGPSData()), Color.GREEN);
        map.show();
    }


    public static void showOnMap(Road r, GPSData ... nodes){
        Map map = new Map(3000,1000);
        map.addPaths(r.getRoadGeometry().getPaths(), Color.RED);
        map.addNodes(nodes);
        map.show();
    }

    public static void showOnMap(RoadPath path, GPSData ... nodes){
        Map map = new Map(3000,1000);
        map.addNodes(nodes);
        map.addPath(path, Color.RED);
        map.show();
    }


    public static void showOnMap(GPSData ...gpsData){
        Map map = new Map(3000,1000);
        map.addNodes(gpsData);
        map.show();
    }

    public static void showOnMap(Track t, SimraData sd){
        Map map = new Map(3000,1000);

        for(TrackSegment ts :  t.getSegments()){
            RoadPath path = ts.getTrackPath();
            if(path == null)
                return;
            map.addPath(path, Color.RED);
        }

        map.addPath(new RoadPath(sd.getGPSData()), Color.GREEN);
        map.show();

    }
}
