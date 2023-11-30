package fr.ign.validator.tools;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

public class MultiTableReaderTest {

    @Before
    public void setUp() {
        FileConverter.getInstance().setGmlasConfig(null);
    }

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

    /**
     * Test without providing an XSDschema (using GMLAS driver in GDAL)
     * 
     * @throws IOException
     */
    @Test
    public void testReadPcrsLyon01WithSchemaAndGmlasConfig() throws IOException {
        File srcFile = ResourceHelper.getResourceFile(
            getClass(), "/documents/pcrs-lyon-01/20200716.RIL_L264_C298_P0.gml"
        );
        clearCache(srcFile);
        FileConverter.getInstance().setGmlasConfig(
            ResourceHelper.getResourceFile(FileConverter.class, "/gdal/gmlasconf-validator.xml")
        );
        TableReaderOptions options = new TableReaderOptions();
        options.setXsdSchema(new URL("https://cnigfr.github.io/PCRS/schemas/CNIG_PCRS_v2.0.xsd"));
        MultiTableReader reader = MultiTableReader.createMultiTableReader(srcFile, options);
        List<String> tableNames = reader.getTableNames();
        String[] expectedTableNamess = {
            "AffleurantEnveloppePCRS",
            "AffleurantGeometriquePCRS",
            "AffleurantGeometriquePCRS_enveloppe",
            "AffleurantGeometriquePCRS_point",
            "AffleurantPCRS",
            "AffleurantPointPCRS",
            "AffleurantSymbolePCRS",
            "ArbrePCRS",
            "EmpriseEchangePCRS",
            "EmpriseEchangePCRS_habillage",
            "EmpriseEchangePCRS_objet",
            "FacadePCRS",
            "HabillageLignesPCRS",
            "LimiteVoiriePCRS",
            "PlanCorpsRueSimplifie",
            "PlanCorpsRueSimplifie_featureMember",
            "SeuilPCRS",
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
                "description_nilReason",
                "description",
                "descriptionReference_href",
                "descriptionReference_title",
                "descriptionReference_nilReason",
                "identifier_codeSpace",
                "identifier",
                "location_location_pkid",
                "idHabillage",
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
                "description_nilReason",
                "description",
                "descriptionReference_href",
                "descriptionReference_title",
                "descriptionReference_nilReason",
                "identifier_codeSpace",
                "identifier",
                "location_location_pkid",
                "dateLeve",
                "idObjet",
                "thematique",
                "qualiteCategorisation",
                "precisionPlanimetrique",
                "precisionAltimetrique",
                "producteur",
                "symbole_href",
                "symbole_title",
                "symbole_nilReason",
                "symbole_owns",
                "symbole_HabillageSymbolePCRS_pkid",
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

    @Test
    public void testReadGeopackage() throws IOException {
        File srcFile = ResourceHelper.getResourceFile(
            getClass(), "/geopackage/pprt_69dreal20090005/pprt_69dreal20090005.gpkg"
        );
        clearCache(srcFile);

        MultiTableReader reader = MultiTableReader.createMultiTableReader(srcFile, new TableReaderOptions());
        List<String> tableNames = reader.getTableNames();

        String[] expectedTableNamess = {
            "pprt_69dreal20090005_perimetre_s",
            "pprt_69dreal20090005_procedure",
            "pprt_69dreal20090005_referenceinternet",
            "pprt_69dreal20090005_zonereglementairefoncier_s",
            "pprt_69dreal20090005_zonereglementaireurba_s",
        };
        assertArrayEquals(expectedTableNamess, tableNames.toArray());

        TableReader tableReader = reader.getTableReader("pprt_69dreal20090005_perimetre_s");
        String[] expectedColumns = {
            "WKT",
            "idperimetre",
            "codeprocedure",
            "etatprocedure",
            "dateetat"
        };
        assertArrayEquals(expectedColumns, tableReader.getHeader());

        String[] row = tableReader.next();

        assertEquals(
            "[MULTIPOLYGON (((922139.398009804 6459589.3099722,922129.088609526 6459575.09077642,922122.329329887 6459566.13318788,922114.928136819 6459557.08220995,922107.171859057 6459548.33201155,922099.072596367 6459539.89547668,922090.643156405 6459531.78648232,922081.89683774 6459524.01790217,922072.847538347 6459516.60260479,922063.509638521 6459509.55145639,922053.897718358 6459502.87532148,922044.027165748 6459496.58605672,922033.913442712 6459490.69252088,922023.572619263 6459485.20456657,922016.198878639 6459481.65829027,922013.883831808 6459479.74397525,922003.577383789 6459471.87491463,921992.970708042 6459464.41206434,921982.079900352 6459457.36827403,921970.921621547 6459450.75239216,921959.512857924 6459444.57626162,921947.870961025 6459438.84772585,921936.01359927 6459433.57662374,921923.958923403 6459428.77079185,921911.725175478 6459424.43706688,921899.331196953 6459420.58228039,921886.795620892 6459417.21226663,921874.137679764 6459414.33285471,921861.376588858 6459411.94787574,921848.531854572 6459410.06015921,921835.622891997 6459408.67353451,921822.669607133 6459407.79082773,921809.691480611 6459407.41187134,921796.708301351 6459407.53749428,921783.740058075 6459408.16852376,921770.806214233 6459409.30279426,921757.926641467 6459410.93913584,921745.121003029 6459413.07538126,921732.408645288 6459415.70736786,921719.808923203 6459418.83193197,921707.341183147 6459422.44491093,921695.024346117 6459426.53914849,921682.877042001 6459431.10848998,921670.917909276 6459436.14777975,921659.16515246 6459441.64786954,921647.636585057 6459447.60061355,921636.349820771 6459453.99786769,921625.322147835 6459460.82849343,921614.664802248 6459468.01560417,921606.586799454 6459473.69390386,921604.439155303 6459475.27193589,921601.97337694 6459476.78876216,921591.067587126 6459484.10689049,921589.749081813 6459485.03438497,921581.557580383 6459490.82855483,921570.370329795 6459499.0972313,921559.943815234 6459507.48625,921549.864623762 6459516.28690103,921540.149210057 6459525.48405669,921530.813246768 6459535.06459418,921521.871781375 6459545.01239884,921513.339544474 6459555.30936056,921505.230302017 6459565.94137385,921497.557086292 6459576.89034323,921490.332529985 6459588.13817659,921483.568166878 6459599.66679135,921477.275022656 6459611.45711015,921471.463432294 6459623.49106069,921466.142606089 6459635.74758307,921461.321371915 6459648.20761887,921457.00745874 6459660.85211907,921453.207870458 6459673.6590437,921449.928720445 6459686.60735947,921447.175431366 6459699.67703814,921444.952418285 6459712.84706105,921443.263279885 6459726.09441838,921442.110750098 6459739.40010409,921441.496729293 6459752.74112296,921441.422227323 6459766.09548653,921441.88725503 6459779.4412149,921442.891241035 6459792.75833136,921444.432397967 6459806.02487147,921446.508230561 6459819.21787782,921449.115461525 6459832.31639786,921452.249823147 6459845.30048666,921455.90623133 6459858.14820813,921460.078811359 6459870.83863205,921464.760698102 6459883.35183582,921469.944309939 6459895.66590482,921475.621200502 6459907.76292819,921481.782289656 6459919.62100421,921488.41754121 6459931.22323475,921495.516193894 6459942.54973071,921500.250324765 6459949.57752772,921522.387543057 6459981.55062026,921529.904916168 6459991.97134543,921538.150529145 6460002.49770616,921546.814637583 6460012.68477711,921555.883409052 6460022.51569283,921565.341920799 6460031.97459631,921575.17505886 6460041.04663125,921585.36670167 6460049.71595095,921595.900453731 6460057.96970831,921606.759111746 6460065.79406408,921617.925081405 6460073.17618148,921629.380277481 6460080.10422702,921641.106223733 6460086.56736964,921653.083636122 6460092.55378616,921665.29336487 6460098.05564858,921677.715435219 6460103.06213873,921690.32980687 6460107.56643534,921703.11593143 6460111.56072244,921716.053077883 6460115.03918378,921729.120306819 6460117.99500583,921742.296404898 6460120.42437466,921755.559958974 6460122.32347804,921768.889547314 6460123.68750478,921782.26349143 6460124.51664126,921795.660087065 6460124.80807685,921809.05755583 6460124.56199883,921822.434310549 6460123.77759374,921835.768598604 6460122.45804584,921849.038741507 6460120.60354157,921862.223294933 6460118.21826162,921875.300714657 6460115.3063876,921888.249947369 6460111.87109777,921901.049974118 6460107.91956646,921913.679967167 6460103.45796725,921926.119498383 6460098.49247029,921938.348265303 6460093.03224191,921950.346656184 6460087.08544339,921962.094976561 6460080.66223493,921973.574331178 6460073.77276983,921980.113757717 6460069.5593607,921988.536109218 6460063.99298964,922000.711552909 6460055.54996144,922003.860755066 6460053.15605801,922004.164419736 6460052.96362138,922009.036900591 6460049.67071277,922017.115892851 6460044.10729405,922028.471277683 6460035.9300793,922039.067269768 6460027.61452745,922049.316360406 6460018.87834784,922059.201877934 6460009.73467192,922061.903372945 6460007.02491201,922062.175994225 6460006.83573955,922064.020649113 6460005.60999004,922073.830528163 6459998.80081921,922082.996657626 6459991.88228937,922091.885127415 6459984.61347122,922100.482237121 6459977.00447338,922108.774894333 6459969.06639834,922116.750623224 6459960.81234144,922124.397039283 6459952.25439819,922131.702665694 6459943.40565532,922138.656433834 6459934.28019517,922145.247557606 6459924.89009948,922151.466093069 6459915.25243815,922157.302552838 6459905.37928175,922162.748083292 6459895.28669172,922167.794421628 6459884.98972536,922172.433821726 6459874.50543365,922176.659310902 6459863.84786371,922180.464250537 6459853.0350561,922183.843075151 6459842.0820449,922186.790536147 6459831.00585964,922189.302084233 6459819.82352382,922191.373969324 6459808.55205409,922193.00304074 6459797.20846192,922194.186855697 6459785.81075178,922194.923553636 6459774.374925,922195.211999073 6459762.91997387,922195.05173865 6459751.46288668,922194.443009723 6459740.01964669,922193.386874625 6459728.60922731,922191.884878013 6459717.24859965,922189.939172537 6459705.95572865,922187.552892676 6459694.74657266,922184.7295811 6459683.6380856,922181.473596865 6459672.64921253,922177.78985548 6459661.7938983,922173.683914811 6459651.09107768,922169.162097565 6459640.5556825,922164.231343036 6459630.20463747,922158.89907284 6459620.05286499,922153.173416486 6459610.11628044,922147.063094297 6459600.40979505,922140.577426002 6459590.94831489,922139.398009804 6459589.3099722))), 1, 69DREAL20090005, APPROUVE, 2017/02/08]",
            Arrays.toString(row)
        );
    }

    private void clearCache(File srcFile) throws IOException {
        File csvDirectory = CompanionFileUtils.getCompanionFile(srcFile, MultiTableReader.TMP_EXTENSION);
        if (csvDirectory.exists()) {
            FileUtils.forceDelete(csvDirectory);
        }
    }

}
