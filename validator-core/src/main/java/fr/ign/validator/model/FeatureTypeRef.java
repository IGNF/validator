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

    public boolean isEmpty() {
        return StringUtils.isEmpty(value);
    }
    
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
