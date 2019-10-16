package de.felixbublitz.simra_rq;


import de.felixbublitz.simra_rq.changepoint.BinarySegmentation;
import de.felixbublitz.simra_rq.changepoint.PELT;
import de.felixbublitz.simra_rq.data.*;
import de.felixbublitz.simra_rq.data.db.AnomolieData;
import de.felixbublitz.simra_rq.data.db.RoughnessData;
import de.felixbublitz.simra_rq.data.simra.SimraData;
import de.felixbublitz.simra_rq.data.track.Road;
import de.felixbublitz.simra_rq.data.track.Track;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import java.util.ArrayList;
import java.util.stream.Stream;


public class Evaluate {



    public static void main(String[] args) {
        //load data
        SimraData dataset = new SimraData("/home/felix/Documents/SimRa/rides/ride4.csv");
        Database db = new Database();

        //detect segments
        ArrayList<Double> magnitudes = dataset.getMagnitudes();
        ArrayList<Double> filteredMagnitudes = Filter.applyHighpass(magnitudes, dataset.getSamplingRate(), 2);

        ArrayList<DataSegment> dataSegments = SegmentDetection.getSegments(filteredMagnitudes, new BinarySegmentation(5));

        //detect anomalies
        ArrayList<Integer> peaks = AnomolieDetection.getPeaks(magnitudes, dataSegments, 1, 10);
        //ArrayList<Integer> evasions = AnomolieDetection.getEvasions(dataset.getDirectedAccelerometerData(SimraData.Axis.Y));




        //determine track
        Track track = new Track(dataset.getGPSData());
        Road r = new Road("Friedelstraße", "Neukölln");

        //map data to track
        ArrayList<RoughnessData> roughnessData = RoadMapper.mapSegments(dataSegments, track);
        ArrayList<AnomolieData> anomolieData1 = RoadMapper.mapAnomolies(peaks, track);
        //ArrayList<AnomolieData> anomolieData2 = RoadMapper.mapAnomolies(evasions, track);

        //save in database
        db.insert(roughnessData);
        db.insert(anomolieData1);
        //db.insert(anomolieData2);
    }


    private void debug(ArrayList<Double> filteredMagnitudes){
        double[] xData = new double[filteredMagnitudes.size()];
        for( int i = 0; i < filteredMagnitudes.size(); i++ )
            xData[i] = i+1;

        Double[] yData = filteredMagnitudes.toArray(new Double[filteredMagnitudes.size()]);

        XYChart chart = QuickChart.getChart("Sample Chart", "X", "Y", "y(x)", xData, Stream.of(yData).mapToDouble(Double::doubleValue).toArray());
        new SwingWrapper(chart).displayChart();
    }


}
