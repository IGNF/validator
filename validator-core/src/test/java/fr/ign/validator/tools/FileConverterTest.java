package fr.ign.validator.tools;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.tools.ResourceHelper;
import fr.ign.validator.tools.ogr.OgrVersion;

/**
 * Regress test for format conversions
 * @author MBorne
 */
public class FileConverterTest {
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	FileConverter fileConverter;

	@Before
	public void setUp() {
		fileConverter = FileConverter.getInstance();
	}
	
	@Test
	public void testGetVersion(){
		OgrVersion version = fileConverter.getVersion();
		System.out.println(version);
	}

	@Test
	public void testConvertToCSV() throws IOException {
		File source = ResourceHelper.getResourceFile(getClass(),"/data/ZONE_URBA_41003.TAB");
		File target = folder.newFile("ZONE_URBA.csv");
		try {
			fileConverter.convertToCSV(source, target);
			Assert.assertTrue(target.exists());
			List<String> lines = FileUtils.readLines(target);
			Assert.assertEquals(37, lines.size());
			Assert.assertEquals("WKT,LIBELLE,LIBELONG,TYPEZONE,DESTDOMI,NOMFIC,URLFIC,INSEE,DATAPPRO,DATVALID",
					lines.get(0));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	/**
	 * Regress test about GDAL coordinate precision while converting to CSV
	 * 
	 * @see validator-core/src/test/resources/data/POINT.README.md 
	 * 
	 * @throws IOException
	 */
	@Test
	public void testConvertToCSVPoint() throws IOException {
		File source = ResourceHelper.getResourceFile(getClass(),"/data/POINT.shp");
		File target = folder.newFile("POINT.csv");
		
		String expectedTargetName = getExpectedPointName();
		Assume.assumeNotNull(expectedTargetName);
		File expectedTarget = ResourceHelper.getResourceFile(getClass(),expectedTargetName);
		List<String> expectedLines = FileUtils.readLines(expectedTarget);
		try {
			fileConverter.convertToCSV(source, target);
			Assert.assertTrue(target.exists());
			List<String> lines = FileUtils.readLines(target);
			Assert.assertEquals(11, lines.size());
			for ( int i = 0; i < lines.size(); i++ ){
				String expected = expectedLines.get(i);
				String actual   = lines.get(i);
				Assert.assertEquals(expected, actual);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	/**
	 * Find expected CSV POINT file according to the GDAL current version
	 * @return
	 */
	private String getExpectedPointName(){
		OgrVersion version = FileConverter.getInstance().getVersion();
		if ( version.getFullVersion().startsWith("GDAL 1.10.") ){
			return "/data/POINT_EXPECTED_1.10.x.csv";
		}else if ( version.getFullVersion().startsWith("GDAL 1.11.") ){
			return "/data/POINT_EXPECTED_1.11.x.csv";
		}else if ( version.getFullVersion().startsWith("GDAL 2.1.") ){
			// supposed to be same as GDAL 2.2.2 output 
			return "/data/POINT_EXPECTED_2.2.x.csv";
		}else if ( version.getFullVersion().startsWith("GDAL 2.2.") ){
			return "/data/POINT_EXPECTED_2.2.x.csv";
		}else{
			System.err.println("GDAL version is not supported for this test : "+version);
			return null;
		}
	}

	/**
	 * Test dbf to csv with backslash as last char
	 */
	@Test
	public void testRegressBugBackslash01() throws IOException {
		File source = ResourceHelper.getResourceFile(getClass(),"/bug-backslash/source.dbf");
		File target = folder.newFile("output.csv");
		try {
			fileConverter.convertToCSV(source, target);
			Assert.assertTrue(target.exists());
			List<String> lines = FileUtils.readLines(target);
			Assert.assertEquals(2, lines.size());
			Assert.assertEquals("URLFIC,INSEE", lines.get(0));
			Assert.assertEquals(
				"\\\\ALPICITE-NAS\\etudes\\ORNON\\ELABORATION DU PLU\\14.GPU\\GPU ORNON\\38285_PLU_20171018\\,38285",
				lines.get(1)
			);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	/**
	 * Test csv to dbf to csv with backslash as last char
	 */
	@Test
	public void testRegressBugBackslash02() throws IOException {
		Assume.assumeFalse(isOgrVersionKnownToHaveBackslashBug());
		File source = ResourceHelper.getResourceFile(getClass(),"/bug-backslash/source.csv");
		File targetDbf = folder.newFile("output.dbf");
		File targetCsv = folder.newFile("output.csv");
		try {
			/* csv -> dbf */
			fileConverter.convertToCSV(source, targetDbf);
			Assert.assertTrue(targetDbf.exists());
			/* dbf -> csv */
			fileConverter.convertToCSV(targetDbf, targetCsv);
			Assert.assertTrue(targetCsv.exists());
			List<String> lines = FileUtils.readLines(targetCsv);
			Assert.assertEquals(2, lines.size());
			Assert.assertEquals("URLFIC,INSEE", lines.get(0));
			Assert.assertEquals(
				"\\\\ALPICITE-NAS\\etudes\\ORNON\\ELABORATION DU PLU\\14.GPU\\GPU ORNON\\38285_PLU_20171018\\,38285",
				lines.get(1)
			);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
	
	/**
	 * Allows to skip backslash test if ogr2ogr is known to have problems with fields ending with backslash
	 * @return
	 */
	public boolean isOgrVersionKnownToHaveBackslashBug(){
		if ( FileConverter.getInstance().getVersion().getFullVersion().startsWith("GDAL 1.10.1") ){
			return true;
		}
		return false;
	}

}
