package fr.ign.validator.model;

import java.net.URL;

import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class StaticTable {

    public static final String TYPE = "static_table";

    private String name;
    
    private String title;

    private String dataReference;

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

    @XmlTransient
	public void setData(URL data) {
		this.data = data;
	}

	public static String getType() {
        return TYPE;
    }

}
