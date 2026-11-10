package fr.ign.validator.model.constraint;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.ign.validator.model.FeatureTypeConstraints;

public class ConditionConstraintTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testReadStringAndObjectForms() throws Exception {
        String json = "{\"conditions\": ["
            + "\"A IS NOT NULL OR B IS NULL\","
            + "{\"sql\": \"C IS NOT NULL\", \"message\": \"C est obligatoire\"}"
            + "]}";
        FeatureTypeConstraints constraints = mapper.readValue(json, FeatureTypeConstraints.class);

        Assert.assertEquals(2, constraints.getConditions().size());

        ConditionConstraint plain = constraints.getConditions().get(0);
        Assert.assertEquals("A IS NOT NULL OR B IS NULL", plain.getSql());
        Assert.assertNull(plain.getMessage());
        Assert.assertEquals("A IS NOT NULL OR B IS NULL", plain.getLabel());

        ConditionConstraint withMessage = constraints.getConditions().get(1);
        Assert.assertEquals("C IS NOT NULL", withMessage.getSql());
        Assert.assertEquals("C est obligatoire", withMessage.getLabel());
    }

    @Test
    public void testWriteRoundTrip() throws Exception {
        Assert.assertEquals("\"X IS NULL\"", mapper.writeValueAsString(new ConditionConstraint("X IS NULL")));
        Assert.assertEquals(
            "{\"sql\":\"X IS NULL\",\"message\":\"msg\"}",
            mapper.writeValueAsString(new ConditionConstraint("X IS NULL", "msg"))
        );
    }

}
