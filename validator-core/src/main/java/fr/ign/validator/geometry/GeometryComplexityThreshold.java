package fr.ign.validator.geometry;

public class GeometryComplexityThreshold {

	private boolean testSkiped;

	private int    warnPoinCnt = 50000;
	private int    warnRingCnt = 500;
	private int    warnPartCnt = 500;
	private double warnDensity = 0.1;

	private int    errPoinCnt = 200000;
	private int    errRingCnt = 1000;
	private int    errPartCnt = 1000;
	private double errDensity = 10;

	public GeometryComplexityThreshold() {
		testSkiped = true;
	}

	public GeometryComplexityThreshold(
			int warnPoinCnt, int warnRingCnt, int warnPartCnt, double warnDensity,
			int errPoinCnt, int errRingCnt, int errPartCnt, double errDensity
	) {

		testSkiped = false;

		this.warnPoinCnt = warnPoinCnt;
		this.warnRingCnt = warnRingCnt;
		this.warnPartCnt = warnPartCnt;
		this.warnDensity = warnDensity;

		this.errPoinCnt = errPoinCnt;
		this.errRingCnt = errRingCnt;
		this.errPartCnt = errPartCnt;
		this.errDensity = errDensity;
	}

	public boolean isTestSkiped() {
		return testSkiped;
	}

	public int getWarnPoinCnt() {
		return warnPoinCnt;
	}

	public void setWarnPoinCnt(int warnPoinCnt) {
		this.warnPoinCnt = warnPoinCnt;
	}

	public double getWarnDensity() {
		return warnDensity;
	}

	public void setWarnDensity(double warnDensity) {
		this.warnDensity = warnDensity;
	}

	public int getWarnRingCnt() {
		return warnRingCnt;
	}

	public void setWarnRingCnt(int warnRingCnt) {
		this.warnRingCnt = warnRingCnt;
	}

	public int getWarnPartCnt() {
		return warnPartCnt;
	}

	public void setWarnPartCnt(int warnPartCnt) {
		this.warnPartCnt = warnPartCnt;
	}

	public int getErrPoinCnt() {
		return errPoinCnt;
	}

	public void setErrPoinCnt(int errPoinCnt) {
		this.errPoinCnt = errPoinCnt;
	}

	public double getErrDensity() {
		return errDensity;
	}

	public void setErrDensity(double errDensity) {
		this.errDensity = errDensity;
	}

	public int getErrRingCnt() {
		return errRingCnt;
	}

	public void setErrRingCnt(int errRingCnt) {
		this.errRingCnt = errRingCnt;
	}

	public int getErrPartCnt() {
		return errPartCnt;
	}

	public void setErrPartCnt(int errPartCnt) {
		this.errPartCnt = errPartCnt;
	}

}
