package fr.ign.validator.info.model;

import org.locationtech.jts.geom.Envelope;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fr.ign.validator.io.json.EnvelopeSerializer;

/**
 * Statistics about a table.
 * 
 * @author MBorne
 *
 */
public class TableStats {
    /**
     * bounding box of the table.
     */
    private Envelope boundingBox = new Envelope();
    /**
     * total number of features.
     */
    private int totalFeatures = 0;

    @JsonSerialize(using = EnvelopeSerializer.class)
    @JsonInclude(value = Include.NON_NULL)
    public Envelope getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(Envelope boundingBox) {
        this.boundingBox = boundingBox;
    }

    public int getTotalFeatures() {
        return totalFeatures;
    }

    public void setTotalFeatures(int totalFeatures) {
        this.totalFeatures = totalFeatures;
    }

    public void incrementTotalFeatures() {
        this.totalFeatures++;
    }

}
