package fr.ign.validator.tools;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import fr.ign.validator.tools.FileConverter;
import junit.framework.TestCase;


/**
 * 
 * @author cbouche
 *
 */
public class FileConverterTest extends TestCase {

	FileConverter fileConverter ;
	
	@Override
	protected void setUp() throws Exception {
		fileConverter = new FileConverter() ;
	}
	
	@Override
	protected void tearDown() throws Exception {
		fileConverter = null ;
		File target0 = new File("ZONE_URBA.shp") ;
		if ( target0.exists() ) {
			target0.delete() ;
		}
		File target1 = new File("ZONE_URBA.dbf") ;
		if ( target1.exists() ) {
			target1.delete() ;
		}
		File target2 = new File("ZONE_URBA.prj") ;
		if ( target2.exists() ) {
			target2.delete() ;
		}
		File target3 = new File("ZONE_URBA.shx") ;
		if ( target3.exists() ) {
			target3.delete() ;
		}
		File target4 = new File("ZONE_URBA.csv") ;
		if ( target4.exists() ) {
			target4.delete() ;
		}
	}
	

	@Test
	public void testCreateFile() {
		File target = new File("ZONE_URBA.shp") ;
		try {
			target.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
			fail() ;
		}
	}

	
	@Test
	public void testConvertToCSV() {
		File source = new File(getClass().getResource("/data/ZONE_URBA_41003.TAB").getPath()) ;
		File target = new File("ZONE_URBA.csv") ;
		try {
			fileConverter.convertToCSV(source, target);
		} catch (Exception e) {
			e.printStackTrace();
			fail() ;
		}
	}
}
