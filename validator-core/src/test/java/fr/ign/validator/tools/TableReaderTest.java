package fr.ign.validator.tools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import junit.framework.TestCase;

import org.junit.Ignore;
import org.junit.Test;

import fr.ign.validator.reader.InvalidCharsetException;

public class TableReaderTest extends TestCase {

	
	@Test
	public void testReadTAB(){
		File file = new File(getClass().getResource("/data/ZONE_URBA_41003.TAB").getPath()) ;
		assertTrue(file.exists());
		try {
			TableReader reader = TableReader.createTableReader(file,StandardCharsets.ISO_8859_1);

			String[] header = reader.getHeader() ;
			assertEquals( 10, header.length );
			
			int count = 0 ;
			while ( reader.hasNext() ){
				String[] row = reader.next() ;
				assertEquals(header.length, row.length);
				count++ ;
			}
			assertEquals(36, count);			
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (InvalidCharsetException e) {
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testReadDocUrbaComGML(){
		File file = new File(getClass().getResource("/gml/DOC_URBA_COM.gml").getPath()) ;
		assertTrue(file.exists());
		try {
			TableReader reader = TableReader.createTableReader(file, StandardCharsets.UTF_8);

			String[] header = reader.getHeader() ;
			assertTrue(header.length >= 4);

			assertTrue(Arrays.asList(header).contains("WKT")) ;
			assertTrue(Arrays.asList(header).contains("gml_id")) ;
			assertTrue(Arrays.asList(header).contains("IDURBA")) ;
			assertTrue(Arrays.asList(header).contains("INSEE")) ;

			// test case insensitive find
			assertEquals(2,reader.findColumn("IDURBA"));
			assertEquals(2,reader.findColumn("IdUrBa"));			
			assertEquals(-1,reader.findColumn("_IdUrBa"));
			assertEquals(-1,reader.findColumn("IdUrBa_"));			
			
			// FIXME DATECOG ignoré car balise autofermante <gp-urba:DATECOG/>
			// N'apparait pas dans le DescribeFeatureType
			// assertTrue(Arrays.asList(header).contains("DATECOG")) ;
			
			String[] row = reader.next() ;
			assertTrue(Arrays.asList(row).contains("POINT (225499.742202532826923 6755725.590427031740546)")) ; //WKT
			assertTrue(Arrays.asList(row).contains("DOC_URBA_COM.13")) ; //gml_id
			assertTrue(Arrays.asList(row).contains("5611820140612")) ; //IDURBA
			assertTrue(Arrays.asList(row).contains("56118")) ; // INSEE
						
			assertFalse(reader.hasNext());
			
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (InvalidCharsetException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testReadPrescriptionSurfGML(){
		File file = new File(getClass().getResource("/gml/PRESCRIPTION_SURF.gml").getPath()) ;
		assertTrue(file.exists());
		try {
			TableReader reader = TableReader.createTableReader(file, StandardCharsets.UTF_8);

			String[] header = reader.getHeader() ;
			
			assertTrue( reader.findColumn("TXT") >= 0 ) ;			
			assertTrue( reader.findColumn("WKT") >= 0 ) ;
			assertTrue( reader.findColumn("TYPEPSC") >= 0 ) ;
			assertTrue( reader.findColumn("TYPEPSC2") >= 0 ) ;
			
			assertTrue( reader.findColumn("URLFIC") >= 0 ) ; // always empty not removed

			assertTrue(reader.hasNext());
			String[] row = reader.next() ;
			assertEquals(header.length, row.length);
			
			// check that 05 is not converted to 5
			assertTrue(Arrays.asList(row).contains("05")) ; // TYPEPSC
			assertTrue(Arrays.asList(row).contains("20140123"));
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (InvalidCharsetException e) {
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Ce test fonctionne en fonction des versions d'ogr2ogr
	 */
    @Test
    @Ignore("reprendre avec un jeu test où les FeatureType sont accessibles")
	public void testReadZoneUrbaGML(){
    	return ;/*
    			
    	File file = new File(getClass().getResource("/gml/ZONE_URBA.gml").getPath()) ;
		assertTrue(file.exists());
		try {
			TableReader reader = TableReader.createTableReader(file,StandardCharsets.UTF_8);

			String[] header = reader.getHeader() ;

			assertTrue(Arrays.asList(header).contains("LIBELLE")) ;
			// patché, n'apparait pas quand il est vide
			assertTrue(Arrays.asList(header).contains("NOMFIC")) ;

		} catch (IOException e) {
			fail(e.getMessage());
		} catch (InvalidCharsetException e) {
			fail(e.getMessage());
		}*/
	}
    
    
    /**
     * La conversion ogr2ogr en CSV produit un résultat étrange :
     *
     *		IDSUP,
     *		75
     *		26
     *		85
     *		84
     *		83
     *		82
     *		81
     *		80
     *
     * Ce test vérifier que les éléments "null" de l'entête sont ignorés...
     */
    public void testReadSingleColumn(){
    	File file = new File(getClass().getResource("/dbf/SINGLE_COLUMN_BUG.dbf").getPath()) ;
		assertTrue(file.exists());
		try {
			TableReader reader = TableReader.createTableReader(file,StandardCharsets.UTF_8);

			String[] header = reader.getHeader() ;

			assertEquals(1,header.length);
			assertEquals("IDSUP",header[0]);
			
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (InvalidCharsetException e) {
			fail(e.getMessage());
		}
    }
    

	@Test
	public void testTrim(){
		File file = new File(getClass().getResource("/csv/not-trimmed.csv").getPath()) ;
		assertTrue(file.exists());
		try {
			TableReader reader = TableReader.createTableReader(file,StandardCharsets.UTF_8);

			String[] header = reader.getHeader() ;

			assertTrue(Arrays.asList(header).contains("A")) ;
			assertTrue(Arrays.asList(header).contains("B")) ;
			
			assertTrue(reader.hasNext());
			String[] row = reader.next() ;
			assertEquals("valeur A", row[0]);
			assertEquals("valeur B", row[1]);
			
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (InvalidCharsetException e) {
			fail(e.getMessage());
		}
	}

	
	
}
