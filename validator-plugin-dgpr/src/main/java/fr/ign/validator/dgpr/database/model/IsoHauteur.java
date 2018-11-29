package fr.ign.validator.dgpr.database.model;

public class IsoHauteur {

	private String id;

	private String idSurfaceInondable;

	private String htMin;

	private String htMax;
	
	private String wkt;


	public IsoHauteur() {
	}

	public IsoHauteur(String id, String idSurface, String htMin, String htMax, String wkt) {
		this.id = id;
		this.idSurfaceInondable = idSurface;
		this.htMin = htMin;
		this.htMax = htMax;
		this.wkt = wkt;
	}

	public IsoHauteur(String id, String idSurface, String wkt) {
		this.id = id;
		this.idSurfaceInondable = idSurface;
		this.wkt = wkt;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIdSurfaceInondable() {
		return idSurfaceInondable;
	}

	public void setIdSurfaceInondable(String idSurfaceInondable) {
		this.idSurfaceInondable = idSurfaceInondable;
	}

	public String getHtMin() {
		return htMin;
	}

	public void setHtMin(String htMin) {
		this.htMin = htMin;
	}

	public String getHtMax() {
		return htMax;
	}

	public void setHtMax(String htMax) {
		this.htMax = htMax;
	}
	
	public String getWkt() {
		return wkt;
	}

	public void setWkt(String wkt) {
		this.wkt = wkt;
	}
	
	@Override
	public String toString() {
		return "[" + this.htMin + ", " + this.htMax + "]";
	}
	
	public String idToString() {
		return "'" + this.id + "'";
	}

}
