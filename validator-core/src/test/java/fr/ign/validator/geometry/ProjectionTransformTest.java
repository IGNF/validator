package fr.ign.validator.geometry;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang.StringUtils;
import org.geotools.referencing.CRS;
import org.junit.Assert;
import org.junit.Test;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.WKTReader;

import fr.ign.validator.tools.ResourceHelper;
import fr.ign.validator.tools.TableReader;

/**
 * This test relies on validator-core/src/test/resources/projection/reference_postgis.csv to perform
 * test about coordinate transforms. Basically, it compares postgis and geotools transforms.
 * 
 * @see validator-core/src/test/resources/projection/README.md
 * 
 * @author MBorne
 *
 */
public class ProjectionTransformTest {

	@Test
	public void test2154toCRS84() throws Exception {
		runTestFromTo("EPSG:2154","CRS:84",1.0e-7);
	}
	
	@Test
	public void testCRS84to2154() throws Exception {
		runTestFromTo("CRS:84","EPSG:2154",1.0e-3);
	}
	
	@Test
	public void test32620toCRS84() throws Exception {
		runTestFromTo("EPSG:32620","CRS:84",1.0e-7);
	}
	
	@Test
	public void testCRS84to32620() throws Exception {
		runTestFromTo("CRS:84","EPSG:32620",1.0e-3);
	}
	
	@Test
	public void test2972toCRS84() throws Exception {
		runTestFromTo("EPSG:2972","CRS:84",1.0e-7);
	}
	
	@Test
	public void testCRS84to2972() throws Exception {
		runTestFromTo("CRS:84","EPSG:2972",1.0e-3);
	}

	@Test
	public void test2975toCRS84() throws Exception {
		runTestFromTo("EPSG:2975","CRS:84",1.0e-7);
	}
	
	@Test
	public void testCRS84to2975() throws Exception {
		runTestFromTo("CRS:84","EPSG:2975",1.0e-3);
	}

	@Test
	public void test4471toCRS84() throws Exception {
		runTestFromTo("EPSG:4471","CRS:84",1.0e-7);
	}
	
	@Test
	public void testCRS84to4471() throws Exception {
		runTestFromTo("CRS:84","EPSG:4471",1.0e-3);
	}

	
	/**
	 * 
	 * @param sourceSRID
	 * @param targetSRID
	 * @param tolerance
	 * @throws Exception
	 */
	private void runTestFromTo(String sourceSRID, String targetSRID, double tolerance) throws Exception {
		CoordinateReferenceSystem sourceCRS = CRS.decode(sourceSRID);
		CoordinateReferenceSystem targetCRS = CRS.decode(targetSRID);
		ProjectionTransform transformProjection = new ProjectionTransform(sourceCRS, targetCRS);
		
		File reference = ResourceHelper.getResourceFile(getClass(),"/projection/reference_postgis.csv");
		TableReader reader = TableReader.createTableReader(reference, StandardCharsets.UTF_8);
		int indexSource = reader.findColumn(sourceSRID);
		int indexTarget = reader.findColumn(targetSRID);
		Assert.assertTrue(indexSource >= 0 );
		Assert.assertTrue(indexTarget >= 0 );

		WKTReader wktReader = new WKTReader();
		while ( reader.hasNext() ){
			String[] row = reader.next();
			String sourceWKT = row[indexSource];
			String expectedWKT = row[indexTarget];
			if ( StringUtils.isEmpty(sourceWKT) || StringUtils.isEmpty(expectedWKT) ){
				continue;
			}

			Geometry source = wktReader.read(sourceWKT);
			Geometry expectedTarget = wktReader.read(expectedWKT);
			
			Geometry target = transformProjection.transform(source);
			double distance = target.distance(expectedTarget);
			Assert.assertTrue(
				"EPSG:"+sourceSRID+" to EPSG:"+targetSRID+" : distance : "+distance+" greater than "+tolerance,
				distance <= tolerance
			);
		}
	}
	
	
}
