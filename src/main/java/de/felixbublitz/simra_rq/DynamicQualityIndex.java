package de.felixbublitz.simra_rq;

import de.felixbublitz.simra_rq.database.Database;
import javafx.util.Pair;

public class DynamicQualityIndex implements QualityIndex {

    private double minVariance;
    private double maxVariance;

    public DynamicQualityIndex(Database db){
        Pair result =  db.getQualityIndex("dynamic");
        minVariance = (double)result.getKey();
        maxVariance = (double)result.getValue();
    }

    @Override
    public double getQuality(double value) {
        double score = Math.min(1.0, Math.max(0.0, (double)(maxVariance- value )/(maxVariance-minVariance)));
        return Math.min(1.0, Math.max(0.0, (double)(maxVariance- value )/(maxVariance-minVariance)));
    }
}
