package fr.ign.validator.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import fr.ign.validator.tools.ResourceHelper;
import fr.ign.validator.exception.InvalidCharsetException;

public class TableReaderLatinUtf8Test {

	@Test
	public void testReadTabLatin1(){
		File file = ResourceHelper.getResourceFile(getClass(),"/data/tab_latin1/PRESCRIPTION_PCT.TAB") ;
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
		File file = ResourceHelper.getResourceFile(getClass(),"/data/shp_latin1/PRESCRIPTION_PCT.shp") ;
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
		File file = ResourceHelper.getResourceFile(getClass(),"/data/tab_utf8/PRESCRIPTION_PCT.tab") ;
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
		File file = ResourceHelper.getResourceFile(getClass(),"/data/shp_utf8/PRESCRIPTION_PCT.shp") ;
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
		File file = ResourceHelper.getResourceFile(getClass(),"/data/dbf_latin1/ACTE_SUP.dbf") ;
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
