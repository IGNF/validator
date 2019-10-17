package fr.ign.validator.tools;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import fr.ign.validator.exception.InvalidCharsetException;

/**
 * 
 * Test CSV file reading with TableReader
 * 
 * @author mickael
 *
 */
public class TableReaderCSVTest {

	@Test
	public void testReadCsvUtf8() throws IOException, InvalidCharsetException {
		File srcFile = ResourceHelper.getResourceFile(getClass(),"/csv/sample-utf8.csv") ;
		checkExpectedSampleContent( srcFile, StandardCharsets.UTF_8 ) ;
	}

	@Test
	public void testReadCsvLatin1() throws IOException, InvalidCharsetException {
		File srcFile = ResourceHelper.getResourceFile(getClass(),"/csv/sample-latin1.csv") ;
		checkExpectedSampleContent( srcFile, StandardCharsets.ISO_8859_1 ) ;
	}

	private void checkExpectedSampleContent( File srcFile, Charset charset ) throws IOException, InvalidCharsetException{
		TableReader csv = TableReader.createTableReader(srcFile,charset);
		String[] header = csv.getHeader() ;
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

	@Test
	public void testEmptyStringConvertedToNull(){
		File srcFile = ResourceHelper.getResourceFile(getClass(),"/csv/sample-with-empty.csv") ;
		try {
			TableReader csv = TableReader.createTableReader(srcFile,StandardCharsets.UTF_8);
			String[] header = csv.getHeader() ;
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
		} catch (InvalidCharsetException | IOException e) {
			fail(e.getMessage()) ;
		}
	}

	@Test
	public void testTrim(){
		File file = ResourceHelper.getResourceFile(getClass(),"/csv/not-trimmed.csv");
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
