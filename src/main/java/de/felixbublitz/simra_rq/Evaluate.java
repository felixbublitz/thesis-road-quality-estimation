package de.felixbublitz.simra_rq;

import de.felixbublitz.simra_rq.changepoint.*;
import de.felixbublitz.simra_rq.database.AnomalyData;
import de.felixbublitz.simra_rq.database.RoughnessData;
import de.felixbublitz.simra_rq.quality.AnomalyDetection;
import de.felixbublitz.simra_rq.quality.DataSegment;
import de.felixbublitz.simra_rq.quality.Filter;
import de.felixbublitz.simra_rq.quality.SegmentDetection;
import de.felixbublitz.simra_rq.simra.GPSData;
import de.felixbublitz.simra_rq.simra.SimraData;
import de.felixbublitz.simra_rq.database.Database;
import de.felixbublitz.simra_rq.track.RoadMapper;
import de.felixbublitz.simra_rq.track.Track;
import de.felixbublitz.simra_rq.track.TrackSegment;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.viewer.*;
import org.knowm.xchart.*;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.Styler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;


public class Evaluate {
    private static Database db;

    private static void evaluateFile(String file){
        //load data
        SimraData dataset = new SimraData(file);



        //detect segments
        ArrayList<Double> magnitudes = dataset.getMagnitudes();
        ArrayList<Double> filteredMagnitudes = Filter.applyHighpass(magnitudes, dataset.getSamplingRate(), 3);

        ArrayList<DataSegment> dataSegments = SegmentDetection.getSegments(filteredMagnitudes, new BinarySegmentation(1));

        DebugHelper.showSegments(filteredMagnitudes, dataSegments);

        //detect anomalies
        ArrayList<Integer> peaks = AnomalyDetection.getPeaks(filteredMagnitudes, dataSegments, 25, (int)(5/dataset.getSamplingRate()));
        //ArrayList<Integer> evasions = anomalyDetection.getEvasions(dataset.getDirectedAccelerometerData(SimraData.Axis.Y));

        //determine track
        Track track = new Track(dataset, db);

        for(TrackSegment t : track.getSegments()){
            DebugHelper.showOnMap(t.getRoad(), dataset.getGPSData(t.getStart(), true), dataset.getGPSData(t.getEnd(), true), t.getRoad().getGPSPoint(t.getStartPosition()), t.getRoad().getGPSPoint(t.getEndPosition()));
        }

        DebugHelper.showOnMap(track, dataset);

        //map data to track
        ArrayList<RoughnessData> roughnessData = RoadMapper.mapSegments(dataset, dataSegments, track);
        ArrayList<AnomalyData> anomalyData1 = RoadMapper.mapAnomalys(dataset, peaks, track);
        //ArrayList<anomalyData> anomalyData2 = RoadMapper.mapanomalys(dataset, evasions, track);

        //save in database
        db.insert(roughnessData);
        db.insert(anomalyData1);
        //db.insert(anomalyData2);
    }

    public static void main(String[] args) {
        db = new Database("database.db");

        evaluateFile("/home/felix/Documents/SimRa/rides/ride" + 14 + ".csv");
        /*
        for(int i=2; i< 200; i++){
            try {
                evaluateFile("/home/felix/Documents/SimRa/rides/ride" + i + ".csv");
            }catch(IllegalArgumentException e){
                System.out.println("skip");
            }
        }*/
    }




}
