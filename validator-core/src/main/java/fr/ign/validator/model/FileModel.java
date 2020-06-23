package fr.ign.validator.model;

import java.io.File;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.io.xml.FileModelAdapter;
import fr.ign.validator.model.file.DirectoryModel;
import fr.ign.validator.model.file.MetadataModel;
import fr.ign.validator.model.file.PdfModel;
import fr.ign.validator.model.file.TableModel;

/**
 * Represents a file of a Document
 * 
 * @author MBorne
 */
@XmlJavaTypeAdapter(FileModelAdapter.class)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME, 
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({ 
    @Type(value = DirectoryModel.class, name = DirectoryModel.TYPE), 
    @Type(value = MetadataModel.class, name = MetadataModel.TYPE),
    @Type(value = PdfModel.class, name = PdfModel.TYPE), 
    @Type(value = TableModel.class, name = TableModel.TYPE),
})
public abstract class FileModel implements Model {
    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("FileModel");

    @XmlEnum(String.class)
    public enum MandatoryMode {
        /**
         * Ignore if file is missing
         */
        OPTIONAL,
        /**
         * Report WARNING if file is missing
         */
        WARN,
        /**
         * Report ERROR if file is missing
         */
        ERROR
    }

    /**
     * Name of the file (ex : ZONE_URBA)
     */
    private String name;

    /**
     * Path of the file (regexp without extension, ex :
     * Donnees_geographiques/ZONE_URBA_[0-9a-b]{5})
     * 
     * @since 4.0 previously "regexp"
     */
    private String path;

    /**
     * Mandatory mode
     */
    private MandatoryMode mandatory = MandatoryMode.WARN;

    /**
     * Table model (optional, for tables only)
     */
    private FeatureType featureType = null;

    protected FileModel() {

    }

    /**
     * Get file type
     * 
     * @return
     */
    @JsonIgnore
    public abstract String getType();

    /**
     * Creates a DocumentFile for this FileModel
     * 
     * @return
     */
    abstract public DocumentFile createDocumentFile(File path);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    @XmlElement(name = "regexp")
    public void setPath(String path) {
        this.path = path;
    }

    public MandatoryMode getMandatory() {
        return mandatory;
    }

    public void setMandatory(MandatoryMode mandatory) {
        this.mandatory = mandatory;
    }

    @JsonIgnore
    public FeatureType getFeatureType() {
        return featureType;
    }

    @XmlTransient
    public void setFeatureType(FeatureType featureType) {
        this.featureType = featureType;
    }

    /**
     * Returns the corresponding regexp to : - the supported extensions - the
     * character "/" for folders
     * 
     * @return
     */
    @JsonIgnore
    public String getRegexpSuffix() {
        return "";
    }

    /**
     * Tests if the file matches the regexp
     * 
     * @param file
     * @return
     */
    public boolean matchPath(File file) {
        String uriFile = file.toURI().toString();
        String regexp = getPathRegexp();
        log.trace(MARKER, "matchPath / {} / {} match {} ...", getName(), uriFile, regexp);
        return uriFile.matches(regexp);
    }

    /**
     * Returns a regexp corresponding to the full path (with folder and extension
     * according to file type)
     * 
     * @return
     */
    @JsonIgnore
    public String getPathRegexp() {
        // (?i) : case insensitive
        // .* : starts by any character
        String regexp = "(?i).*/" + getPath() + getRegexpSuffix();
        return regexp;
    }

    /**
     * Tests if filename matches the regexp (in order to detect files in wrong
     * directory)
     * 
     * @param file
     * @return
     */
    public boolean matchFilename(File file) {
        String regexp = getFilenameRegexp();
        log.trace(MARKER, "matchFilename / {} / {} match {} ...", getName(), file, regexp);
        return file.getName().matches(regexp);
    }

    /**
     * Returns a regexp corresponding to the filename (with folder for flat
     * validation and extension according to file type)
     * 
     * @return
     */
    @JsonIgnore
    public String getFilenameRegexp() {
        String parts[] = getPath().split("/");

        String result = "(?i)" + parts[parts.length - 1] + ".*" + getRegexpSuffix();

        // validate regexp (/ may be misplaced)
        try {
            Pattern.compile(result);
            return result;
        } catch (PatternSyntaxException exception) {
            return "(?i)" + getName() + ".*" + getRegexpSuffix();
        }
    }

}
