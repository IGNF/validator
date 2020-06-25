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

public class AzimuthValidatorTest {

    public static final Logger log = LogManager.getRootLogger();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    protected Context context;

    protected InMemoryReportBuilder report;

    @Before
    public void setUp() {
        report = new InMemoryReportBuilder();
        context = new Context();
        context.setReportBuilder(report);
    }

    @Test
    public void testValidate() throws Exception {
        // le csv
        String[] header = {
            "VITESSE", "DEBLIN"
        };
        String[] values = {
            "1.0", "0.0"
        };

        // le modele
        DoubleType doubleTypeAzimuth = new DoubleType();
        doubleTypeAzimuth.setName("AZIMUTH");

        DoubleType doubleTypeVitesse = new DoubleType();
        doubleTypeVitesse.setName("VITESSE");

        DoubleType doubleTypeDeblin = new DoubleType();
        doubleTypeDeblin.setName("DEBLIN");

        FeatureType featureType = new FeatureType();
        featureType.addAttribute(doubleTypeAzimuth);
        featureType.addAttribute(doubleTypeVitesse);
        featureType.addAttribute(doubleTypeDeblin);

        FeatureTypeMapper mapping = new FeatureTypeMapper(header, featureType);

        // la ligne
        Row row = new Row(0, values, mapping);
        context.beginData(row);

        // le test
        AzimuthValidator azimuthValidator = new AzimuthValidator();
        Attribute<Double> attribute = new Attribute<>(doubleTypeAzimuth, 15.0);
        azimuthValidator.validate(context, attribute);

        context.beginData(row);

        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testError() throws Exception {
        // le csv
        String[] header = {
            "VITESSE", "DEBLIN"
        };
        String[] values = {
            "1.0", "0.0"
        };

        // le modele
        DoubleType doubleTypeAzimuth = new DoubleType();
        doubleTypeAzimuth.setName("AZIMUTH");

        DoubleType doubleTypeVitesse = new DoubleType();
        doubleTypeVitesse.setName("VITESSE");

        DoubleType doubleTypeDeblin = new DoubleType();
        doubleTypeDeblin.setName("DEBLIN");

        FeatureType featureType = new FeatureType();
        featureType.addAttribute(doubleTypeAzimuth);
        featureType.addAttribute(doubleTypeVitesse);
        featureType.addAttribute(doubleTypeDeblin);

        FeatureTypeMapper mapping = new FeatureTypeMapper(header, featureType);

        // la ligne
        Row row = new Row(0, values, mapping);
        context.beginData(row);

        // le test
        AzimuthValidator azimuthValidator = new AzimuthValidator();
        Attribute<Double> attribute = new Attribute<>(doubleTypeAzimuth, null);
        azimuthValidator.validate(context, attribute);

        context.beginData(row);

        Assert.assertEquals(1, report.countErrors());
        Assert.assertEquals(
            "La vitesse (1.0) et/ou le débit linéique (0.0) est renseigné, mais l’azimuth est null.", report
                .getErrorsByCode(DgprErrorCodes.DGPR_AZIMUTH_ERROR).get(0).getMessage()
        );
    }

    @Test
    public void testAllNull() throws Exception {
        // le csv
        String[] header = {
            "VITESSE", "DEBLIN"
        };
        String[] values = {
            null, null
        };

        // le modele
        DoubleType doubleTypeAzimuth = new DoubleType();
        doubleTypeAzimuth.setName("AZIMUTH");

        DoubleType doubleTypeVitesse = new DoubleType();
        doubleTypeVitesse.setName("VITESSE");

        DoubleType doubleTypeDeblin = new DoubleType();
        doubleTypeDeblin.setName("DEBLIN");

        FeatureType featureType = new FeatureType();
        featureType.addAttribute(doubleTypeAzimuth);
        featureType.addAttribute(doubleTypeVitesse);
        featureType.addAttribute(doubleTypeDeblin);

        FeatureTypeMapper mapping = new FeatureTypeMapper(header, featureType);

        // la ligne
        Row row = new Row(0, values, mapping);
        context.beginData(row);

        // le test
        AzimuthValidator azimuthValidator = new AzimuthValidator();
        Attribute<Double> attribute = new Attribute<>(doubleTypeAzimuth, null);
        azimuthValidator.validate(context, attribute);

        context.beginData(row);

        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testDebSetAzimNul() throws Exception {
        // le csv
        String[] header = {
            "VITESSE", "DEBLIN"
        };
        String[] values = {
            null, "2.0"
        };

        // le modele
        DoubleType doubleTypeAzimuth = new DoubleType();
        doubleTypeAzimuth.setName("AZIMUTH");

        DoubleType doubleTypeVitesse = new DoubleType();
        doubleTypeVitesse.setName("VITESSE");

        DoubleType doubleTypeDeblin = new DoubleType();
        doubleTypeDeblin.setName("DEBLIN");

        FeatureType featureType = new FeatureType();
        featureType.addAttribute(doubleTypeAzimuth);
        featureType.addAttribute(doubleTypeVitesse);
        featureType.addAttribute(doubleTypeDeblin);

        FeatureTypeMapper mapping = new FeatureTypeMapper(header, featureType);

        // la ligne
        Row row = new Row(0, values, mapping);
        context.beginData(row);

        // le test
        AzimuthValidator azimuthValidator = new AzimuthValidator();
        Attribute<Double> attribute = new Attribute<>(doubleTypeAzimuth, null);
        azimuthValidator.validate(context, attribute);

        context.beginData(row);

        Assert.assertEquals(1, report.countErrors());
        Assert.assertEquals(
            "La vitesse (NULL) et/ou le débit linéique (2.0) est renseigné, mais l’azimuth est null.", report
                .getErrorsByCode(DgprErrorCodes.DGPR_AZIMUTH_ERROR).get(0).getMessage()
        );
    }

    @Test
    public void testVitSetAzimNull() throws Exception {
        // le csv
        String[] header = {
            "VITESSE", "DEBLIN"
        };
        String[] values = {
            "10.0", null
        };

        // le modele
        DoubleType doubleTypeAzimuth = new DoubleType();
        doubleTypeAzimuth.setName("AZIMUTH");

        DoubleType doubleTypeVitesse = new DoubleType();
        doubleTypeVitesse.setName("VITESSE");

        DoubleType doubleTypeDeblin = new DoubleType();
        doubleTypeDeblin.setName("DEBLIN");

        FeatureType featureType = new FeatureType();
        featureType.addAttribute(doubleTypeAzimuth);
        featureType.addAttribute(doubleTypeVitesse);
        featureType.addAttribute(doubleTypeDeblin);

        FeatureTypeMapper mapping = new FeatureTypeMapper(header, featureType);

        // la ligne
        Row row = new Row(0, values, mapping);
        context.beginData(row);

        // le test
        AzimuthValidator azimuthValidator = new AzimuthValidator();
        Attribute<Double> attribute = new Attribute<>(doubleTypeAzimuth, null);
        azimuthValidator.validate(context, attribute);

        context.beginData(row);

        Assert.assertEquals(1, report.countErrors());
        Assert.assertEquals(
            "La vitesse (10.0) et/ou le débit linéique (NULL) est renseigné, mais l’azimuth est null.", report
                .getErrorsByCode(DgprErrorCodes.DGPR_AZIMUTH_ERROR).get(0).getMessage()
        );
    }

    @Test
    public void testDebVitNullAzimSet() throws Exception {
        // le csv
        String[] header = {
            "VITESSE", "DEBLIN"
        };
        String[] values = {
            null, null
        };

        // le modele
        DoubleType doubleTypeAzimuth = new DoubleType();
        doubleTypeAzimuth.setName("AZIMUTH");

        DoubleType doubleTypeVitesse = new DoubleType();
        doubleTypeVitesse.setName("VITESSE");

        DoubleType doubleTypeDeblin = new DoubleType();
        doubleTypeDeblin.setName("DEBLIN");

        FeatureType featureType = new FeatureType();
        featureType.addAttribute(doubleTypeAzimuth);
        featureType.addAttribute(doubleTypeVitesse);
        featureType.addAttribute(doubleTypeDeblin);

        FeatureTypeMapper mapping = new FeatureTypeMapper(header, featureType);

        // la ligne
        Row row = new Row(0, values, mapping);
        context.beginData(row);

        // le test
        AzimuthValidator azimuthValidator = new AzimuthValidator();
        Attribute<Double> attribute = new Attribute<>(doubleTypeAzimuth, 15.0);
        azimuthValidator.validate(context, attribute);

        context.beginData(row);

        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testNoDebAzimNull() throws Exception {
        // le csv
        String[] header = {
            "VITESSE"
        };
        String[] values = {
            "2.0"
        };

        // le modele
        DoubleType doubleTypeAzimuth = new DoubleType();
        doubleTypeAzimuth.setName("AZIMUTH");

        DoubleType doubleTypeVitesse = new DoubleType();
        doubleTypeVitesse.setName("VITESSE");

        DoubleType doubleTypeDeblin = new DoubleType();
        doubleTypeDeblin.setName("DEBLIN");

        FeatureType featureType = new FeatureType();
        featureType.addAttribute(doubleTypeAzimuth);
        featureType.addAttribute(doubleTypeVitesse);
        featureType.addAttribute(doubleTypeDeblin);

        FeatureTypeMapper mapping = new FeatureTypeMapper(header, featureType);

        // la ligne
        Row row = new Row(0, values, mapping);
        context.beginData(row);

        // le test
        AzimuthValidator azimuthValidator = new AzimuthValidator();
        Attribute<Double> attribute = new Attribute<>(doubleTypeAzimuth, null);
        azimuthValidator.validate(context, attribute);

        context.beginData(row);

        Assert.assertEquals(1, report.countErrors());
        Assert.assertEquals(
            "La vitesse (2.0) et/ou le débit linéique (NULL) est renseigné, mais l’azimuth est null.", report
                .getErrorsByCode(DgprErrorCodes.DGPR_AZIMUTH_ERROR).get(0).getMessage()
        );
    }

    @Test
    public void testNoVitAzimNull() throws Exception {
        // le csv
        String[] header = {
            "DEBLIN"
        };
        String[] values = {
            "2.0"
        };

        // le modele
        DoubleType doubleTypeAzimuth = new DoubleType();
        doubleTypeAzimuth.setName("AZIMUTH");

        DoubleType doubleTypeVitesse = new DoubleType();
        doubleTypeVitesse.setName("VITESSE");

        DoubleType doubleTypeDeblin = new DoubleType();
        doubleTypeDeblin.setName("DEBLIN");

        FeatureType featureType = new FeatureType();
        featureType.addAttribute(doubleTypeAzimuth);
        featureType.addAttribute(doubleTypeVitesse);
        featureType.addAttribute(doubleTypeDeblin);

        FeatureTypeMapper mapping = new FeatureTypeMapper(header, featureType);

        // la ligne
        Row row = new Row(0, values, mapping);
        context.beginData(row);

        // le test
        AzimuthValidator azimuthValidator = new AzimuthValidator();
        Attribute<Double> attribute = new Attribute<>(doubleTypeAzimuth, null);
        azimuthValidator.validate(context, attribute);

        context.beginData(row);

        Assert.assertEquals(1, report.countErrors());
        Assert.assertEquals(
            "La vitesse (NULL) et/ou le débit linéique (2.0) est renseigné, mais l’azimuth est null.", report
                .getErrorsByCode(DgprErrorCodes.DGPR_AZIMUTH_ERROR).get(0).getMessage()
        );
    }

    @Test
    public void testNoVitDebAzimNull() throws Exception {
        // le csv
        String[] header = {};
        String[] values = {};

        // le modele
        DoubleType doubleTypeAzimuth = new DoubleType();
        doubleTypeAzimuth.setName("AZIMUTH");

        DoubleType doubleTypeVitesse = new DoubleType();
        doubleTypeVitesse.setName("VITESSE");

        DoubleType doubleTypeDeblin = new DoubleType();
        doubleTypeDeblin.setName("DEBLIN");

        FeatureType featureType = new FeatureType();
        featureType.addAttribute(doubleTypeAzimuth);
        featureType.addAttribute(doubleTypeVitesse);
        featureType.addAttribute(doubleTypeDeblin);

        FeatureTypeMapper mapping = new FeatureTypeMapper(header, featureType);

        // la ligne
        Row row = new Row(0, values, mapping);
        context.beginData(row);

        // le test
        AzimuthValidator azimuthValidator = new AzimuthValidator();
        Attribute<Double> attribute = new Attribute<>(doubleTypeAzimuth, null);
        azimuthValidator.validate(context, attribute);

        context.beginData(row);

        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testNoVitDebAzimSet() throws Exception {
        // le csv
        String[] header = {};
        String[] values = {};

        // le modele
        DoubleType doubleTypeAzimuth = new DoubleType();
        doubleTypeAzimuth.setName("AZIMUTH");

        DoubleType doubleTypeVitesse = new DoubleType();
        doubleTypeVitesse.setName("VITESSE");

        DoubleType doubleTypeDeblin = new DoubleType();
        doubleTypeDeblin.setName("DEBLIN");

        FeatureType featureType = new FeatureType();
        featureType.addAttribute(doubleTypeAzimuth);
        featureType.addAttribute(doubleTypeVitesse);
        featureType.addAttribute(doubleTypeDeblin);

        FeatureTypeMapper mapping = new FeatureTypeMapper(header, featureType);

        // la ligne
        Row row = new Row(0, values, mapping);
        context.beginData(row);

        // le test
        AzimuthValidator azimuthValidator = new AzimuthValidator();
        Attribute<Double> attribute = new Attribute<>(doubleTypeAzimuth, 12.5);
        azimuthValidator.validate(context, attribute);

        context.beginData(row);

        Assert.assertEquals(0, report.countErrors());
    }
}
