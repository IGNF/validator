package fr.ign.validator.geometry;

/**
 * Defines treshold to determine geometry complexity
 * - maximum point allowed
 * - maximum ring (holes) allowed
 * - maximum parts (multigeometry) allowed
 * - maximum density (number of point by meters used to describe the geometry)
 * 
 * All threshold work by couple a warning one or an error one
 * 
 * @author cbouche
 *
 */
public class GeometryComplexityThreshold {

	private int    warningPointCount;
	private int    warningRingCount;
	private int    warningPartCount;
	private double warningDensity;

	private int    errorPointCount;
	private int    errorRingCount;
	private int    errorPartCount;
	private double errorDensity;

	public GeometryComplexityThreshold(
			int warningPointCount, int warningRingCount, int warningPartCount, double warningDensity,
			int errorPointCount, int errorRingCount, int errorPartCount, double errorDensity
	) {

		this.warningPointCount = warningPointCount;
		this.warningRingCount = warningRingCount;
		this.warningPartCount = warningPartCount;
		this.warningDensity = warningDensity;

		this.errorPointCount = errorPointCount;
		this.errorRingCount = errorRingCount;
		this.errorPartCount = errorPartCount;
		this.errorDensity = errorDensity;
	}

	public int getWarningPointCount() {
		return warningPointCount;
	}

	public void setWarningPointCount(int warningPointCount) {
		this.warningPointCount = warningPointCount;
	}

	public int getWarningRingCount() {
		return warningRingCount;
	}

	public void setWarningRingCount(int warningRingCount) {
		this.warningRingCount = warningRingCount;
	}

	public int getWarningPartCount() {
		return warningPartCount;
	}

	public void setWarningPartCount(int warningPartCount) {
		this.warningPartCount = warningPartCount;
	}

	public double getWarningDensity() {
		return warningDensity;
	}

	public void setWarningDensity(double warningDensity) {
		this.warningDensity = warningDensity;
	}

	public int getErrorPointCount() {
		return errorPointCount;
	}

	public void setErrorPointCount(int errorPointCount) {
		this.errorPointCount = errorPointCount;
	}

	public int getErrorRingCount() {
		return errorRingCount;
	}

	public void setErrorRingCount(int errorRingCount) {
		this.errorRingCount = errorRingCount;
	}

	public int getErrorPartCount() {
		return errorPartCount;
	}

	public void setErrorPartCount(int errorPartCount) {
		this.errorPartCount = errorPartCount;
	}

	public double getErrorDensity() {
		return errorDensity;
	}

	public void setErrorDensity(double errorDensity) {
		this.errorDensity = errorDensity;
	}

}
