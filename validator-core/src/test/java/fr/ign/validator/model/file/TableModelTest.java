package fr.ign.validator.model.file;

import java.io.File;

import org.junit.Test;

import fr.ign.validator.model.file.TableModel;
import junit.framework.TestCase;

public class TableModelTest extends TestCase {

	@Test
	public void testFullRegexp() {
		TableModel tableModel = new TableModel();
		tableModel.setRegexp("COMMUNE");
		assertEquals("(?i).*/COMMUNE\\.(dbf|DBF|tab|TAB|gml|GML|csv|CSV)", tableModel.getFullRegexp());
	}

	@Test
	public void testMatchPath() {
		TableModel tableModel = new TableModel();
		tableModel.setRegexp("COMMUNE");
		assertTrue(tableModel.matchPath(new File("/test/path/COMMUNE.dbf")));
		assertFalse(tableModel.matchPath(new File("/test/path/LIMITE_COMMUNE.dbf")));
	}

}
