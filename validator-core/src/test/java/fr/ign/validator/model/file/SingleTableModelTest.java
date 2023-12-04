package fr.ign.validator.model.file;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

public class SingleTableModelTest {

    @Test
    public void testFullRegexp() {
        SingleTableModel tableModel = new SingleTableModel();
        tableModel.setPath("COMMUNE");
        Assert.assertEquals("(?i).*/COMMUNE\\.(dbf|DBF|tab|TAB|gml|GML|csv|CSV|gpkg|GPKG)", tableModel.getPathRegexp());
    }

    @Test
    public void testMatchPath() {
        SingleTableModel tableModel = new SingleTableModel();
        tableModel.setPath("COMMUNE");
        Assert.assertTrue(tableModel.matchPath(new File("/test/path/COMMUNE.dbf")));
        Assert.assertFalse(tableModel.matchPath(new File("/test/path/LIMITE_COMMUNE.dbf")));
    }

}
