package fr.ign.validator.model;

import java.net.URL;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Static Table
 *
 * @author cbouche
 *
 */
public class StaticTable {

    /**
     * Table name in database
     */
    private String name;

    /**
     * Table name for report
     */
    private String title;

    /**
     * Data reference described in document model
     */
    private String dataReference;

    /**
     * URL to access data
     */
    private URL data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("data")
    @JsonInclude(value = Include.NON_NULL)
    public String getDataReference() {
        return dataReference;
    }

    public void setDataReference(String dataReference) {
        this.dataReference = dataReference;
    }

    @JsonIgnore
    public URL getData() {
        return data;
    }

    public void setData(URL data) {
        this.data = data;
    }

}
