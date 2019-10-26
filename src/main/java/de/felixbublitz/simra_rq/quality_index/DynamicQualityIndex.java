package de.felixbublitz.simra_rq.quality_index;

import de.felixbublitz.simra_rq.database.Database;
import de.felixbublitz.simra_rq.quality_index.QualityIndex;
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
    public Double getQuality(Double value) {
        if(value == null)
            return null;
        double score = Math.min(1.0, Math.max(0.0, (double)(maxVariance- value )/(maxVariance-minVariance)));
        return Math.min(1.0, Math.max(0.0, (double)(maxVariance- value )/(maxVariance-minVariance)));
    }
}
