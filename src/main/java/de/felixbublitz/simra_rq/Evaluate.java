package de.felixbublitz.simra_rq;
import de.felixbublitz.simra_rq.changepoint.*;
import de.felixbublitz.simra_rq.database.AnomalyData;
import de.felixbublitz.simra_rq.database.RoughnessData;
import de.felixbublitz.simra_rq.quality.AnomalyDetection;
import de.felixbublitz.simra_rq.quality.DataSegment;
import de.felixbublitz.simra_rq.quality.Filter;
import de.felixbublitz.simra_rq.quality.SegmentDetection;
import de.felixbublitz.simra_rq.simra.SimraData;
import de.felixbublitz.simra_rq.database.Database;
import de.felixbublitz.simra_rq.track.RoadMapper;
import de.felixbublitz.simra_rq.track.Track;
import java.util.ArrayList;

public class Evaluate {
    private static Database db;
    private static final int EVALUATE_FILE_COUNT = 200;

    public static void main(String[] args) {
        db = new Database("database.db");

        for(int i=2; i< EVALUATE_FILE_COUNT; i++){
            try {
                System.out.println("Evaluate File: " + i);
                evaluateFile("/home/felix/Documents/SimRa/rides/ride" + i + ".csv");
            }catch(IllegalArgumentException e){
                System.out.println("Skip File: " + i);
            }
        }
    }

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

        //determine track
        Track track = new Track(dataset, db);

        //map data to track
        ArrayList<RoughnessData> roughnessData = RoadMapper.mapSegments(dataset, dataSegments, track);
        ArrayList<AnomalyData> anomalyData = RoadMapper.mapAnomalys(dataset, peaks, track);

        //save in database
        db.insert(roughnessData);
        db.insert(anomalyData);
    }
}
