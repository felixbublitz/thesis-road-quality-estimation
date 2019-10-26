package de.felixbublitz.simra_rq.changepoint;

import de.felixbublitz.simra_rq.quality.DataSegment;
import de.felixbublitz.simra_rq.simra.SimraData;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ChangepointAlgorithmTest {

    @org.junit.jupiter.api.Test
    void getCosts() throws InterruptedException {
        SimraData dataset = new SimraData("/home/felix/Documents/SimRa/rides/ride-3.csv");

        OptimalPartitioning o = new OptimalPartitioning(3.8, dataset.getSamplingRate());
        o.data = dataset.getMagnitudes();

        int offset = 0;

        System.out.println(o.getCosts(5 - offset,30- offset));
        System.out.println(o.getCosts(40- offset,80- offset));
        System.out.println(o.getCosts(85 - offset,135- offset));
        System.out.println(o.getCosts(140 - offset,160- offset));

        System.out.println(o.getCosts(80 - offset,90- offset));
        System.out.println(o.getCosts(30 - offset,40- offset));

        debugSegments(dataset.getMagnitudes(), new ArrayList<>());

        TimeUnit.SECONDS.sleep(100);



    }

    private static void debugSegments(ArrayList<Double> filteredMagnitudes, ArrayList<DataSegment> segments){
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
            chart.addSeries("var "+i+": " + Math.round(ds.getVariance())  , new double[]{ds.getStart(), ds.getStart()}, new double[]{-20,20});
        }

        new SwingWrapper(chart).displayChart();

    }
}