package fr.ign.validator.model;

import java.net.URL;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class StaticTable {

    public static final String TYPE = "static_table";

    private String name;

    private String path;
    
    private URL url;

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

    @JsonIgnore
    public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public static String getType() {
		return TYPE;
	}

}
