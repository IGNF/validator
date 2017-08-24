package fr.ign.validator.tools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import fr.ign.validator.reader.InvalidCharsetException;
import junit.framework.TestCase;

/**
 * 
 * Test de TableReader en présence de caractères spéciaux
 * 
 * @author MBorne
 *
 */
public class TableReaderSpecialCharsTest extends TestCase {

	
	@Test
	public void testReadFileWithBadChars(){
		// read file where last character in NOMFIC column is not printable
		File file = new File(getClass().getResource("/dbf/SPECIAL_CHARS.DBF").getPath()) ;
		assertTrue(file.exists());
		try {
			TableReader reader = TableReader.createTableReader(file,StandardCharsets.ISO_8859_1);

			String[] header = reader.getHeader() ;
			assertEquals( 9, header.length );
			
			int indexNomFic = reader.findColumn("NOMFIC");
			assertTrue(indexNomFic >= 0);
			
			int count = 0 ;
			while ( reader.hasNext() ){
				String[] row = reader.next() ;
				assertEquals(header.length, row.length);
				
				String nomfic = row[indexNomFic];
				char lastChar = nomfic.charAt(nomfic.length()-1);
				assertEquals("\\u0092",toUnicode(lastChar));
				
				// could crash with some java version
				File fileWithBadChar = new File(nomfic);
				assertEquals(nomfic,fileWithBadChar.toString());
				
				count++ ;
			}
			assertEquals(85, count);			
		} catch (IOException e) {
			fail(e.getMessage());
		} catch (InvalidCharsetException e) {
			fail(e.getMessage());
		}
	}
	


	private static String toUnicode(char ch) {
	    return String.format("\\u%04x", (int) ch);
	}
	

	
	
}
