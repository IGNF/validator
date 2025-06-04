package fr.ign.validator.validation.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.Context;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ErrorLevel;
import fr.ign.validator.error.ErrorScope;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.model.Projection;
import fr.ign.validator.report.InMemoryReportBuilder;
import fr.ign.validator.tools.Networking;
import fr.ign.validator.tools.ResourceHelper;

/**
 * Regress test for {{@link XsdSchemaValidator}
 *
 * @author MBorne
 *
 */
public class XsdSchemaValidatorRegressTest {
    protected Context context;
    protected InMemoryReportBuilder report;

    TemporaryFolder folder;

    @Before
    public void setUp() {
        context = new Context();
        report = new InMemoryReportBuilder();
        context.setReportBuilder(report);
        context.setProjection(Projection.CODE_CRS84);

        /* to access XML schema */
        Networking.configureHttpClient();
    }

    /**
     * Empty file
     */
    @Test
    public void testValidateGmlInfoLin() throws MalformedURLException {
        InMemoryReportBuilder report = new InMemoryReportBuilder();
        Context context = new Context();
        context.setReportBuilder(report);

        File xmlFile = ResourceHelper.getResourceFile(getClass(), "/gml/INFO_LIN.gml");
        URL xsdSchema = ResourceHelper.getResourceFile(getClass(), "/gml/xsd/info_lin.xsd").toURI().toURL();
        XsdSchemaValidator validator = new XsdSchemaValidator();
        validator.validate(context, xsdSchema, xmlFile);

        assertEquals(0, report.getErrorsByCode(CoreErrorCodes.XSD_SCHEMA_ERROR).size());
    }

    /**
     * Valid file
     */
    @Test
    public void testValidateGmlInfoPct() throws MalformedURLException {
        InMemoryReportBuilder report = new InMemoryReportBuilder();
        Context context = new Context();
        context.setReportBuilder(report);

        File xmlFile = ResourceHelper.getResourceFile(getClass(), "/gml/INFO_PCT.gml");
        URL xsdSchema = ResourceHelper.getResourceFile(getClass(), "/gml/xsd/info_pct.xsd").toURI().toURL();
        XsdSchemaValidator validator = new XsdSchemaValidator();
        validator.validate(context, xsdSchema, xmlFile);

        assertEquals(0, report.getErrorsByCode(CoreErrorCodes.XSD_SCHEMA_ERROR).size());
    }

    /**
     * Unexpected custom field.
     */
    @Test
    public void testValidateGmlInfoPctInvalid() throws MalformedURLException {
        InMemoryReportBuilder report = new InMemoryReportBuilder();
        Context context = new Context();
        context.setReportBuilder(report);

        File xmlFile = ResourceHelper.getResourceFile(getClass(), "/gml/INFO_PCT-invalid.gml");
        URL xsdSchema = ResourceHelper.getResourceFile(getClass(), "/gml/xsd/info_pct.xsd").toURI().toURL();
        XsdSchemaValidator validator = new XsdSchemaValidator();
        validator.validate(context, xsdSchema, xmlFile);

        assertEquals(1, report.getErrorsByCode(CoreErrorCodes.XSD_SCHEMA_ERROR).size());
        {
            ValidatorError error = report.getErrorsByCode(CoreErrorCodes.XSD_SCHEMA_ERROR).get(0);
            assertEquals(ErrorLevel.ERROR, error.getLevel());
            assertEquals(ErrorScope.DIRECTORY, error.getScope());
            // check line number
            assertEquals("26", error.getId());
            assertEquals("cvc-complex-type.2.4.d", error.getXsdErrorCode());
            // Note that complete message depends on the local
            assertNotNull(error.getXsdErrorMessage());
            assertTrue(
                "unexpected xsdErrorMessage : " + error.getXsdErrorMessage(),
                error.getXsdErrorMessage().contains("'gp-urba:CUSTOM'")
            );
            // check pseudo XPath
            assertEquals(
                "//wfs:FeatureCollection/wfs:member/gp-urba:INFO_PCT[@id='INFO_PCT.356']",
                error.getXsdErrorPath()
            );

            // see https://wiki.xmldation.com/Support/Validator/cvc-complex-type-2-4-d
            assertEquals(
                "Fichier non conforme au schéma XSD",
                error.getMessage()
            );
        }
    }

}
