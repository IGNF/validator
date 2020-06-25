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
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.model.type.DoubleType;
import fr.ign.validator.report.InMemoryReportBuilder;

public class DebLinValidatorTest {

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
        DoubleType doubleTypeDebLinMin = new DoubleType();
        doubleTypeDebLinMin.setName("DEBLIN");

        // test avec DEBLIN = 15.0
        DebLinValidator deblinValidator = new DebLinValidator();
        Attribute<Double> attribute = new Attribute<>(doubleTypeDebLinMin, 15.0);
        deblinValidator.validate(context, attribute);

        // test avec DEBLIN = null
        Attribute<Double> attribute2 = new Attribute<>(doubleTypeDebLinMin, null);
        deblinValidator.validate(context, attribute2);

        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testError() throws Exception {
        DoubleType doubleTypeDebLinMin = new DoubleType();
        doubleTypeDebLinMin.setName("DEBLIN");

        // test
        DebLinValidator minValidator = new DebLinValidator();
        Attribute<Double> attribute = new Attribute<>(doubleTypeDebLinMin, -10.0);
        minValidator.validate(context, attribute);

        Assert.assertEquals(1, report.countErrors());
        Assert.assertEquals(
            "La valeur DEBLIN (-10.0) doit être non renseignée ou supérieure à 0.", report.getErrorsByCode(
                DgprErrorCodes.DGPR_DEBLIN_ERROR
            ).get(0).getMessage()
        );
    }

}
