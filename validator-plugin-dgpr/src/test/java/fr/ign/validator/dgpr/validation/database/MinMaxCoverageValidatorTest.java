package fr.ign.validator.dgpr.validation.database;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import fr.ign.validator.dgpr.database.model.IsoHauteur;
import fr.ign.validator.dgpr.database.model.SurfaceInondable;

public class MinMaxCoverageValidatorTest {

	@Test
	public void sucess1Coveragetest() {
		MinMaxCoverageValidator coverageValidator = new MinMaxCoverageValidator();

		// no need to give a valid wkt, only attribute validation
		SurfaceInondable surfaceInondable = new SurfaceInondable("id", "wkt");
		//
		List<IsoHauteur> hauteurs = new ArrayList<IsoHauteur>();
		hauteurs.add(new IsoHauteur("iso2", "id", "0.5", "1"));
		hauteurs.add(new IsoHauteur("iso3", "id", "1.0", "2.0"));
		hauteurs.add(new IsoHauteur("iso4", "id", "1.0", "2.0"));
		hauteurs.add(new IsoHauteur("iso5", "id", "2.0", null));

		boolean noError = coverageValidator.evaluate(surfaceInondable, hauteurs);
		Assert.assertTrue(noError);
	}

	@Test
	public void sucess2Coveragetest() {
		MinMaxCoverageValidator coverageValidator = new MinMaxCoverageValidator();

		// no need to give a valid wkt, only attribute validation
		SurfaceInondable surfaceInondable = new SurfaceInondable("id", "wkt");
		//
		List<IsoHauteur> hauteurs = new ArrayList<IsoHauteur>();
		hauteurs.add(new IsoHauteur("iso1", "id", "0.0", "0.5"));
		hauteurs.add(new IsoHauteur("iso2", "id", "0.0", "0.5"));
		hauteurs.add(new IsoHauteur("iso3", "id", "0.0", "0.5"));
		hauteurs.add(new IsoHauteur("iso4", "id", "0.0", "0.5"));
		hauteurs.add(new IsoHauteur("iso5", "id", "0.5", "1"));
		hauteurs.add(new IsoHauteur("iso6", "id", "0.5", "1"));
		hauteurs.add(new IsoHauteur("iso7", "id", "0.5", "1"));
		hauteurs.add(new IsoHauteur("iso8", "id", "1.0", "2.0"));
		hauteurs.add(new IsoHauteur("iso9", "id", "1.0", "2.0"));

		boolean noError = coverageValidator.evaluate(surfaceInondable, hauteurs);
		Assert.assertTrue(noError);
	}

	@Test
	public void error1Coveragetest() {
		MinMaxCoverageValidator coverageValidator = new MinMaxCoverageValidator();

		// no need to give a valid wkt, only attribute validation
		SurfaceInondable surfaceInondable = new SurfaceInondable("id", "wkt");
		//
		List<IsoHauteur> hauteurs = new ArrayList<IsoHauteur>();
		hauteurs.add(new IsoHauteur("iso1", "id", "0.0", "0.5"));
		hauteurs.add(new IsoHauteur("iso2", "id", "0.5", "1"));
		// htmax < htmin
		hauteurs.add(new IsoHauteur("iso3", "id", "1.0", "0.5"));
		hauteurs.add(new IsoHauteur("iso4", "id", "1.0", "2.0"));
		hauteurs.add(new IsoHauteur("iso5", "id", "2.0", null));

		boolean noError = coverageValidator.evaluate(surfaceInondable, hauteurs);
		Assert.assertFalse(noError);
	}

