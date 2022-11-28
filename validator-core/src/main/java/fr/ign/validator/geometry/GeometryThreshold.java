package fr.ign.validator.geometry;

/**
 * Defines treshold to determine geometry complexity - maximum point allowed -
 * maximum ring (holes) allowed - maximum parts (multigeometry) allowed -
 * maximum density (number of point by meters used to describe the geometry) -
 * and maximum point of any ring (used in density control)
 * 
 * @author cbouche
 *
 */
public class GeometryThreshold {

    private int pointCount;
    private int partCount;
    private int ringCount;
    private double density;
    private int ringPointCount;

    public GeometryThreshold() {
    }

    public GeometryThreshold(int pointCount, int partCount, int ringCount, double density, int ringPointCount) {
        this.pointCount = pointCount;
        this.partCount = partCount;
        this.ringCount = ringCount;
        this.density = density;
        this.ringPointCount = ringPointCount;
    }

    public int getPointCount() {
        return pointCount;
    }

    public void setPointCount(int pointCount) {
        this.pointCount = pointCount;
    }

    public int getPartCount() {
        return partCount;
    }

    public void setPartCount(int partCount) {
        this.partCount = partCount;
    }

    public int getRingCount() {
        return ringCount;
    }

    public void setRingCount(int ringCount) {
        this.ringCount = ringCount;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    public int getRingPointCount() {
        return ringPointCount;
    }

    public void setRingPointCount(int ringPointCount) {
        this.ringPointCount = ringPointCount;
    }

}
