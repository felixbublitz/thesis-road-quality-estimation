package de.felixbublitz.simra_rq.quality_index;

/**
 * Interface of a quality index
 */
public interface QualityIndex {

    /**
     * Get quality score between [0,1] based an quality index
     * @param value value to be scored
     * @return score
     */
    public Double getQuality(Double value);


}
