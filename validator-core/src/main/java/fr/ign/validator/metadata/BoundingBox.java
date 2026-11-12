package fr.ign.validator.metadata;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fr.ign.validator.io.json.BoundingBoxSerializer;

/**
 *
 * Implementation of gmd:EX_GeographicBoundingBox
 *
 * Note that coordinates are stored as string to allow validation reporting
 *
 * @see <a href=
 *      "http://www.datypic.com/sc/niem21/e-gmd_EX_GeographicBoundingBox.html">gmd:EX_GeographicBoundingBox</a>
 *
 * @author MBorne
 *
 */
@JsonSerialize(using = BoundingBoxSerializer.class)
public class BoundingBox {

    private String westBoundLongitude;

    private String eastBoundLongitude;

    private String southBoundLatitude;

    private String northBoundLatitude;

    public String getWestBoundLongitude() {
        return westBoundLongitude;
    }

    public void setWestBoundLongitude(String westBoundLongitude) {
        this.westBoundLongitude = westBoundLongitude;
    }

    public String getEastBoundLongitude() {
        return eastBoundLongitude;
    }

    public void setEastBoundLongitude(String eastBoundLongitude) {
        this.eastBoundLongitude = eastBoundLongitude;
    }

    public String getSouthBoundLatitude() {
        return southBoundLatitude;
    }

    public void setSouthBoundLatitude(String southBoundLatitude) {
        this.southBoundLatitude = southBoundLatitude;
    }

    public String getNorthBoundLatitude() {
        return northBoundLatitude;
    }

    public void setNorthBoundLatitude(String northBoundLatitude) {
        this.northBoundLatitude = northBoundLatitude;
    }

    /**
     * Test if bounding box is valid : coordinates are numbers, longitudes are in
     * [-180,180], latitudes are in [-90,90] and south is not greater than north.
     *
     * @return
     */
    public boolean isValid() {
        double west = safeStringToDouble(westBoundLongitude);
        double east = safeStringToDouble(eastBoundLongitude);
        double south = safeStringToDouble(southBoundLatitude);
        double north = safeStringToDouble(northBoundLatitude);
        if (Double.isNaN(west) || Double.isNaN(east) || Double.isNaN(south) || Double.isNaN(north)) {
            return false;
        }
        return isBetween(west, -180.0, 180.0)
            && isBetween(east, -180.0, 180.0)
            && isBetween(south, -90.0, 90.0)
            && isBetween(north, -90.0, 90.0)
            && south <= north;
    }

    private static boolean isBetween(double value, double min, double max) {
        return value >= min && value <= max;
    }

    @Override
    public String toString() {
        return westBoundLongitude + "," + southBoundLatitude + "," + eastBoundLongitude + "," + northBoundLatitude;
    }

    /**
     * Converts to double array
     * [westBoundLongitude,southBoundLatitude,eastBoundLongitude,northBoundLatitude]
     *
     * @return
     */
    public double[] toArray() {
        return new double[] {
            safeStringToDouble(westBoundLongitude),
            safeStringToDouble(southBoundLatitude),
            safeStringToDouble(eastBoundLongitude),
            safeStringToDouble(northBoundLatitude)
        };
    }

    private double safeStringToDouble(String value) {
        if (value == null) {
            return Double.NaN;
        }
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

}
