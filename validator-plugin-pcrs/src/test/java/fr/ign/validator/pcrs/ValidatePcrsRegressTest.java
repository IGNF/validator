package fr.ign.validator.pcrs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.database.Database;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ErrorLevel;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.io.JsonModelReader;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.plugin.PluginManager;
import fr.ign.validator.report.InMemoryReportBuilder;
import fr.ign.validator.tools.FileConverter;
import fr.ign.validator.tools.Networking;
import fr.ign.validator.tools.ResourceHelper;

/**
 *
 * Regress test with sample document
 *
 * @author MBorne
 *
 */
public class ValidatePcrsRegressTest {

    protected Context context;
    protected InMemoryReportBuilder report;
    protected DocumentModel documentModel;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() {
        context = new Context();

        /* create report */
        report = new InMemoryReportBuilder();
        context.setReportBuilder(report);
        context.setNormalizeEnabled(true);

        /* enable plugin */
        PluginManager pluginManager = new PluginManager();
        pluginManager.getPluginByName(PcrsPlugin.NAME).setup(context);

        /* load document model */
        {
            JsonModelReader modelReader = new JsonModelReader();
            documentModel = modelReader.loadDocumentModel(
                ResourceHelper.getResourceFile(getClass(), "/config-json/CNIG_PCRS_v2.0/document.json")
            );
        }

        /* to access XML schema */
        Networking.configureHttpClient();

        /* reset GMLAS_CONFIG */
        FileConverter.getInstance().setGmlasConfig(null);
    }

    @Test
    public void testJeuxTest() throws Exception {
        File documentPath = getSampleDocument("pcrs-jeux-test");

        Document document = new Document(documentModel, documentPath);
        File validationDirectory = new File(documentPath.getParentFile(), "validation");
        validationDirectory.mkdirs();
        context.setValidationDirectory(validationDirectory);

        document.validate(context);

        assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
        assertEquals(1, report.getErrorsByLevel(ErrorLevel.ERROR).size());

        assertEquals(0, report.getErrorsByCode(CoreErrorCodes.MULTITABLE_UNEXPECTED).size());
        assertEquals(0, report.getErrorsByLevel(ErrorLevel.WARNING).size());

        /*
         * Ensure that validation database is correctly loaded
         */
        Database database = Database.createDatabase(context, false);
        assertEquals(1, database.getCount("EmpriseEchangePCRS"));
        assertEquals(2916, database.getCount("AffleurantPCRS"));
    }

    @Test
    public void testJeuxTestFixed() throws Exception {
        File documentPath = getSampleDocument("pcrs-jeux-test-fixed");

        Document document = new Document(documentModel, documentPath);
        File validationDirectory = new File(documentPath.getParentFile(), "validation");
        validationDirectory.mkdirs();
        context.setValidationDirectory(validationDirectory);

        document.validate(context);

        assertEquals(0, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID).size());
        assertEquals(0, report.getErrorsByLevel(ErrorLevel.ERROR).size());

        assertEquals(0, report.getErrorsByCode(CoreErrorCodes.MULTITABLE_UNEXPECTED).size());
        assertEquals(0, report.getErrorsByLevel(ErrorLevel.WARNING).size());

