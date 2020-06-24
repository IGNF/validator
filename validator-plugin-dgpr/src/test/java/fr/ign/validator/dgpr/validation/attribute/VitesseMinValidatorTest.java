package fr.ign.validator.dgpr.validation.attribute;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.data.Row;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.mapping.FeatureTypeMapper;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.type.DoubleType;
import fr.ign.validator.report.InMemoryReportBuilder;

public class VitesseMinValidatorTest {

    public static final Logger log = LogManager.getRootLogger();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    protected Context context;

    protected InMemoryReportBuilder report;

    protected FeatureType featureType;

    protected DoubleType doubleTypeMin;

    private VitesseMinValidator minValidator;

    @Before
    public void setUp() {
        report = new InMemoryReportBuilder();
        context = new Context();
        context.setReportBuilder(report);

        // model setup
        DoubleType doubleTypeMax = new DoubleType();
        doubleTypeMax.setName("VITESS_MAX");

        doubleTypeMin = new DoubleType();
        doubleTypeMin.setName("VITESS_MIN");

        featureType = new FeatureType();
        featureType.addAttribute(doubleTypeMax);
        featureType.addAttribute(doubleTypeMin);

        minValidator = new VitesseMinValidator();
    }

    @Test
    public void testValidate() throws Exception {
        // data
        String[] header = {
            "VITESS_MAX"
        };
        String[] values = {
            "30.0"
        };
        FeatureTypeMapper mapping = new FeatureTypeMapper(header, featureType);
        Row row = new Row(0, values, mapping);

        // validate
        context.beginData(row);
        Attribute<Double> attribute = doubleTypeMin.newAttribute(15.0);
        minValidator.validate(context, attribute);
        context.endData(row);

        // test
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testLegalString() throws Exception {
        // data
        String[] header = {
            "VITESS_MAX"
        };
        String[] values = {
            "30.0"
        };
        FeatureTypeMapper mapping = new FeatureTypeMapper(header, featureType);
        Row row = new Row(0, values, mapping);

        // validate
        context.beginData(row);
        Attribute<Double> attribute = doubleTypeMin.newAttribute("15.0");
        minValidator.validate(context, attribute);
        context.endData(row);

        // test
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testNullMinValue() throws Exception {
        // data
        String[] header = {
            "VITESS_MAX"
        };
        String[] values = {
            "30.0"
        };
        FeatureTypeMapper mapping = new FeatureTypeMapper(header, featureType);
        Row row = new Row(0, values, mapping);

        // validate
        context.beginData(row);
        Attribute<Double> attribute = doubleTypeMin.newAttribute(null);
        minValidator.validate(context, attribute);
        context.endData(row);

        // test
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testNullValidate() throws Exception {
        // data
        String[] header = {
            "VITESS_MAX"
        };
        String[] values = {
            null
        };
        FeatureTypeMapper mapping = new FeatureTypeMapper(header, featureType);
        Row row = new Row(0, values, mapping);

        // validate
        context.beginData(row);
        Attribute<Double> attribute = doubleTypeMin.newAttribute(15.0);
        minValidator.validate(context, attribute);
        context.beginData(row);

        // test
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testValueMaxError() throws Exception {
        // data
        String[] header = {
            "VITESS_MAX"
        };
        String[] values = {
            "30.0"
        };
        FeatureTypeMapper mapping = new FeatureTypeMapper(header, featureType);
        Row row = new Row(0, values, mapping);

        // validate
        context.beginData(row);
        Attribute<Double> attribute = doubleTypeMin.newAttribute(31.0);
        minValidator.validate(context, attribute);
        context.beginData(row);

        // test
        Assert.assertEquals(1, report.countErrors());
        Assert.assertEquals(
            "La vitesse MIN 31.0 est supérieure à la vitesse MAX 30.0.", report.getErrorsByCode(
                DgprErrorCodes.DGPR_VITESSE_MIN_ERROR
            ).get(0).getMessage()
        );
    }

    @Test
    public void testIllegalDoubleFormat() throws Exception {
        // data
        String[] header = {
            "VITESS_MAX"
        };
        String[] values = {
            "30,0"
        };
        FeatureTypeMapper mapping = new FeatureTypeMapper(header, featureType);
        Row row = new Row(0, values, mapping);

        // validate
        context.beginData(row);
        Attribute<Double> attribute = doubleTypeMin.newAttribute("15.0");
        minValidator.validate(context, attribute);
        context.beginData(row);

        // test
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testIllegalDoubleFormat_2() throws Exception {
        // data
        String[] header = {
            "VITESS_MAX"
        };
        String[] values = {
            "30.0"
        };
        FeatureTypeMapper mapping = new FeatureTypeMapper(header, featureType);
        Row row = new Row(0, values, mapping);

        // validate
        context.beginData(row);
        Attribute<Double> attribute = doubleTypeMin.newAttribute("15,0");
        minValidator.validate(context, attribute);
        context.beginData(row);

        // test
        Assert.assertEquals(0, report.countErrors());
    }

}
