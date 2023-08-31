package fr.ign.validator.info.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fr.ign.validator.io.json.EnvelopeSerializer;

import java.util.HashMap;
import java.util.Map;

import org.locationtech.jts.geom.Envelope;

/**
 * Represents a non-geographical file (pdf) in directory
 * 
 * @author CBouche
 *
 */
public class DocumentFileInfo {
    /**
     * file type
     */
    private String type;
    /**
     * FileModel name
     */
    private String modelName;
    /**
     * file name
     */
    private String name;
    /**
     * path (relative to document)
     */
    private String path;
    /**
     * Bounding box (only for tables)
     */
    private Envelope boundingBox = null;
    /**
     * Geometry(only for tables)
     */
    private String unionWKT = null;
    /**
     * Feature count (only for tables)
     */
    private Integer totalFeatures = null;
    /**
     * Stats about sub tables
     */
    private Map<String, TableStats> tables = new HashMap<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean hasExtent() {
        return boundingBox != null;
    }

    @JsonSerialize(using = EnvelopeSerializer.class)
    @JsonInclude(value = Include.NON_NULL)
    public Envelope getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(Envelope boundingBox) {
        this.boundingBox = boundingBox;
    }

    public String getUnionWKT() {
        return unionWKT;
    }

    public void setUnionWKT(String unionWKT) {
        this.unionWKT = unionWKT;
    }

    @JsonInclude(value = Include.NON_NULL)
    public Integer getTotalFeatures() {
        return totalFeatures;
    }

    public void setTotalFeatures(Integer totalFeatures) {
        this.totalFeatures = totalFeatures;
    }

    @JsonInclude(value = Include.NON_EMPTY)
    public Map<String, TableStats> getTables() {
        return tables;
    }

    public void setTables(Map<String, TableStats> tables) {
        this.tables = tables;
    }

}