        /*
         * Ensure that validation database is correctly loaded
         */
        Database database = Database.createDatabase(context, false);
        assertEquals(1, database.getCount("EmpriseEchangePCRS"));
        assertEquals(2916, database.getCount("AffleurantPCRS"));
    }

    @Test
    public void testLyon01() throws Exception {
        File documentPath = getSampleDocument("pcrs-lyon-01");

        Document document = new Document(documentModel, documentPath);
        File validationDirectory = new File(documentPath.getParentFile(), "validation");
        validationDirectory.mkdirs();
        context.setValidationDirectory(validationDirectory);

        document.validate(context);

        /*
         * WKT with CURVEPOLYGON is not supported by current JTS version.
         */
        assertEquals(0, report.getErrorsByLevel(ErrorLevel.ERROR).size());

        assertEquals(0, report.getErrorsByCode(CoreErrorCodes.FILE_UNEXPECTED).size());
        assertEquals(0, report.getErrorsByCode(CoreErrorCodes.DIRECTORY_UNEXPECTED).size());
        assertEquals(0, report.getErrorsByCode(CoreErrorCodes.MULTITABLE_UNEXPECTED).size());
        assertEquals(0, report.getErrorsByLevel(ErrorLevel.WARNING).size());

        /*
         * Ensure that validation database is correctly loaded
         */
        Database database = Database.createDatabase(context, false);
        assertEquals(1, database.getCount("EmpriseEchangePCRS"));
        assertEquals(25, database.getCount("AffleurantPCRS"));
    }

    @Test
    public void testPcrsGeovendee() throws Exception {
        File documentPath = getSampleDocument("pcrs-geovendee");

        Document document = new Document(documentModel, documentPath);
        File validationDirectory = new File(documentPath.getParentFile(), "validation");
        validationDirectory.mkdirs();
        context.setValidationDirectory(validationDirectory);

        document.validate(context);

        /*
         * WKT with CURVEPOLYGON is not supported by current JTS version.
         */
        assertEquals(2, report.getErrorsByLevel(ErrorLevel.ERROR).size());
        {
            List<ValidatorError> errors = report.getErrorsByCode(CoreErrorCodes.XSD_SCHEMA_ERROR);
            assertEquals(2, errors.size());
            // check first XSD_SCHEMA_ERROR
            {
                ValidatorError error = errors.get(0);
                assertEquals("cvc-enumeration-valid", error.getXsdErrorCode());
                assertEquals(
                    "//PlanCorpsRueSimplifie/featureMember/MurPCRS[@id='MurPCRS_f94af702-4ca2-4f06-afaf-0ecabb72a13b']/typeMur",
                    error.getXsdErrorPath()
                );
                assertTrue(
                    "Unexpected message : " + error.getXsdErrorMessage(),
                    error.getXsdErrorMessage().contains("'07'")
                );
            }
            // check second XSD_SCHEMA_ERROR
            {
                ValidatorError error = errors.get(1);
                assertEquals("cvc-type.3.1.3", error.getXsdErrorCode());
                assertEquals(
                    "//PlanCorpsRueSimplifie/featureMember/MurPCRS[@id='MurPCRS_f94af702-4ca2-4f06-afaf-0ecabb72a13b']/typeMur",
                    error.getXsdErrorPath()
                );
                assertTrue(
                    "Unexpected message : " + error.getXsdErrorMessage(),
                    error.getXsdErrorMessage().contains("'typeMur")
                );
            }
        }

        assertEquals(0, report.getErrorsByCode(CoreErrorCodes.FILE_UNEXPECTED).size());
        assertEquals(0, report.getErrorsByCode(CoreErrorCodes.DIRECTORY_UNEXPECTED).size());
        assertEquals(0, report.getErrorsByCode(CoreErrorCodes.MULTITABLE_UNEXPECTED).size());
        assertEquals(0, report.getErrorsByLevel(ErrorLevel.WARNING).size());

        /*
         * Ensure that validation database is correctly loaded
         */
        Database database = Database.createDatabase(context, false);
        assertEquals(1, database.getCount("MurPCRS"));
    }

    /**
     * Get sample document copied on
     * 
     * @param documentName
     * @return
     * @throws IOException
     */
    public File getSampleDocument(String documentName) throws IOException {
        File sourcePath = ResourceHelper.getResourceFile(
            ValidatePcrsRegressTest.class,
            "/documents/" + documentName
        );
        File documentPath = folder.newFolder(documentName);
        FileUtils.copyDirectory(sourcePath, documentPath);
        return documentPath;
    }
}
