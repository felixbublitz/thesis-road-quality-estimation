package de.felixbublitz.simra_rq;

import de.felixbublitz.simra_rq.changepoint.*;
import de.felixbublitz.simra_rq.database.RoughnessData;
import de.felixbublitz.simra_rq.quality.AnomalyDetection;
import de.felixbublitz.simra_rq.quality.DataSegment;
import de.felixbublitz.simra_rq.quality.Filter;
import de.felixbublitz.simra_rq.quality.SegmentDetection;
import de.felixbublitz.simra_rq.simra.SimraData;
import de.felixbublitz.simra_rq.database.Database;
import de.felixbublitz.simra_rq.track.RoadMapper;
import de.felixbublitz.simra_rq.track.Track;
import de.felixbublitz.simra_rq.track.TrackSegment;
import java.util.ArrayList;


public class Evaluate {
    private static Database db;

    private static void evaluateFile(String file){

        //load data
        SimraData dataset = new SimraData(file);

        //detect segments
        ArrayList<Double> magnitudes = dataset.getMagnitudes();
        ArrayList<Double> filteredMagnitudes = Filter.applyHighpass(magnitudes, dataset.getSamplingRate(), 3);

        ArrayList<DataSegment> dataSegments = SegmentDetection.getSegments(filteredMagnitudes, new PELTv3(5, dataset.getSamplingRate()));

        DebugHelper.showSegments(filteredMagnitudes, dataSegments);

        //detect anomalies
        ArrayList<Integer> peaks = AnomalyDetection.getPeaks(filteredMagnitudes, dataSegments, 25, (int)(5/dataset.getSamplingRate()));
        //ArrayList<Integer> evasions = anomalyDetection.getEvasions(dataset.getDirectedAccelerometerData(SimraData.Axis.Y));

        //determine track
        Track track = new Track(dataset, db);


        for(TrackSegment t : track.getSegments()){
           // DebugHelper.showOnMap(t,dataset);
        }

        //DebugHelper.showOnMap(track, dataset);


        //map data to track
        ArrayList<RoughnessData> roughnessData = RoadMapper.mapSegments(dataset, dataSegments, track);
//        ArrayList<AnomalyData> anomalyData1 = RoadMapper.mapAnomalys(dataset, peaks, track);
        //ArrayList<anomalyData> anomalyData2 = RoadMapper.mapanomalys(dataset, evasions, track);

        //save in database
          db.insert(roughnessData);
   //       db.insert(anomalyData1);
          //db.insert(anomalyData2);
    }

    public static void main(String[] args) {
        db = new Database("database.db");

        evaluateFile("/home/felix/Documents/SimRa/rides/ride" + 2+ ".csv");
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
