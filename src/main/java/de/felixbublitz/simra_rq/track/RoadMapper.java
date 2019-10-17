package de.felixbublitz.simra_rq.track;

import de.felixbublitz.simra_rq.quality.DataSegment;
import de.felixbublitz.simra_rq.database.AnomalyData;
import de.felixbublitz.simra_rq.database.RoughnessData;
import de.felixbublitz.simra_rq.simra.SimraData;

import java.util.ArrayList;

public class RoadMapper {

    public static ArrayList<RoughnessData> mapSegments(SimraData simraData, ArrayList<DataSegment> dataSegments, Track track){
        ArrayList<RoughnessData> out = new ArrayList<RoughnessData>();

        for(TrackSegment ts : track.getSegments()){
            DataSegment startSegment = getDataSegment(dataSegments, ts.getStart());
            DataSegment endSegment = getDataSegment(dataSegments, ts.getEnd());

            for(int i=dataSegments.indexOf(startSegment); i<dataSegments.indexOf(endSegment); i++){
                int start = i == dataSegments.indexOf(startSegment) ? ts.getStart() : dataSegments.get(i).getStart();
                int end = i == dataSegments.indexOf(endSegment) ? ts.getEnd() : dataSegments.get(i).getEnd();
                out.add(new RoughnessData(ts.getRoad(), simraData.getRecordingDate(), ts.getRoad().getPosition(simraData.getGPSData(start,true)), ts.getRoad().getPosition(simraData.getGPSData(end, true)), dataSegments.get(i).getVariance()));
            }
        }

        return out;
    }

    public static ArrayList<AnomalyData>mapAnomalys(SimraData simraData, ArrayList<Integer> anomalyData, Track track){
        ArrayList<AnomalyData> out = new ArrayList<AnomalyData>();
        for(Integer anomaly : anomalyData){
            if(track.getSegment(anomaly) != null)
                out.add(new AnomalyData(track.getSegment(anomaly).getRoad(), simraData.getRecordingDate(), track.getSegment(anomaly).getRoad().getPosition(simraData.getGPSData(anomaly, true))));
        }

        return out;
    }


    private static DataSegment getDataSegment(ArrayList<DataSegment> dataSegments, int x){
        for(DataSegment ds : dataSegments){
            if(ds.getStart() <= x && ds.getEnd() >= x)
                return ds;
        }
        return null;
    }

}
