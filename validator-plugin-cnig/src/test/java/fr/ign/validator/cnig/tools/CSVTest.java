package fr.ign.validator.cnig.tools;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import fr.ign.validator.tools.ResourceHelper;

public class CSVTest {

    @Test
    public void testCountRows() throws IOException {
        File csvFile = ResourceHelper.getResourceFile(getClass(), "/csv/DOC_URBA.csv");
        int numRows = CSV.countRows(csvFile);
        assertEquals(844, numRows);
    }

}
