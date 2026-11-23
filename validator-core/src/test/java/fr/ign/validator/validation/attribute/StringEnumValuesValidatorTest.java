package fr.ign.validator.validation.attribute;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.ign.validator.Context;
import fr.ign.validator.data.Attribute;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.io.json.ObjectMapperFactory;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.type.StringType;
import fr.ign.validator.report.InMemoryReportBuilder;

public class StringEnumValuesValidatorTest {

    private Context context;

    private InMemoryReportBuilder report;

    @Before
    public void setUp() throws Exception {
        context = new Context();
        report = new InMemoryReportBuilder();
        context.setReportBuilder(report);
    }

    private StringType createType(String separator) {
        StringType type = new StringType();
        type.getConstraints().getEnumValues(Arrays.asList("PROPANE", "BUTANE", "OXYDE D’ETHYLENE"));
        type.getConstraints().setEnumSeparator(separator);
        return type;
    }

    private void validate(StringType type, String value) {
        new StringEnumValuesValidator().validate(context, new Attribute<String>(type, value));
    }

    @Test
    public void testNoEnum() {
        validate(new StringType(), "anything");
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testNull() {
        validate(createType(","), null);
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testSingleValueValid() {
        validate(createType(null), "PROPANE");
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testSingleValueInvalid() {
        validate(createType(null), "PROPANE,BUTANE");
        Assert.assertEquals(1, report.countErrors());
        ValidatorError error = report.getErrors().get(0);
        Assert.assertEquals(CoreErrorCodes.ATTRIBUTE_UNEXPECTED_VALUE, error.getCode());
        Assert.assertEquals(
            "La valeur renseignée (PROPANE,BUTANE) ne correspond pas à une valeur autorisée (PROPANE, BUTANE, OXYDE D’ETHYLENE).",
            error.getMessage()
        );
    }

    @Test
    public void testSeparatorOneValue() {
        validate(createType(","), "OXYDE D’ETHYLENE");
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testSeparatorSeveralValues() {
        validate(createType(","), "PROPANE,BUTANE");
        validate(createType(","), "PROPANE, BUTANE , OXYDE D’ETHYLENE");
        Assert.assertEquals(0, report.countErrors());
    }

    @Test
    public void testSeparatorInvalidValue() {
        validate(createType(","), "PROPANE, ESSENCE");
        Assert.assertEquals(1, report.countErrors());
        Assert.assertEquals(
            "La valeur renseignée (ESSENCE) ne correspond pas à une valeur autorisée (PROPANE, BUTANE, OXYDE D’ETHYLENE).",
            report.getErrors().get(0).getMessage()
        );
    }

    @Test
    public void testSeparatorEmptyParts() {
        validate(createType(","), "PROPANE,,BUTANE,");
        Assert.assertEquals(2, report.countErrors());
        Assert.assertTrue(report.getErrors().get(0).getMessage().startsWith("La valeur renseignée () "));
    }

    @Test
    public void testSeparatorFromJson() throws Exception {
        ObjectMapper mapper = ObjectMapperFactory.createObjectMapper();
        AttributeType<?> type = mapper.readValue(
            "{\"name\":\"des_fluide\",\"type\":\"String\",\"constraints\":{\"enum\":[\"PROPANE\",\"BUTANE\"],\"enumSeparator\":\",\"}}",
            AttributeType.class
        );
        Assert.assertEquals(",", type.getConstraints().getEnumSeparator());
        Assert.assertEquals(Arrays.asList("PROPANE", "BUTANE"), type.getConstraints().getEnumValues());
    }

}
