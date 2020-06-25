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

public class DebLinMaxValidatorTest {

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
            "DEBLIN_MIN"
        };
        String[] values = {
            "1.0"
        };

        // le modele
        DoubleType doubleTypeDebLinMin = new DoubleType();
        doubleTypeDebLinMin.setName("DEBLIN_MIN");

        DoubleType doubleTypeDebLinMax = new DoubleType();
        doubleTypeDebLinMax.setName("DEBLIN_MAX");

        FeatureType featureType = new FeatureType();
        featureType.addAttribute(doubleTypeDebLinMin);
        featureType.addAttribute(doubleTypeDebLinMax);

        FeatureTypeMapper mapping = new FeatureTypeMapper(header, featureType);

        // la ligne
        Row row = new Row(0, values, mapping);
        context.beginData(row);

        // test avec DEBLIN_MAX = 1.5
        DebLinMaxValidator maxValidator = new DebLinMaxValidator();
        Attribute<Double> attribute = new Attribute<>(doubleTypeDebLinMax, 1.5);
        maxValidator.validate(context, attribute);

        // test avec DEBLIN_MAX = null
        DebLinMaxValidator maxValidator2 = new DebLinMaxValidator();
        Attribute<Double> attribute2 = new Attribute<>(doubleTypeDebLinMax, null);
        maxValidator2.validate(context, attribute2);

        context.beginData(row);

        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testValueMaxError() throws Exception {
        // le csv
        String[] header = {
            "DEBLIN_MIN"
        };
        String[] values = {
            "1.0"
        };

        // le modele
        DoubleType doubleTypeDebLinMin = new DoubleType();
        doubleTypeDebLinMin.setName("DEBLIN_MIN");

        DoubleType doubleTypeDebLinMax = new DoubleType();
        doubleTypeDebLinMax.setName("DEBLIN_MAX");

        FeatureType featureType = new FeatureType();
        featureType.addAttribute(doubleTypeDebLinMin);
        featureType.addAttribute(doubleTypeDebLinMax);

        FeatureTypeMapper mapping = new FeatureTypeMapper(header, featureType);

        // la ligne
        Row row = new Row(0, values, mapping);
        context.beginData(row);

        // test
        DebLinMaxValidator minValidator = new DebLinMaxValidator();
        Attribute<Double> attribute = new Attribute<>(doubleTypeDebLinMax, 0.9);
        minValidator.validate(context, attribute);

        context.beginData(row);

        Assert.assertEquals(1, report.countErrors());
        Assert.assertEquals(
            "La valeur DEBLIN_MAX (0.9) doit être nulle ou supérieure à la valeur DEBLIN_MIN (1.0)", report
                .getErrorsByCode(DgprErrorCodes.DGPR_DEBLIN_MAX_ERROR).get(0).getMessage()
        );
    }

    @Test
    public void testNoValueMinError() throws Exception {
        // le csv
        String[] header = {};
        String[] values = {};

        // le modele
        DoubleType doubleTypeDebLinMin = new DoubleType();
        doubleTypeDebLinMin.setName("DEBLIN_MIN");

        DoubleType doubleTypeDebLinMax = new DoubleType();
        doubleTypeDebLinMax.setName("DEBLIN_MAX");

        FeatureType featureType = new FeatureType();
        featureType.addAttribute(doubleTypeDebLinMin);
        featureType.addAttribute(doubleTypeDebLinMax);

        FeatureTypeMapper mapping = new FeatureTypeMapper(header, featureType);

        // la ligne
        Row row = new Row(0, values, mapping);
        context.beginData(row);

        // test
        DebLinMaxValidator minValidator = new DebLinMaxValidator();
        Attribute<Double> attribute = new Attribute<>(doubleTypeDebLinMax, 0.9);
        minValidator.validate(context, attribute);

        context.beginData(row);

        Assert.assertEquals(1, report.countErrors());
        Assert.assertEquals(
            "La valeur DEBLIN_MAX (0.9) doit être nulle ou supérieure à la valeur DEBLIN_MIN (non renseignée)", report
                .getErrorsByCode(DgprErrorCodes.DGPR_DEBLIN_MAX_ERROR).get(0).getMessage()
        );
    }

    @Test
    public void testIllegalDouble() throws Exception {
        // le csv
        String[] header = {
            "DEBLIN_MIN"
        };
        String[] values = {
            "1,5"
        };

        // le modele
        DoubleType doubleTypeDebLinMin = new DoubleType();
        doubleTypeDebLinMin.setName("DEBLIN_MIN");

        DoubleType doubleTypeDebLinMax = new DoubleType();
        doubleTypeDebLinMax.setName("DEBLIN_MAX");

        FeatureType featureType = new FeatureType();
        featureType.addAttribute(doubleTypeDebLinMin);
        featureType.addAttribute(doubleTypeDebLinMax);

        FeatureTypeMapper mapping = new FeatureTypeMapper(header, featureType);

        // la ligne
        Row row = new Row(0, values, mapping);
        context.beginData(row);

        // test avec DEBLIN_MAX = 2.0
        // error no value for deblinmin
        DebLinMaxValidator maxValidator = new DebLinMaxValidator();
        Attribute<Double> attribute = new Attribute<>(doubleTypeDebLinMax, 2.0);
        maxValidator.validate(context, attribute);

        // test avec DEBLIN_MAX = null
        // no error
        DebLinMaxValidator maxValidator2 = new DebLinMaxValidator();
        Attribute<Double> attribute2 = new Attribute<>(doubleTypeDebLinMax, null);
        maxValidator2.validate(context, attribute2);

        context.beginData(row);

        Assert.assertEquals(1, report.countErrors());
    }

}
