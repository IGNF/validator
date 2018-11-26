package fr.ign.validator.dgpr.database.model;

public class IsoHauteur {

	private String id;

	private String idSurfaceInondable;

	private String htMin;

	private String htMax;


	public IsoHauteur() {
	}

	public IsoHauteur(String id, String idSurface, String htMin, String htMax) {
		this.id = id;
		this.idSurfaceInondable = idSurface;
		this.htMin = htMin;
		this.htMax = htMax;
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


}
