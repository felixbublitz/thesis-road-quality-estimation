package de.felixbublitz.simra_rq.track;

import de.felixbublitz.simra_rq.quality.segments.DataSegment;
import de.felixbublitz.simra_rq.database.data.AnomalyData;
import de.felixbublitz.simra_rq.database.data.RoughnessData;
import de.felixbublitz.simra_rq.simra.SimraData;

import java.util.ArrayList;

/**
 * Creates DB ready objects from track and data partition data.
 */

public class RoadMapper {

    private final static int MIN_DIST = 20;

    /**
     * Returns list of db ready roughness data
     * @param simraData simradata object
     * @param dataSegments segmentation of data
     * @param track track
     * @return list of db ready roughness data
     */
    public static ArrayList<RoughnessData> mapSegments(SimraData simraData, ArrayList<DataSegment> dataSegments, Track track){
        ArrayList<RoughnessData> out = new ArrayList<RoughnessData>();

        for(TrackSegment ts : track.getSegments()){
            DataSegment startSegment = getDataSegment(dataSegments, ts.getStart());
            DataSegment endSegment = getDataSegment(dataSegments, ts.getEnd());

            for(int i=dataSegments.indexOf(startSegment); i<=dataSegments.indexOf(endSegment); i++){
                int start = i == dataSegments.indexOf(startSegment) ? ts.getStart() : dataSegments.get(i).getStart();
                int end = i == dataSegments.indexOf(endSegment) ? ts.getEnd() : dataSegments.get(i).getEnd();
                int posStart = ts.getRoad().getRoadGeometry().getPosition(simraData.getGPSData(start,true));
                int posEnd = ts.getRoad().getRoadGeometry().getPosition(simraData.getGPSData(end, true));

                if(posEnd-posStart > MIN_DIST)
                    out.add(new RoughnessData(ts.getRoad(), simraData.getRecordingDate(), posStart , posEnd, dataSegments.get(i).getVariance()));
            }
        }

        return out;
    }

    /**
     * Returns list of db ready roughness data
     * @param simraData simradata object
     * @param anomalyData anomolies of data
     * @param track track
     * @return list of db ready roughness data
     */
    public static ArrayList<AnomalyData>mapAnomalys(SimraData simraData, ArrayList<Integer> anomalyData, Track track){
        ArrayList<AnomalyData> out = new ArrayList<AnomalyData>();
        for(Integer anomaly : anomalyData){
            if(track.getSegment(anomaly) != null)
                out.add(new AnomalyData(track.getSegment(anomaly).getRoad(), simraData.getRecordingDate(), track.getSegment(anomaly).getRoad().getRoadGeometry().getPosition(simraData.getGPSData(anomaly, true))));
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
