package fr.ign.validator.cnig.regress;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import fr.ign.validator.Context;
import fr.ign.validator.cnig.CnigRegressHelper;
import fr.ign.validator.cnig.ReportAssert;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.data.Document;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ErrorLevel;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.geometry.GeometryComplexityThreshold;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.plugin.PluginManager;
import fr.ign.validator.report.InMemoryReportBuilder;
import fr.ign.validator.tools.TableReader;

/**
 * 
 * Regress test for some GpU documents.
 * 
 * @author MBorne
 *
 */
public class CnigValidatorRegressTest {

    public static final Logger log = LogManager.getRootLogger();

    protected InMemoryReportBuilder report;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() {
        report = new InMemoryReportBuilder();
    }

    /**
     * Create validation context
     *
     * @param documentPath
     * @return
     * @throws Exception
     */
    private Context createContext(File documentPath) throws Exception {
        Context context = new Context();
        context.setReportBuilder(report);
        context.setProjection("EPSG:2154");
        File validationDirectory = new File(documentPath.getParentFile(), "validation");
        context.setValidationDirectory(validationDirectory);
        PluginManager pluginManager = new PluginManager();
        pluginManager.getPluginByName("CNIG").setup(context);
        return context;
    }

    /**
     * Test PLU en standard 2014
     *
     * @throws Exception
     */
    @Test
    public void test41175_PLU_20140603() throws Exception {
        /*
         * validate
         */
        DocumentModel documentModel = CnigRegressHelper.getDocumentModel("cnig_PLU_2013");
        File documentPath = CnigRegressHelper.getSampleDocument("41175_PLU_20140603", folder);
        Context context = createContext(documentPath);
        Document document = new Document(documentModel, documentPath);
        document.validate(context);

        /*
         * check basic points
         */
        Assert.assertEquals(StandardCharsets.UTF_8, context.getEncoding());
        Assert.assertEquals("41175_PLU_20140603", document.getDocumentName());

        /*
         * check errors
         */
        ReportAssert.assertCount(1, CoreErrorCodes.ATTRIBUTE_INVALID_REGEXP, report);
        {
            ValidatorError error = report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_INVALID_REGEXP).get(0);
            Assert.assertEquals(
                "DOC_URBA.dbf",
                error.getFile()
            );
            Assert.assertEquals(
                "DATEREF",
                error.getAttribute()
            );
            Assert.assertEquals(
                "La valeur (2011) ne correspond pas à l'expression régulière ([0-9]{8}).",
                error.getMessage()
            );
        }
        ReportAssert.assertCount(1, CoreErrorCodes.METADATA_SPATIALRESOLUTION_INVALID_DENOMINATOR, report);
        ReportAssert.assertCount(1, CoreErrorCodes.METADATA_CHARACTERSET_INVALID, report);
        ReportAssert.assertCount(1, CnigErrorCodes.CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_URI_NOT_FOUND, report);
        ReportAssert.assertCount(4, ErrorLevel.ERROR, report);

        /*
         * check warnings
         */
        ReportAssert.assertCount(3, CoreErrorCodes.METADATA_LOCATOR_PROTOCOL_NOT_FOUND, report);
        ReportAssert.assertCount(3, CoreErrorCodes.METADATA_LOCATOR_NAME_NOT_FOUND, report);
        ReportAssert.assertCount(1, CnigErrorCodes.CNIG_METADATA_KEYWORD_INVALID, report);
        ReportAssert.assertCount(7, ErrorLevel.WARNING, report);

