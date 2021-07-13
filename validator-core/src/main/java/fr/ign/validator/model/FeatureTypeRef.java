package fr.ign.validator.model;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * A reference to a FeatureType (a complete URL or an URL relative to the
 * DocumentModel URL)
 * 
 * @author MBorne
 *
 */
public class FeatureTypeRef {

    /**
     * A special value to declare that the FeatureType is generated according to the
     * data.
     */
    public static final String AUTO = "auto";

    /**
     * A path relative to the document URL or a complete URL.
     */
    private String value;

    public FeatureTypeRef(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * True is the value is null or empty.
     * 
     * @return
     */
    public boolean isEmpty() {
        return StringUtils.isEmpty(value);
    }

    /**
     * True if the value is a complete URL.
     * 
     * @return
     */
    public boolean isURL() {
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        try {
            new URL(value);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }
}
