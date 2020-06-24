package fr.ign.validator.model.file;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

public class TableModelTest {

    @Test
    public void testFullRegexp() {
        TableModel tableModel = new TableModel();
        tableModel.setRegexp("COMMUNE");
        Assert.assertEquals("(?i).*/COMMUNE\\.(dbf|DBF|tab|TAB|gml|GML|csv|CSV)", tableModel.getFullRegexp());
    }

    @Test
    public void testMatchPath() {
        TableModel tableModel = new TableModel();
        tableModel.setRegexp("COMMUNE");
        Assert.assertTrue(tableModel.matchPath(new File("/test/path/COMMUNE.dbf")));
        Assert.assertFalse(tableModel.matchPath(new File("/test/path/LIMITE_COMMUNE.dbf")));
    }

}
