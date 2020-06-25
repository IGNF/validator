package fr.ign.validator.tools.ogr;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * Wrapper around GDAL version provided by "ogr2ogr --version" with the
 * following format : "GDAL 2.2.2, released 2017/09/15"
 * 
 * @author MBorne
 *
 */
public class OgrVersion {

    private static final String REGEXP_VERSION = "GDAL (\\d+)\\.(\\d+)\\.(\\d+).*";

    /*
     * ogr2ogr --version result
     * 
     * Examples :
     * 
     * GDAL 1.9.1, released 2012/05/15 GDAL 2.2.2, released 2017/09/15 ...
     */
    private String fullVersion;

    private int major;

    private int minor;

    private int patch;

    public OgrVersion(String fullVersion) {
        this.fullVersion = fullVersion;
        validate();
    }

    public String getFullVersion() {
        return this.fullVersion;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    public String toString() {
        return getFullVersion();
    }

    /**
     * Throws exceptions for banned versions
     */
    private void validate() {
        if (StringUtils.isEmpty(this.fullVersion)) {
            throw new OgrNotFoundException();
        }

        /* Validate pattern and parse major, minor and patch */
        Pattern pattern = Pattern.compile("(?i)" + REGEXP_VERSION);
        Matcher matcher = pattern.matcher(fullVersion);

        if (matcher.matches()) {
            this.major = Integer.valueOf(matcher.group(1));
            this.minor = Integer.valueOf(matcher.group(2));
            this.patch = Integer.valueOf(matcher.group(3));
        } else {
            throw new OgrNotFoundException(fullVersion);
        }

        if (fullVersion.contains("1.11.0")) {
            throw new OgrBadVersionException("ogr2ogr 1.11.0 is not supported (bug in WKT limited to 8000 characters)");
        }
    }

}
