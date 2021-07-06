package fr.ign.validator.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

public class MultiTableReaderTest {

    @Test
    public void testReadPcrsLyon01() throws IOException {
        File srcFile = ResourceHelper.getResourceFile(
            getClass(), "/documents/pcrs-lyon-01/20200716.RIL_L264_C298_P0.gml"
        );
        MultiTableReader reader = MultiTableReader.createMultiTableReader(srcFile);
        List<String> tableNames = reader.getTableNames();
        assertEquals(6, tableNames.size());
        // HabillageLignesPCRS
        {
            TableReader tableReader = reader.getTableReader("HabillageLignesPCRS");
            assertEquals(4, tableReader.getHeader().length);
            assertTrue(tableReader.findColumn("gml_id") >= 0);
            assertTrue(tableReader.findColumn("idHabillage") >= 0);
            assertTrue(tableReader.findColumn("thematique") >= 0);
            assertTrue(tableReader.findColumn("WKT") >= 0);
        }
        // SeuilPCRS
        // LimiteVoiriePCRS
        // FacadePCRS
        // ArbrePCRS
        // AffleurantPCRS
    }

}
