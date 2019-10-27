package de.felixbublitz.simra_rq.database.data;

import de.felixbublitz.simra_rq.track.road.Road;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a Roughness Record in Database
 */

public class RoughnessData  {
    private Road road;
    private Date recorded;
    private double variance;
    private int start;
    private int end;

    /**
     * Creates a new RoughnessData object by given information
     * @param road road
     * @param recorded recording date
     * @param start start road position
     * @param end end road position
     * @param variance variance of segment between start and end on road
     */
    public RoughnessData(Road road, Date recorded, int start, int end, double variance){
        this.road = road;
        this.recorded = recorded;
        this.start = start;
        this.end = end;
        this.variance = variance;
    }

    /**
     * Get road where data was recorded
     * @return road
     */

    public Road getRoad() {
        return road;
    }

    /**
     * Get anomaly recording date
     * @return recording data
     */

    public Date getRecordingDate() {
        return recorded;
    }

    /**
     * Get formated anomaly recording date
     * @param format string format of output
     * @return formated string
     */

    public String getRecordingDate(String format){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(recorded);
    }

    /**
     * Returns variance of segment
     * @return variance
     */
    public double getVariance() {
        return variance;
    }

    /**
     * get segment start road position
     * @return road position
     */
    public int getStart() {
        return start;
    }

    /**
     * get segment end road position
     * @return road position
     */
    public int getEnd() {
        return end;
    }

}