	@Test
	public void error2Coveragetest() {
		MinMaxCoverageValidator coverageValidator = new MinMaxCoverageValidator();

		// no need to give a valid wkt, only attribute validation
		SurfaceInondable surfaceInondable = new SurfaceInondable("id", "wkt");
		//
		List<IsoHauteur> hauteurs = new ArrayList<IsoHauteur>();
		hauteurs.add(new IsoHauteur("iso1", "id", "0.0", "0.5"));
		hauteurs.add(new IsoHauteur("iso2", "id", "0.5", "1"));
		// htmin == htmax
		hauteurs.add(new IsoHauteur("iso3", "id", "1.0", "1"));
		hauteurs.add(new IsoHauteur("iso4", "id", "1.0", "2.0"));
		hauteurs.add(new IsoHauteur("iso5", "id", "2.0", null));

		boolean noError = coverageValidator.evaluate(surfaceInondable, hauteurs);
		Assert.assertFalse(noError);
	}

	@Test
	public void error3Coveragetest() {
		MinMaxCoverageValidator coverageValidator = new MinMaxCoverageValidator();

		// no need to give a valid wkt, only attribute validation
		SurfaceInondable surfaceInondable = new SurfaceInondable("id", "wkt");
		//
		List<IsoHauteur> hauteurs = new ArrayList<IsoHauteur>();
		hauteurs.add(new IsoHauteur("iso2", "id", "0.5", "1"));
		// htmin == htmax == null
		hauteurs.add(new IsoHauteur("iso3", "id", null, null));
		hauteurs.add(new IsoHauteur("iso4", "id", "1.0", "2.0"));
		hauteurs.add(new IsoHauteur("iso5", "id", "2.0", null));

		boolean noError = coverageValidator.evaluate(surfaceInondable, hauteurs);
		Assert.assertFalse(noError);
	}

	@Test
	public void error4Coveragetest() {
		MinMaxCoverageValidator coverageValidator = new MinMaxCoverageValidator();

		// no need to give a valid wkt, only attribute validation
		SurfaceInondable surfaceInondable = new SurfaceInondable("id", "wkt");
		//
		List<IsoHauteur> hauteurs = new ArrayList<IsoHauteur>();
		hauteurs.add(new IsoHauteur("iso1", "id", "0.0", "0.5"));
		hauteurs.add(new IsoHauteur("iso2", "id", "0.5", "1"));
		// htmin+1 != htmax
		hauteurs.add(new IsoHauteur("iso3", "id", "2.0", "4.0"));
		hauteurs.add(new IsoHauteur("iso5", "id", "4.0", null));

		boolean noError = coverageValidator.evaluate(surfaceInondable, hauteurs);
		Assert.assertFalse(noError);
	}

	@Test
	public void error5Coveragetest() {
		MinMaxCoverageValidator coverageValidator = new MinMaxCoverageValidator();

		// no need to give a valid wkt, only attribute validation
		SurfaceInondable surfaceInondable = new SurfaceInondable("id", "wkt");
		//
		List<IsoHauteur> hauteurs = new ArrayList<IsoHauteur>();
		hauteurs.add(new IsoHauteur("iso1", "id", "0.0", "0.5"));
		// htmin+1 != htmax
		hauteurs.add(new IsoHauteur("iso2", "id", "0.0", "1"));
		hauteurs.add(new IsoHauteur("iso3", "id", "2.0", "4.0"));
		hauteurs.add(new IsoHauteur("iso5", "id", "4.0", null));

		boolean noError = coverageValidator.evaluate(surfaceInondable, hauteurs);
		Assert.assertFalse(noError);
	}

	@Test
	public void doubleCastErrorTest() {
		MinMaxCoverageValidator coverageValidator = new MinMaxCoverageValidator();

		try {
			coverageValidator.compare("0.5", "0,5");
		} catch (NumberFormatException e) {
			Assert.assertTrue(true);
		}
	}


	@Test
	public void doubleCastNoErrorTest() {
		MinMaxCoverageValidator coverageValidator = new MinMaxCoverageValidator();

		try {
			coverageValidator.compare("0.5", "0.5");
		} catch (NumberFormatException e) {
			Assert.fail();
		}

	}

}
