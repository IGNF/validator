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

/**
 * Test TableReader with ESRI Shapefiles
 * 
 * @author mborne
 */
public class TableReaderSHPTest {

	@Test
	public void testReadShpLatin1() {
		File file = ResourceHelper.getResourceFile(getClass(), "/data/shp_latin1/PRESCRIPTION_PCT.shp");
		assertTrue(file.exists());
		try {
			TableReader reader = TableReader.createTableReader(file, StandardCharsets.ISO_8859_1);
			// check header
			{
				String[] row = reader.getHeader();
				assertEquals(9, row.length);
				assertEquals("WKT", row[0]);
				assertEquals("LIBELLE", row[1]);
			}
			// check line 1
			{
				String[] row = reader.next();
				assertEquals(9, row.length);

				assertEquals("Bâtiment agricole", row[1]);// LIBELLE
			}

		} catch (IOException e) {
			fail(e.getMessage());
		} catch (InvalidCharsetException e) {
			fail(e.getMessage());
		}

	}

	@Test
	public void testReadShpUtf8() {
		File file = ResourceHelper.getResourceFile(getClass(), "/data/shp_utf8/PRESCRIPTION_PCT.shp");
		assertTrue(file.exists());
		try {
			TableReader reader = TableReader.createTableReader(file, StandardCharsets.UTF_8);
			// check header
			{
				String[] row = reader.getHeader();
				assertEquals(9, row.length);
				assertEquals("WKT", row[0]);
				assertEquals("LIBELLE", row[1]);
			}
			// check line 1
			{
				String[] row = reader.next();
				assertEquals(9, row.length);

				assertEquals("Bâtiment agricole", row[1]);// LIBELLE
			}

		} catch (IOException e) {
			fail(e.getMessage());
		} catch (InvalidCharsetException e) {
			fail(e.getMessage());
		}

	}

	@Test
	public void testReadDbfLatin1() {
		File file = ResourceHelper.getResourceFile(getClass(), "/data/dbf_latin1/ACTE_SUP.dbf");
		assertTrue(file.exists());
		try {
			TableReader reader = TableReader.createTableReader(file, StandardCharsets.ISO_8859_1);
			// check header
			{
				String[] row = reader.getHeader();
				assertEquals(9, row.length);
				assertEquals("IdActe", row[0]);
				assertEquals("nomActe", row[1]);
			}
			// check line 1
			{
				String[] row = reader.next();
				assertEquals(9, row.length);

				assertEquals("Création", row[5]);// LIBELLE
			}

		} catch (IOException e) {
			fail(e.getMessage());
		} catch (InvalidCharsetException e) {
			fail(e.getMessage());
		}

	}

	@Test
	public void testReadFileWithBadChars() {
		// read file where last character in NOMFIC column is not "printable"
		File file = ResourceHelper.getResourceFile(getClass(), "/dbf/SPECIAL_CHARS.DBF");
		assertTrue(file.exists());
		try {
			TableReader reader = TableReader.createTableReader(file, StandardCharsets.ISO_8859_1);

			String[] header = reader.getHeader();
			assertEquals(9, header.length);

			int indexNomFic = reader.findColumn("NOMFIC");
			assertTrue(indexNomFic >= 0);

			int count = 0;
			while (reader.hasNext()) {
				String[] row = reader.next();
				assertEquals(header.length, row.length);

				String nomfic = row[indexNomFic];
				char lastChar = nomfic.charAt(nomfic.length() - 1);
				assertEquals("\\u0092", toUnicode(lastChar));

				count++;
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

	/**
	 * ogr2ogr csv conversion outputs a strange result :
	 *
	 * IDSUP, 75 26 85 84 83 82 81 80
	 *
	 * This test verifies that the null elements from header are ignored
	 */
	public void testReadSingleColumn() {
		File file = ResourceHelper.getResourceFile(getClass(), "/dbf/SINGLE_COLUMN_BUG.dbf");
		assertTrue(file.exists());
		try {
			TableReader reader = TableReader.createTableReader(file, StandardCharsets.UTF_8);

			String[] header = reader.getHeader();

			assertEquals(1, header.length);
			assertEquals("IDSUP", header[0]);

		} catch (IOException e) {
			fail(e.getMessage());
		} catch (InvalidCharsetException e) {
			fail(e.getMessage());
		}
	}

}
