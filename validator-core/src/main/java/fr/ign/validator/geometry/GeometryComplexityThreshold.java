package fr.ign.validator.geometry;

/**
 * A couple of warning threshold and an error one - used by geometry complexity
 * validator
 *
 * @author cbouche
 *
 */
public class GeometryComplexityThreshold {

    private GeometryThreshold warningThreshold;

    private GeometryThreshold errorThreshold;

    public GeometryComplexityThreshold(GeometryThreshold warningThreshold, GeometryThreshold errorThreshold) {
        this.warningThreshold = warningThreshold;
        this.errorThreshold = errorThreshold;
    }

    public GeometryThreshold getWarningThreshold() {
        return warningThreshold;
    }

    public void setWarningThreshold(GeometryThreshold warningThreshold) {
        this.warningThreshold = warningThreshold;
    }

    public GeometryThreshold getErrorThreshold() {
        return errorThreshold;
    }

    public void setErrorThreshold(GeometryThreshold errorThreshold) {
        this.errorThreshold = errorThreshold;
    }

}
