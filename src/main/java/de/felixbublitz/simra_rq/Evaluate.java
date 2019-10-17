package de.felixbublitz.simra_rq;

import de.felixbublitz.simra_rq.changepoint.BinarySegmentation;
import de.felixbublitz.simra_rq.database.AnomalyData;
import de.felixbublitz.simra_rq.database.RoughnessData;
import de.felixbublitz.simra_rq.quality.AnomalyDetection;
import de.felixbublitz.simra_rq.quality.DataSegment;
import de.felixbublitz.simra_rq.quality.Filter;
import de.felixbublitz.simra_rq.quality.SegmentDetection;
import de.felixbublitz.simra_rq.simra.GPSData;
import de.felixbublitz.simra_rq.simra.SimraData;
import de.felixbublitz.simra_rq.database.Database;
import de.felixbublitz.simra_rq.track.Road;
import de.felixbublitz.simra_rq.track.RoadMapper;
import de.felixbublitz.simra_rq.track.Track;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import java.util.ArrayList;
import java.util.stream.Stream;


public class Evaluate {

    public static void main(String[] args) {
        //load data
        SimraData dataset = new SimraData("/home/felix/Documents/SimRa/rides/ride17.csv");
        Database db = new Database("database.db");

/*
        Road test = new Road(db, "Ritterstraße", "Kreuzberg");


        GPSData anfang = new GPSData(52.487140, 13.427920);
        GPSData ende = new GPSData(52.493402, 13.428149);

        test.getPosition(anfang);
        test.getPosition(ende);


*/



        //detect segments
        ArrayList<Double> magnitudes = dataset.getMagnitudes();
        ArrayList<Double> filteredMagnitudes = Filter.applyHighpass(magnitudes, dataset.getSamplingRate(), 3);
        ArrayList<DataSegment> dataSegments = SegmentDetection.getSegments(filteredMagnitudes, new BinarySegmentation(14));

        //detect anomalies
        ArrayList<Integer> peaks = AnomalyDetection.getPeaks(filteredMagnitudes, dataSegments, 25, (int)(5/dataset.getSamplingRate()));
        //ArrayList<Integer> evasions = anomalyDetection.getEvasions(dataset.getDirectedAccelerometerData(SimraData.Axis.Y));

        //determine track
        Track track = new Track(dataset.getGPSData(), dataset.getSamplingRate(), db);

        //map data to track
        ArrayList<RoughnessData> roughnessData = RoadMapper.mapSegments(dataset, dataSegments, track);
        ArrayList<AnomalyData> anomalyData1 = RoadMapper.mapAnomalys(dataset, peaks, track);
        //ArrayList<anomalyData> anomalyData2 = RoadMapper.mapanomalys(dataset, evasions, track);

        //save in database
        db.insert(roughnessData);
        db.insert(anomalyData1);
        //db.insert(anomalyData2);
    }

    private static void debug(ArrayList<Double> filteredMagnitudes){
        double[] xData = new double[filteredMagnitudes.size()];
        for( int i = 0; i < filteredMagnitudes.size(); i++ )
            xData[i] = i+1;

        Double[] yData = filteredMagnitudes.toArray(new Double[filteredMagnitudes.size()]);

        XYChart chart = QuickChart.getChart("Sample Chart", "X", "Y", "y(x)", xData, Stream.of(yData).mapToDouble(Double::doubleValue).toArray());
        new SwingWrapper(chart).displayChart();
    }


}
