package de.felixbublitz.simra_rq.database.data;

import de.felixbublitz.simra_rq.track.road.Road;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Represents an Anomaly Record in Database
 */

public class AnomalyData {

    private Road road;
    private Date recorded;
    private int position;

    /**
     * Creates a new anomaly opbject by given information
     * @param road road where anomaly was detected
     * @param recorded recording date
     * @param position road position
     */

    public AnomalyData(Road road, Date recorded, int position){
        this.road = road;
        this.recorded = recorded;
        this.position = position;
    }

    /**
     * Get Road on which anomaly was detected
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
     * Get road position where anomaly happened
     * @return
     */
    public int getPosition() {
        return position;
    }

}
