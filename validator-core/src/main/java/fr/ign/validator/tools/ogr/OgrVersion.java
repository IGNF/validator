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
    /**
     * Regexp to parse ogr2ogr version from "ogr2ogr --version"
     */
    private static final String REGEXP_VERSION = "GDAL (\\d+)\\.(\\d+)\\.(\\d+).*";
    /**
     * Describe supported version for ogr2ogr
     */
    private static final String SUPPORTED_VERSION_MESSAGE = "ogr2ogr greater than 2.3.0 is required";

    /**
     * Result of the command "ogr2ogr --version", for example :
     * 
     * <ul>
     * <li>GDAL 1.9.1, released 2012/05/15</li>
     * <li>GDAL 2.2.2, released 2017/09/15</li>
     * </ul>
     */
    private String fullVersion;

    private int major;

    private int minor;

    private int patch;

    /**
     * Create OgrVersion parsing fullVersion string.
     * 
     * @param fullVersion
     * @throws OgrNotFoundException on failure to parse fullVersion
     */
    public OgrVersion(String fullVersion) throws OgrNotFoundException {
        this.fullVersion = fullVersion;
        parseFullVersion();
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

    /**
     * Indicates if version is supported by IGNF/validator.
     * 
     * @return
     */
    public boolean isSupported() {
        /*
         * Version bellow 2.3.0 are forbidden (MapInfo TAB charset is supported from
         * this version)
         */
        if (major < 2 || (major == 2 && minor < 3)) {
            return false;
        }
        return true;
    }

    /**
     * Throws exception if ogr2ogr is not supported.
     *
     * @throws OgrBadVersionException
     */
    public void ensureVersionIsSupported() throws OgrBadVersionException {
        if (!isSupported()) {
            String message = SUPPORTED_VERSION_MESSAGE + String.format(
                " (version found : %s)",
                fullVersion
            );
            throw new OgrBadVersionException(message);
        }
    }

    /**
     * Get display version
     */
    public String toString() {
        return getFullVersion();
    }

    /**
     * Parse major, minor and patch from fullVersion string.
     * 
     * @throws OgrNotFoundException
     */
    private void parseFullVersion() throws OgrNotFoundException {
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
    }

}
