package fr.ign.validator.tools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import fr.ign.validator.exception.InvalidCharsetException;
import junit.framework.TestCase;

public class TableReaderLatinUtf8Test extends TestCase {

	@Test
	public void testReadTabLatin1(){
		File file = new File(getClass().getResource("/data/tab_latin1/PRESCRIPTION_PCT.TAB").getPath()) ;
		assertTrue(file.exists());
		try {
			TableReader reader = TableReader.createTableReader(file, StandardCharsets.ISO_8859_1);
			// check header
			{
				String[] row = reader.getHeader() ;				
				assertEquals(9,row.length);
				assertEquals("WKT", row[0]);
				assertEquals("LIBELLE", row[1]);
			}
			// check line 1
			{
				String[] row = reader.next() ;				
				assertEquals(9,row.length);
				
				assertEquals("Bâtiment agricole", row[1]);//LIBELLE
				
			}
			
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (InvalidCharsetException e) {
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testReadShpLatin1(){
		File file = new File(getClass().getResource("/data/shp_latin1/PRESCRIPTION_PCT.shp").getPath()) ;
		assertTrue(file.exists());
		try {
			TableReader reader = TableReader.createTableReader(file, StandardCharsets.ISO_8859_1);
			// check header
			{
				String[] row = reader.getHeader() ;				
				assertEquals(9,row.length);
				assertEquals("WKT", row[0]);
				assertEquals("LIBELLE", row[1]);
			}
			// check line 1
			{
				String[] row = reader.next() ;				
				assertEquals(9,row.length);
				
				assertEquals("Bâtiment agricole", row[1]);//LIBELLE
			}
			
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (InvalidCharsetException e) {
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testReadTabUtf8(){
		File file = new File(getClass().getResource("/data/tab_utf8/PRESCRIPTION_PCT.tab").getPath()) ;
		assertTrue(file.exists());
		try {
			TableReader reader = TableReader.createTableReader(file, StandardCharsets.UTF_8);
			// check header
			{
				String[] row = reader.getHeader() ;				
				assertEquals(9,row.length);
				assertEquals("WKT", row[0]);
				assertEquals("LIBELLE", row[1]);
			}
			// check line 1
			{
				String[] row = reader.next() ;				
				assertEquals(9,row.length);
				
				assertEquals("Bâtiment agricole", row[1]);//LIBELLE
				
			}
			
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (InvalidCharsetException e) {
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testReadShpUtf8(){
		File file = new File(getClass().getResource("/data/shp_utf8/PRESCRIPTION_PCT.shp").getPath()) ;
		assertTrue(file.exists());
		try {
			TableReader reader = TableReader.createTableReader(file, StandardCharsets.UTF_8);
			// check header
			{
				String[] row = reader.getHeader() ;				
				assertEquals(9,row.length);
				assertEquals("WKT", row[0]);
				assertEquals("LIBELLE", row[1]);
			}
			// check line 1
			{
				String[] row = reader.next() ;				
				assertEquals(9,row.length);
				
				assertEquals("Bâtiment agricole", row[1]);//LIBELLE
			}
			
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (InvalidCharsetException e) {
			fail(e.getMessage());
		}
		
	}
	
	
	@Test
	public void testReadDbfLatin1(){
		File file = new File(getClass().getResource("/data/dbf_latin1/ACTE_SUP.dbf").getPath()) ;
		assertTrue(file.exists());
		try {
			TableReader reader = TableReader.createTableReader(file, StandardCharsets.ISO_8859_1);
			// check header
			{
				String[] row = reader.getHeader() ;				
				assertEquals(9,row.length);
				assertEquals("IdActe", row[0]);
				assertEquals("nomActe", row[1]);
			}
			// check line 1
			{
				String[] row = reader.next() ;				
				assertEquals(9,row.length);
				
				assertEquals("Création", row[5]);//LIBELLE
			}
			
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (InvalidCharsetException e) {
			fail(e.getMessage());
		}
		
	}
	
}
