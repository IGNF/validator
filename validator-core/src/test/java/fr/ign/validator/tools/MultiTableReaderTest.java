package fr.ign.validator.tools;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class MultiTableReaderTest {

    /**
     * Test without providing an XSDschema (use standard GML driver in GDAL)
     * 
     * @throws IOException
     */
    @Test
    public void testReadPcrsLyon01() throws IOException {
        File srcFile = ResourceHelper.getResourceFile(
            getClass(), "/documents/pcrs-lyon-01/20200716.RIL_L264_C298_P0.gml"
        );
        clearCache(srcFile);

        TableReaderOptions options = new TableReaderOptions();
        assertNull(options.getXsdSchema());
        MultiTableReader reader = MultiTableReader.createMultiTableReader(srcFile, options);
        List<String> tableNames = reader.getTableNames();
        assertEquals(11, tableNames.size());
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

    /**
     * Test without providing an XSDschema (using GMLAS driver in GDAL)
     * 
     * @throws IOException
     */
    @Test
    public void testReadPcrsLyon01WithSchema() throws IOException {
        File srcFile = ResourceHelper.getResourceFile(
            getClass(), "/documents/pcrs-lyon-01/20200716.RIL_L264_C298_P0.gml"
        );
        clearCache(srcFile);
        TableReaderOptions options = new TableReaderOptions();
        options.setXsdSchema(new URL("https://cnigfr.github.io/PCRS/schemas/CNIG_PCRS_v2.0.xsd"));
        MultiTableReader reader = MultiTableReader.createMultiTableReader(srcFile, options);
        List<String> tableNames = reader.getTableNames();
        String[] expectedTableNamess = {
            "affleurantenveloppepcrs",
            "affleurantgeometriquepcrs",
            "affleurantgeometriquepcrs_enveloppe",
            "affleurantgeometriquepcrs_point",
            "affleurantpcrs",
            "affleurantpointpcrs",
            "affleurantsymbolepcrs",
            "arbrepcrs",
            "empriseechangepcrs",
            "empriseechangepcrs_habillage",
            "empriseechangepcrs_objet",
            "facadepcrs",
            "habillagelignespcrs",
            "limitevoiriepcrs",
            "plancorpsruesimplifie",
            "plancorpsruesimplifie_featuremember",
            "seuilpcrs",
        };
        assertArrayEquals(expectedTableNamess, tableNames.toArray());

        // HabillageLignesPCRS
        {
            TableReader tableReader = reader.getTableReader("HabillageLignesPCRS");
            String[] expectedColumns = {
                "WKT",
                "ogr_pkid",
                "id",
                "description_href",
                "description_title",
                "description_nilreason",
                "description",
                "descriptionreference_href",
                "descriptionreference_title",
                "descriptionreference_nilreason",
                "identifier_codespace",
                "identifier",
                "location_location_pkid",
                "idhabillage",
                "thematique"
            };
            assertArrayEquals(expectedColumns, tableReader.getHeader());
            // check first row
            {
                String[] row = tableReader.next();
                // id
                assertEquals("gml_5ededfe4-8ebb-4cf1-8621-b80a9b6e3912", row[2]);
            }
        }
        // SeuilPCRS
        {
            TableReader tableReader = reader.getTableReader("SeuilPCRS");
            String[] expectedColumns = {
                "WKT",
                "ogr_pkid",
                "id",
                "description_href",
                "description_title",
                "description_nilreason",
                "description",
                "descriptionreference_href",
                "descriptionreference_title",
                "descriptionreference_nilreason",
                "identifier_codespace",
                "identifier",
                "location_location_pkid",
                "dateleve",
                "idobjet",
                "thematique",
                "qualitecategorisation",
                "precisionplanimetrique",
                "precisionaltimetrique",
                "producteur",
                "symbole_href",
                "symbole_title",
                "symbole_nilreason",
                "symbole_owns",
                "symbole_habillagesymbolepcrs_pkid",
            };
            assertArrayEquals(expectedColumns, tableReader.getHeader());
            // check first row
            {
                String[] row = tableReader.next();
                // id
                assertEquals("gml_48b70971-dce9-40b9-abec-eb307dd4f946", row[2]);
            }
        }
    }

    private void clearCache(File srcFile) throws IOException {
        File csvDirectory = CompanionFileUtils.getCompanionFile(srcFile, MultiTableReader.TMP_EXTENSION);
        if (csvDirectory.exists()) {
            FileUtils.forceDelete(csvDirectory);
        }
    }

}
