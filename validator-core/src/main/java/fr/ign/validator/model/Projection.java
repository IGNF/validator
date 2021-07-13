package fr.ign.validator.model;

import org.apache.commons.lang.StringUtils;
import org.geotools.referencing.CRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * Provides mapping informations between CRS in geotools and metadata
 * 
 * WARNING : validityDomain will probably be replaced by a lon,lat bounding box
 * extracted from URI (current usage is for GpU)
 * 
 * @author MBorne
 *
 */
public class Projection {

    /**
     * Code for CRS:84 (lon,lat)
     */
    public static final String CODE_CRS84 = "CRS:84";

    /**
     * Code name ("EPSG:2154", "IGNF:RGF93LAMB93", etc.)
     */
    private String code;

    /**
     * Code geotool (null if same as code)
     */
    private String codeGeotool;

    /**
     * Title RGF93 / Lambert-93
     */
    private String title;

    /**
     * URI (http://www.opengis.net/def/crs/EPSG/0/2154,
     * http://registre.ign.fr/ign/IGNF/crs/IGNF/RGF93LAMB93, etc.)
     */
    private String uri;

    /**
     * WKT representing the validity domain for coordinates in the given projection
     * 
     * ex : "POLYGON((60000 6010000,60000 7130000,1270000 7130000,1270000
     * 6010000,60000 6010000))"
     */
    private String validityDomain;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeGeotool() {
        return codeGeotool;
    }

    public void setCodeGeotool(String codeGeotool) {
        this.codeGeotool = codeGeotool;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getValidityDomain() {
        return validityDomain;
    }

    public void setValidityDomain(String validityDomain) {
        this.validityDomain = validityDomain;
    }

    /**
     * Get geotools CRS (null if not found)
     * 
     * @return
     */
    @JsonIgnore
    public CoordinateReferenceSystem getCRS() {
        try {
            return CRS.decode(
                StringUtils.isEmpty(codeGeotool) ? code : codeGeotool
            );
        } catch (Exception e) {
            /*
             * codeGeotool is supposed to be valid as it is declared in projection.json
             */
            throw new RuntimeException(e);
        }
    }

    /**
     * Get SRID code (used in postgis command)
     * 
     * TODO handle coordinate flip for postgis transform (EPSG:4326 is lat,lon for
     * the validator to match official conventions)
     * 
     * @return
     */
    public String getSrid() {
        String code = StringUtils.isEmpty(codeGeotool) ? this.code : codeGeotool;
        // check namespace
        if (code.startsWith("EPSG:")) {
            return code.substring(5);
        }
        return "4326";
    }

    @Override
    public String toString() {
        return code;
    }

}