        /*
         * check document-info.json
         */
        File producedInfosCnigPath = getGeneratedDocumentInfos(documentPath);
        File expectedInfosCnigPath = CnigRegressHelper.getExpectedDocumentInfos("41175_PLU_20140603");
        assertEqualsJsonFile(producedInfosCnigPath, expectedInfosCnigPath);
    }

    /**
     * Test CC en standard 2013
     *
     * @throws Exception
     */
    @Test
    public void test50545_CC_20130902() throws Exception {
        /*
         * validate
         */
        DocumentModel documentModel = CnigRegressHelper.getDocumentModel("cnig_CC_2013");
        File documentPath = CnigRegressHelper.getSampleDocument("50545_CC_20130902", folder);
        Context context = createContext(documentPath);
        Document document = new Document(documentModel, documentPath);
        document.validate(context);

        /*
         * check basic points
         */
        Assert.assertEquals(StandardCharsets.UTF_8, context.getEncoding());
        Assert.assertEquals("50545_CC_20130902", document.getDocumentName());

        /*
         * check errors
         */
        ReportAssert.assertCount(1, CnigErrorCodes.CNIG_METADATA_SPECIFICATION_NOT_FOUND, report);
        ReportAssert.assertCount(1, CnigErrorCodes.CNIG_METADATA_REFERENCESYSTEMIDENTIFIER_URI_NOT_FOUND, report);
        ReportAssert.assertCount(2, ErrorLevel.ERROR, report);

        /*
         * check warnings
         */
        ReportAssert.assertCount(3, CoreErrorCodes.METADATA_LOCATOR_NAME_NOT_FOUND, report);
        ReportAssert.assertCount(3, CoreErrorCodes.METADATA_LOCATOR_PROTOCOL_NOT_FOUND, report);
        ReportAssert.assertCount(6, ErrorLevel.WARNING, report);

        /*
         * check some infos
         */
        ReportAssert.assertCount(1, CoreErrorCodes.TABLE_MISSING_NULLABLE_ATTRIBUTE, report);
        {
            ValidatorError error = report.getErrorsByCode(CoreErrorCodes.TABLE_MISSING_NULLABLE_ATTRIBUTE).get(0);
            assertEquals("DATECOG", error.getAttribute());
        }

        /*
         * check document-info.json
         */
        File producedInfosCnigPath = getGeneratedDocumentInfos(documentPath);
        File expectedInfosCnigPath = CnigRegressHelper.getExpectedDocumentInfos("50545_CC_20130902");
        assertEqualsJsonFile(producedInfosCnigPath, expectedInfosCnigPath);
    }

    /**
     * Test CC en standard 2014 avec aucune donnée géographique
     * (CNIG_NO_SPATIAL_DATA)
     *
     * @throws Exception
     */
    @Test
    public void test50545_CC_20140101() throws Exception {
        /*
         * validate
         */
        DocumentModel documentModel = CnigRegressHelper.getDocumentModel("cnig_CC_2014");
        File documentPath = CnigRegressHelper.getSampleDocument("50545_CC_20140101", folder);
        Context context = createContext(documentPath);
        Document document = new Document(documentModel, documentPath);
        document.validate(context);

        /*
         * check basic points
         */
        Assert.assertEquals(StandardCharsets.UTF_8, context.getEncoding());
        Assert.assertEquals("50545_CC_20140101", document.getDocumentName());

        /*
         * check errors
         */
        ReportAssert.assertCount(1, CoreErrorCodes.NO_SPATIAL_DATA, report);
        ReportAssert.assertCount(1, CoreErrorCodes.ATTRIBUTE_UNEXPECTED_NULL, report);
        ReportAssert.assertCount(1, CoreErrorCodes.FILE_MISSING_MANDATORY_DIRECTORY, report);
        ReportAssert.assertCount(1, CoreErrorCodes.FILE_MISSING_MANDATORY, report);
        ReportAssert.assertCount(1, CnigErrorCodes.CNIG_IDURBA_NOT_FOUND, report);
        ReportAssert.assertCount(1, CnigErrorCodes.CNIG_IDURBA_UNEXPECTED, report);
        ReportAssert.assertCount(0, CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_ERROR, report);
        ReportAssert.assertCount(6, ErrorLevel.ERROR, report);

        /*
         * check warnings
         */
        ReportAssert.assertCount(3, CoreErrorCodes.METADATA_LOCATOR_PROTOCOL_NOT_FOUND, report);
        ReportAssert.assertCount(3, CoreErrorCodes.METADATA_LOCATOR_NAME_NOT_FOUND, report);
        ReportAssert.assertCount(0, CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_WARNING, report);
        ReportAssert.assertCount(6, ErrorLevel.WARNING, report);

        /*
         * check document-info.json
         */
        File producedInfosCnigPath = getGeneratedDocumentInfos(documentPath);
        File expectedInfosCnigPath = CnigRegressHelper.getExpectedDocumentInfos("50545_CC_20140101");
        assertEqualsJsonFile(producedInfosCnigPath, expectedInfosCnigPath);
    }

    /**
     * Test en standard 2017 (IDURBA change de format)
     *
     * @throws Exception
     */
    @Test
    public void test19182_CC_20150517() throws Exception {
        /*
         * validate
         */
        DocumentModel documentModel = CnigRegressHelper.getDocumentModel("cnig_CC_2017");
        File documentPath = CnigRegressHelper.getSampleDocument("19182_CC_20150517", folder);
        Context context = createContext(documentPath);
        // add Complexity Threshold option
        context.setComplexityThreshold(
            new GeometryComplexityThreshold(
                100, 5, 5, 0.08,
                5000, 4, 4, 0.1
            )
        );

        Document document = new Document(documentModel, documentPath);
        document.validate(context);

        /*
         * check basic points
         */
        Assert.assertEquals(StandardCharsets.ISO_8859_1, context.getEncoding());
        Assert.assertEquals("19182_CC_20150517", document.getDocumentName());

        /*
         * check errors
         */
        ReportAssert.assertCount(1, CoreErrorCodes.FILE_MISSING_MANDATORY, report);
        ReportAssert.assertCount(2, CoreErrorCodes.ATTRIBUTE_INVALID_REGEXP, report);
        ReportAssert.assertCount(2, CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID, report);
        ReportAssert.assertCount(19, CoreErrorCodes.ATTRIBUTE_UNEXPECTED_VALUE, report);
        ReportAssert.assertCount(0, CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_ERROR, report);
        ReportAssert.assertCount(1 + 2 + 2 + 19, ErrorLevel.ERROR, report);

        /*
         * check warnings
         */
        ReportAssert.assertCount(3, CoreErrorCodes.METADATA_LOCATOR_PROTOCOL_NOT_FOUND, report);
        ReportAssert.assertCount(7, CnigErrorCodes.CNIG_GEOMETRY_COMPLEXITY_WARNING, report);
        ReportAssert.assertCount(3 + 7, ErrorLevel.WARNING, report);

        /*
         * check document-info.json
         */
        File producedInfosCnigPath = getGeneratedDocumentInfos(documentPath);
        File expectedInfosCnigPath = CnigRegressHelper.getExpectedDocumentInfos("19182_CC_20150517");
        assertEqualsJsonFile(producedInfosCnigPath, expectedInfosCnigPath);
    }

    /**
     * Test SUP
     *
     * @throws Exception
     */
    @Test
    public void testSUP_PM3_28() throws Exception {
        /*
         * validate
         */
        DocumentModel documentModel = CnigRegressHelper.getDocumentModel("cnig_SUP_PM3_2013");
        File documentPath = CnigRegressHelper.getSampleDocument("110068012_PM3_28_20161104", folder);
        Context context = createContext(documentPath);
        Document document = new Document(documentModel, documentPath);
        document.validate(context);

        /*
         * check basic points
         */
        Assert.assertEquals(StandardCharsets.UTF_8, context.getEncoding());
        Assert.assertEquals("110068012_PM3_28_20161104", document.getDocumentName());

        /*
         * check errors
         */
        ReportAssert.assertCount(3, CoreErrorCodes.ATTRIBUTE_INVALID_REGEXP, report);
        ReportAssert.assertCount(3, ErrorLevel.ERROR, report);

        /*
         * check warning
         */
        ReportAssert.assertCount(0, ErrorLevel.WARNING, report);

        /*
         * check document-info.json
         */
        File producedInfosCnigPath = getGeneratedDocumentInfos(documentPath);
        File expectedInfosCnigPath = CnigRegressHelper.getExpectedDocumentInfos("110068012_PM3_28_20161104");
        assertEqualsJsonFile(producedInfosCnigPath, expectedInfosCnigPath);
    }

    /**
     * SUP with duplicated values in AC1_ACTE_SUP.dbf (ex :
     * "AC1-172014607-00099077-1", "AC1-172014607-00099077-1")
     *
     * (was previously crashing SQLITE database insertion)
     *
     * @throws Exception
     */
    @Test
    public void test172014607_AC1_2A_20180130() throws Exception {
        /*
         * validate
         */
        DocumentModel documentModel = CnigRegressHelper.getDocumentModel("cnig_SUP_AC1_2016");
        File documentPath = CnigRegressHelper.getSampleDocument("172014607_AC1_2A_20180130", folder);
        Context context = createContext(documentPath);
        Document document = new Document(documentModel, documentPath);
        document.validate(context);

        /*
         * check basic points
         */
        Assert.assertEquals(StandardCharsets.UTF_8, context.getEncoding());
        Assert.assertEquals("172014607_AC1_2A_20180130", document.getDocumentName());

        /*
         * check errors
         * 
         * TODO : fix ATTRIBUTE_INVALID_FORMAT
         */
        ReportAssert.assertCount(104, CoreErrorCodes.ATTRIBUTE_INVALID_FORMAT, report);
        {
            for (ValidatorError error : report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_INVALID_FORMAT)) {
                Assert.assertEquals(
                    "Donnees_geographiques/AC1_SERVITUDE.dbf",
                    error.getFile()
                );
                Assert.assertEquals(
                    "DATEMAJ",
                    error.getAttribute()
                );
            }
        }
        ReportAssert.assertCount(4, CoreErrorCodes.ATTRIBUTE_NOT_UNIQUE, report);
        {
            List<ValidatorError> errors = report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_NOT_UNIQUE);
            Assert.assertEquals(
                2,
                errors.stream().filter(error -> error.getAttribute().equals("IDACTE")).collect(Collectors.toList())
                    .size()
            );
            Assert.assertEquals(
                2,
                errors.stream().filter(error -> error.getAttribute().equals("IDSUP")).collect(Collectors.toList())
                    .size()
            );
        }
        ReportAssert.assertCount(2, CnigErrorCodes.CNIG_SUP_IDASS_NOT_UNIQUE, report);
        {
            List<ValidatorError> errors = report.getErrorsByCode(CnigErrorCodes.CNIG_SUP_IDASS_NOT_UNIQUE);
            int index = 0;
            {
                ValidatorError error = errors.get(index++);
                assertEquals(
                    "IDASS='AC1-172014607-00099077-153' n'est pas unique dans les tables ASSIETTE_SUP_P/L/S",
                    error.getMessage()
                );
            }
            {
                ValidatorError error = errors.get(index++);
                assertEquals(
                    "IDASS='AC1-172014607-00099080-191' n'est pas unique dans les tables ASSIETTE_SUP_P/L/S",
                    error.getMessage()
                );
            }
        }
        ReportAssert.assertCount(2, CnigErrorCodes.CNIG_SUP_IDGEN_NOT_UNIQUE, report);
        {
            List<ValidatorError> errors = report.getErrorsByCode(CnigErrorCodes.CNIG_SUP_IDGEN_NOT_UNIQUE);
            int index = 0;
            {
                ValidatorError error = errors.get(index++);
                assertEquals(
                    "IDGEN='AC1-172014607-00099077' n'est pas unique dans les tables GENERATEUR_SUP_P/L/S",
                    error.getMessage()
                );
            }
            {
                ValidatorError error = errors.get(index++);
                assertEquals(
                    "IDGEN='AC1-172014607-00099080' n'est pas unique dans les tables GENERATEUR_SUP_P/L/S",
                    error.getMessage()
                );
            }
        }
        ReportAssert.assertCount(1, CnigErrorCodes.CNIG_SUP_IDGEN_NOT_FOUND, report);
        {
            List<ValidatorError> errors = report.getErrorsByCode(CnigErrorCodes.CNIG_SUP_IDGEN_NOT_FOUND);
            {
                ValidatorError error = errors.get(0);
                assertEquals(
                    "IDGEN='AC1-172014607-0009076' référencé par l'assiette IDASS='AC1-172014607-0009076-121' n'existe pas dans les tables GENERATEUR_SUP_P/L/S",
                    error.getMessage()
                );
            }
        }

        // ATTRIBUTE_INVALID_REGEXP
        {
            List<ValidatorError> errors = report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_INVALID_REGEXP);
            // http://cnig.gouv.fr/wp-content/uploads/2019/04/190321_Standard_CNIG_SUP.pdf#page=27&zoom=auto,-260,407
            Assert.assertEquals(
                206,
                errors.stream().filter(error -> error.getAttribute().equals("IDASS")).collect(Collectors.toList())
                    .size()
            );
            // http://cnig.gouv.fr/wp-content/uploads/2019/04/190321_Standard_CNIG_SUP.pdf#page=27&zoom=auto,-260,407
            Assert.assertEquals(
                310,
                errors.stream().filter(error -> error.getAttribute().equals("IDGEN")).collect(Collectors.toList())
                    .size()
            );
            // http://cnig.gouv.fr/wp-content/uploads/2019/04/190321_Standard_CNIG_SUP.pdf#page=30&zoom=auto,-260,406
            Assert.assertEquals(
                206,
                errors.stream().filter(error -> error.getAttribute().equals("NOMASS")).collect(Collectors.toList())
                    .size()
            );
            // http://cnig.gouv.fr/wp-content/uploads/2019/04/190321_Standard_CNIG_SUP.pdf#page=30&zoom=auto,-260,406
            Assert.assertEquals(
                104,
                errors.stream().filter(error -> error.getAttribute().equals("NOMSUP")).collect(Collectors.toList())
                    .size()
            );
        }
        ReportAssert.assertCount(206 + 310 + 206 + 104, CoreErrorCodes.ATTRIBUTE_INVALID_REGEXP, report);

        // ATTRIBUTE_UNEXPECTED_VALUE
        {
            List<ValidatorError> errors = report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_UNEXPECTED_VALUE);
            /*
             * ex : MODEGEOASS - La valeur renseignée (Egal au générateur) ne correspond pas
             * à une valeur autorisée (Égale au générateur, Zone tampon,[...])
             */
            Assert.assertEquals(
                104,
                errors.stream().filter(error -> error.getAttribute().equals("MODEGEOASS")).collect(Collectors.toList())
                    .size()
            );
            /*
             * ex : La valeur renseignée (Périmètre de protection) ne correspond pas à une
             * valeur autorisée (Périmètre des abords, Monument historique).
             */
            Assert.assertEquals(
                206,
                errors.stream().filter(error -> error.getAttribute().equals("TYPEASS")).collect(Collectors.toList())
                    .size()
            );
        }
        ReportAssert.assertCount(104 + 206, CoreErrorCodes.ATTRIBUTE_UNEXPECTED_VALUE, report);

        // check error sum
        ReportAssert.assertCount(104 + 4 + 2 + 2 + 1 + 826 + 310, ErrorLevel.ERROR, report);

        /*
         * check warnings
         */
        ReportAssert.assertCount(1, ErrorLevel.WARNING, report);
        ReportAssert.assertCount(1, CoreErrorCodes.METADATA_LOCATOR_PROTOCOL_NOT_FOUND, report);

        /*
         * check document-info.json
         */
        File producedInfosCnigPath = getGeneratedDocumentInfos(documentPath);
        File expectedInfosCnigPath = CnigRegressHelper.getExpectedDocumentInfos("172014607_AC1_2A_20180130");
        assertEqualsJsonFile(producedInfosCnigPath, expectedInfosCnigPath);

        /* check CSV files */
        File dataDirectory = context.getDataDirectory();
        ac1CheckNormalizedFiles(dataDirectory);
    }

    /**
     * Controls normalized files for SUP
     * 
     * @param dataDirectory
     * @throws IOException
     */
    private void ac1CheckNormalizedFiles(File dataDirectory) throws IOException {
        List<File> csvFiles = new ArrayList<File>(FileUtils.listFiles(dataDirectory, new String[] {
            "csv"
        }, false));
        assertEquals(8, csvFiles.size());
        Collections.sort(csvFiles);
        int index = 0;
        {
            File file = csvFiles.get(index++);
            assertEquals("AC1_ASSIETTE_SUP_S.csv", file.getName());
            TableReader reader = TableReader.createTableReader(file, StandardCharsets.UTF_8);
            int indexFichier = reader.findColumn("fichier");
            int indexNomSupLitt = reader.findColumn("nomsuplitt");
            assertTrue("Column 'fichier' not found!", indexFichier >= 0);
            assertTrue("Column 'nomsuplitt' not found!", indexNomSupLitt >= 0);
            /* check first value */
            String[] row = reader.next();
            assertEquals("AC1_EglisedeSantaMariaFiganiella_19270829_act.pdf", row[indexFichier]);
            assertEquals("Eglise de Santa Maria Figaniella", row[indexNomSupLitt]);
        }
        {
            File file = csvFiles.get(index++);
            assertEquals("AC1_GENERATEUR_SUP_L.csv", file.getName());
            TableReader reader = TableReader.createTableReader(file, StandardCharsets.UTF_8);
            int indexFichier = reader.findColumn("fichier");
            int indexNomSupLitt = reader.findColumn("nomsuplitt");
            assertTrue("Column 'fichier' not found!", indexFichier >= 0);
            assertTrue("Column 'nomsuplitt' not found!", indexNomSupLitt >= 0);
            // header only
            assertFalse(reader.hasNext());
        }
        {
            File file = csvFiles.get(index++);
            assertEquals("AC1_GENERATEUR_SUP_P.csv", file.getName());
            TableReader reader = TableReader.createTableReader(file, StandardCharsets.UTF_8);
            int indexFichier = reader.findColumn("fichier");
            int indexNomSupLitt = reader.findColumn("nomsuplitt");
            assertTrue("Column 'fichier' not found!", indexFichier >= 0);
            assertTrue("Column 'nomsuplitt' not found!", indexNomSupLitt >= 0);
            // header only
            assertFalse(reader.hasNext());
        }
        {
            File file = csvFiles.get(index++);
            assertEquals("AC1_GENERATEUR_SUP_S.csv", file.getName());
            TableReader reader = TableReader.createTableReader(file, StandardCharsets.UTF_8);
            int indexFichier = reader.findColumn("fichier");
            int indexNomSupLitt = reader.findColumn("nomsuplitt");
            assertTrue("Column 'fichier' not found!", indexFichier >= 0);
            assertTrue("Column 'nomsuplitt' not found!", indexNomSupLitt >= 0);
            /* check first value */
            String[] row = reader.next();
            assertEquals("AC1_EglisedeSantaMariaFiganiella_19270829_act.pdf", row[indexFichier]);
            assertEquals("Eglise de Santa Maria Figaniella", row[indexNomSupLitt]);
        }
        {
            File file = csvFiles.get(index++);
            assertEquals("ACTE_SUP.csv", file.getName());
        }
        {
            File file = csvFiles.get(index++);
            assertEquals("GESTIONNAIRE_SUP.csv", file.getName());
        }
        {
            File file = csvFiles.get(index++);
            assertEquals("SERVITUDE.csv", file.getName());
        }
        {
            File file = csvFiles.get(index++);
            assertEquals("SERVITUDE_ACTE_SUP.csv", file.getName());
        }
    }

    /**
     * Test to avoid production of nomsuplitt='null'
     *
     * @throws Exception
     */
    @Test
    public void test213000896_INT1_83044_20160130() throws Exception {
        /*
         * validate
         */
        DocumentModel documentModel = CnigRegressHelper.getDocumentModel("cnig_SUP_INT1_2016");
        File documentPath = CnigRegressHelper.getSampleDocument("213000896_INT1_83044_20160130", folder);
        Context context = createContext(documentPath);
        Document document = new Document(documentModel, documentPath);
        document.validate(context);

        /*
         * check basic points
         */
        Assert.assertEquals(StandardCharsets.UTF_8, context.getEncoding());
        Assert.assertEquals("213000896_INT1_83044_20160130", document.getDocumentName());

        /*
         * check document-info.json
         */
        File producedInfosCnigPath = getGeneratedDocumentInfos(documentPath);
        File expectedInfosCnigPath = CnigRegressHelper.getExpectedDocumentInfos("213000896_INT1_83044_20160130");
        assertEqualsJsonFile(producedInfosCnigPath, expectedInfosCnigPath);

        /* check CSV files */
        File dataDirectory = context.getDataDirectory();

        // ensure that nomsuplitt is null (not "null") in INT1_GENERATEUR_SUP_S
        {
            File generateurSupS = new File(dataDirectory, "INT1_GENERATEUR_SUP_S.csv");
            assertTrue(generateurSupS.exists());
            TableReader reader = TableReader.createTableReader(generateurSupS, StandardCharsets.UTF_8);
            int indexNomSupLitt = reader.findColumnRequired("NOMSUPLITT");
            while (reader.hasNext()) {
                String[] row = reader.next();
                assertNull(row[indexNomSupLitt]);
            }
        }

    }

    /**
     * Test PLU avec coordonnées 3D en standard cnig_PLU_2017
     *
     * @throws Exception
     */
    @Test
    public void test30014_PLU_20171013() throws Exception {
        /*
         * validate
         */
        DocumentModel documentModel = CnigRegressHelper.getDocumentModel("cnig_PLU_2017");
        File documentPath = CnigRegressHelper.getSampleDocument("30014_PLU_20171013", folder);
        Context context = createContext(documentPath);
        Document document = new Document(documentModel, documentPath);
        document.validate(context);

        /*
         * check basic points
         */
        Assert.assertEquals(StandardCharsets.ISO_8859_1, context.getEncoding());
        Assert.assertEquals("30014_PLU_20171013", document.getDocumentName());

        /*
         * check errors
         */
        // YYYYMMDD different in tables
        ReportAssert.assertCount(18, CnigErrorCodes.CNIG_IDURBA_UNEXPECTED, report);
        ReportAssert.assertCount(18, ErrorLevel.ERROR, report);

        /*
         * check warnings
         */
        ReportAssert.assertCount(1, CnigErrorCodes.CNIG_IDURBA_MULTIPLE_FOUND, report);
        ReportAssert.assertCount(1, ErrorLevel.WARNING, report);

        /*
         * check document-info.json
         */
        File producedInfosCnigPath = getGeneratedDocumentInfos(documentPath);
        File expectedInfosCnigPath = CnigRegressHelper.getExpectedDocumentInfos("30014_PLU_20171013");
        assertEqualsJsonFile(producedInfosCnigPath, expectedInfosCnigPath);
    }

    /**
     * Test case sensitivity for IDURBA and "PLUi" 200011781_PLUi_20180101
     */
    @Test
    public void test200011781_PLUi_20180101() throws Exception {
        /*
         * validate
         */
        DocumentModel documentModel = CnigRegressHelper.getDocumentModel("cnig_PLUi_2014");
        File documentPath = CnigRegressHelper.getSampleDocument("200011781_PLUi_20180101", folder);
        Context context = createContext(documentPath);
        Document document = new Document(documentModel, documentPath);
        document.validate(context);

        /*
         * check basic points
         */
        Assert.assertEquals(StandardCharsets.UTF_8, context.getEncoding());
        Assert.assertEquals("200011781_PLUi_20180101", document.getDocumentName());

        /*
         * check errors
         */
        ReportAssert.assertCount(4, CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID, report);
        ReportAssert.assertCount(1, CoreErrorCodes.ATTRIBUTE_INVALID_REGEXP, report);
        ReportAssert.assertCount(5, ErrorLevel.ERROR, report);

        /*
         * check warnings
         */
        ReportAssert.assertCount(0, ErrorLevel.WARNING, report);

        /*
         * check document-info.json
         */
        File producedInfosCnigPath = getGeneratedDocumentInfos(documentPath);
        File expectedInfosCnigPath = CnigRegressHelper.getExpectedDocumentInfos("200011781_PLUi_20180101");
        assertEqualsJsonFile(producedInfosCnigPath, expectedInfosCnigPath);
    }

    /**
     * Test CNIG_DOC_URBA_COM_UNEXPECTED_SIZE with 241800432_PLUi_20200128
     */
    @Test
    public void test241800432_PLUi_20200128() throws Exception {
        /*
         * validate
         */
        DocumentModel documentModel = CnigRegressHelper.getDocumentModel("cnig_PLUi_2017");
        File documentPath = CnigRegressHelper.getSampleDocument("241800432_PLUi_20200128", folder);
        Context context = createContext(documentPath);
        Document document = new Document(documentModel, documentPath);
        document.validate(context);

        /*
         * check basic points
         */
        Assert.assertEquals(StandardCharsets.UTF_8, context.getEncoding());
        Assert.assertEquals("241800432_PLUi_20200128", document.getDocumentName());

        /*
         * check errors
         */
        ReportAssert.assertCount(3, CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID, report);
        ReportAssert.assertCount(2, CoreErrorCodes.ATTRIBUTE_UNEXPECTED_NULL, report);
        ReportAssert.assertCount(5, ErrorLevel.ERROR, report);

        /*
         * check warnings
         */
        ReportAssert.assertCount(1, CnigErrorCodes.CNIG_DOC_URBA_COM_UNEXPECTED_SIZE, report);
        ReportAssert.assertCount(1, ErrorLevel.WARNING, report);

        /*
         * check document-info.json
         */
        File producedInfosCnigPath = getGeneratedDocumentInfos(documentPath);
        File expectedInfosCnigPath = CnigRegressHelper.getExpectedDocumentInfos("241800432_PLUi_20200128");
        assertEqualsJsonFile(producedInfosCnigPath, expectedInfosCnigPath);
    }

    /**
     * Test 200078244_SCOT_20180218 with CNIG_PERIMETRE_SCOT_UNEXPECTED_SIZE error
     */
    @Test
    public void test200078244_SCOT_20180218() throws Exception {
        /*
         * validate
         */
        DocumentModel documentModel = CnigRegressHelper.getDocumentModel("cnig_SCoT_2018");
        File documentPath = CnigRegressHelper.getSampleDocument("200078244_SCOT_20180218", folder);
        Context context = createContext(documentPath);
        Document document = new Document(documentModel, documentPath);
        document.validate(context);

        /*
         * check basic points
         */
        Assert.assertEquals(StandardCharsets.UTF_8, context.getEncoding());
        Assert.assertEquals("200078244_SCOT_20180218", document.getDocumentName());

        /*
         * check errors
         */
        ReportAssert.assertCount(1, CnigErrorCodes.CNIG_PERIMETRE_SCOT_UNEXPECTED_SIZE, report);
        ReportAssert.assertCount(1, ErrorLevel.ERROR, report);

        /*
         * check warnings
         */
        ReportAssert.assertCount(0, CnigErrorCodes.CNIG_DOC_URBA_COM_UNEXPECTED_SIZE, report);
        ReportAssert.assertCount(0, ErrorLevel.WARNING, report);

        /*
         * check document-info.json
         */
        File producedInfosCnigPath = getGeneratedDocumentInfos(documentPath);
        File expectedInfosCnigPath = CnigRegressHelper.getExpectedDocumentInfos("200078244_SCOT_20180218");
        assertEqualsJsonFile(producedInfosCnigPath, expectedInfosCnigPath);
    }

    private void assertEqualsJsonFile(File producedInfosCnigPath, File expectedInfosCnigPath) throws Exception {
        /*
         * switch to true (temporary) to update the regress test (then, review change,
         * refresh eclipse project and comment back)
         */
        boolean updateDocumentInfos = false;
        if (updateDocumentInfos) {
            String originalPath = expectedInfosCnigPath.getAbsolutePath().replaceAll(
                "/target/test-classes/",
                "/src/test/resources/"
            );
            FileUtils.writeStringToFile(
                new File(originalPath),
                FileUtils.readFileToString(
                    producedInfosCnigPath,
                    StandardCharsets.UTF_8
                ),
                StandardCharsets.UTF_8
            );
            fail("restart test switching updateDocumentInfos to false in assertEqualsJsonFile");
        } else {
            String actual = FileUtils.readFileToString(producedInfosCnigPath, StandardCharsets.UTF_8).trim();
            String expected = FileUtils.readFileToString(expectedInfosCnigPath, StandardCharsets.UTF_8).trim();
            JSONAssert.assertEquals(
                expected,
                actual,
                JSONCompareMode.STRICT
            );
        }
    }

    /**
     * Get generated document-info.json file
     *
     * @param documentPath
     * @return
     */
    private File getGeneratedDocumentInfos(File documentPath) {
        File validationDirectory = new File(documentPath.getParentFile(), "validation");
        return new File(validationDirectory, "document-info.json");
    }
}
