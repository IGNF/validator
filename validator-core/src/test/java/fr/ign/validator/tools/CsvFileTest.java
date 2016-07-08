package fr.ign.validator.tools;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class CsvFileTest {
	
	@Test
	public void testEmptyStringConvertedToNull(){
		File srcFile = new File(getClass().getResource("/csv/sample-with-empty.csv").getPath()) ;
		try {
			CSVReader csv = new CSVReader(srcFile,StandardCharsets.UTF_8);
			String[] header = csv.next() ;
			assertEquals(3,header.length);
			assertEquals("a",header[0]);
			assertEquals("b",header[1]);
			assertEquals("c",header[2]);
			
			String[] row = csv.next() ;
			assertEquals(3,row.length);
			assertEquals("ligne",row[0]);
			assertNull(row[1]);
			assertNull(row[2]);
			
			assertFalse(csv.hasNext());
		} catch (IOException e) {
			fail(e.getMessage()) ;
		}
		
	}
	
	@Test
	public void testReadUTF8() {
		File srcFile = new File(getClass().getResource("/csv/sample-utf8.csv").getPath()) ;
		try {
			checkFileContent( srcFile, StandardCharsets.UTF_8 ) ;
		} catch (IOException e) {
			e.printStackTrace() ;
			fail( e.getMessage() ) ;
		}
	}
	
	
	@Test
	public void testReadLatin1() {
		File srcFile = new File(getClass().getResource("/csv/sample-latin1.csv").getPath()) ;
		try {
			checkFileContent( srcFile, StandardCharsets.ISO_8859_1 ) ;
		} catch (IOException e) {
			e.printStackTrace() ;
			fail( e.getMessage() ) ;
		}
	}

	
	/*
	@Test	
	public void testPrescriptionPct41003() throws IOException{
		File srcFile = new File(getClass().getResource("/csv/PRESCRIPTION_PCT_41003.csv").getPath()) ;
		CsvFile csv = new CsvFile(srcFile);
		csv.open();
		assertEquals( 6, csv.size() ) ;
		csv.close();
	}
	
	@Test	
	public void testZoneUrba41003() throws IOException{
		File srcFile = new File(getClass().getResource("/csv/ZONE_URBA_41003-utf8.csv").getPath()) ;
		CsvFile csv = new CsvFile(srcFile);
		csv.open();
		assertEquals( 6, csv.size() ) ;
		csv.close();
	}
	*/
	
	private void checkFileContent( File srcFile, Charset charset ) throws IOException{
		CSVReader csv = new CSVReader(srcFile,charset);
		String[] header = csv.next() ;
		assertEquals( header.length, 3 );
		assertEquals( header[0], "a" ) ;
		assertEquals( header[1], "b" ) ;
		assertEquals( header[2], "c" ) ;

		String[] line1 = csv.next() ;
		assertEquals( line1[0], "a1" ) ;
		assertEquals( line1[1], "b1" ) ;
		assertEquals( line1[2], "c1" ) ;
		
		String[] line2 = csv.next() ;
		assertEquals( line2[0], "aé2" ) ;
		assertEquals( line2[1], "bé2" ) ;
		assertEquals( line2[2], "cé2" ) ;
		
		assertFalse(csv.hasNext());
	}
	
}
