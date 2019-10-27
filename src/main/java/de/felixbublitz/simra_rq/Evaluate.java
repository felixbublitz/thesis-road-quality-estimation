package de.felixbublitz.simra_rq;
import de.felixbublitz.simra_rq.changepoint.implementation.PELT;
import de.felixbublitz.simra_rq.database.data.AnomalyData;
import de.felixbublitz.simra_rq.database.data.RoughnessData;
import de.felixbublitz.simra_rq.quality.anomaly.AnomalyDetection;
import de.felixbublitz.simra_rq.quality.segments.DataSegment;
import de.felixbublitz.simra_rq.quality.filter.Filter;
import de.felixbublitz.simra_rq.quality.segments.SegmentDetection;
import de.felixbublitz.simra_rq.simra.SimraData;
import de.felixbublitz.simra_rq.database.Database;
import de.felixbublitz.simra_rq.track.RoadMapper;
import de.felixbublitz.simra_rq.track.Track;
import java.util.ArrayList;

/**
 * Analyse SimraData, produce RoughnessData and Anomaly Data and save in DB
 */

public class Evaluate {
    private static Database db;
    private static final int EVALUATE_FILE_COUNT = 200;
    private static final int CP_PENALTY = 5;
    private static final int FILTER_FREQUENCY = 3; //Hz
    private static final int ANOMALY_THRESHOLD = 25;
    private static final int ANOMALY_ELIMINATE_TIME = 5; //sec

    public static void main(String[] args) {
        db = new Database(Options.DATABASE_FILE);

        for(int i=1; i< EVALUATE_FILE_COUNT; i++){
            try {
                System.out.println("Evaluate File: " + i);
                evaluateFile(Options.SIMRA_PATH + i + Options.SIMRA_EXTENSION);
            }catch(IllegalArgumentException e){
                System.out.println("Skip File: " + i);
            }
        }
    }

    /**
     * Evaluate given SimRa csv file
     * @param file Path of csv file
     */
    private static void evaluateFile(String file){

        //load data
        SimraData dataset = new SimraData(file);

        if(dataset.getLength() == 0)
            return;

        //detect segments
        ArrayList<Double> magnitudes = dataset.getMagnitudes();
        ArrayList<Double> filteredMagnitudes = Filter.applyHighpass(magnitudes, dataset.getSamplingRate(), FILTER_FREQUENCY);
        ArrayList<DataSegment> dataSegments = SegmentDetection.getSegments(filteredMagnitudes, new PELT(CP_PENALTY, dataset.getSamplingRate()));

        //detect anomalies
        ArrayList<Integer> peaks = AnomalyDetection.getPeaks(filteredMagnitudes, dataSegments, ANOMALY_THRESHOLD, (int)(ANOMALY_ELIMINATE_TIME/dataset.getSamplingRate()));

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
