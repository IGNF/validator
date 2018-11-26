package fr.ign.validator.dgpr.database.model;

public class SurfaceInondable {

	private String id;

	private String scenario;

	private String wkt;


	public SurfaceInondable() {
	}


	public SurfaceInondable(String id, String scenario, String wkt) {
		this.id = id;
		this.scenario = scenario;
		this.wkt = wkt;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getScenario() {
		return scenario;
	}


	public void setScenario(String scenario) {
		this.scenario = scenario;
	}


	public String getWkt() {
		return wkt;
	}


	public void setWkt(String wkt) {
		this.wkt = wkt;
	}

}
