package fr.ign.validator.model.type;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.ign.validator.Context;
import fr.ign.validator.model.AttributeConstraints;
import fr.ign.validator.report.InMemoryReportBuilder;
import static org.junit.Assert.*;

/**
 * 
 * @author CBouche
 *
 */
public class StringTypeTest extends AbstractTypeTest<String> {

    public StringTypeTest() {
        super(new StringType());
    }

    @Before
    public void setUp() throws Exception {
        type = new StringType();
        type.setName("test");

        AttributeConstraints constraints = type.getConstraints();
        List<String> enumValues = new ArrayList<String>();
        enumValues.add("aa");
        enumValues.add("b");
        enumValues.add("cccCCC");
        enumValues.add("96541");
        enumValues.add("9Cou");
        constraints.getEnumValues(enumValues);

        constraints.setPattern("(aa|b|bb|cccCCC|[0-9]{5})");

        context = new Context();
        reportBuilder = new InMemoryReportBuilder();
        context.setReportBuilder(reportBuilder);
    }

    @After
    public void tearDown() throws Exception {
        type = null;
    }

    @Test
    public void testCheckAttributeValue0() {
        // all is ok
        bindValidate(context, "b");
        assertTrue(reportBuilder.isValid());
    }

    @Test
    public void testCheckAttributeValue1() {
        // all is ok
        bindValidate(context, "aa");
        assertTrue(reportBuilder.isValid());
    }

    @Test
    public void testCheckAttributeValue2() {
        // too long
        bindValidate(context, "cccCCC");
        assertTrue(reportBuilder.isValid());
    }

    @Test
    public void testCheckAttributeValue3() {
        // doesn't belong to list of values
        bindValidate(context, "bb");
        assertFalse(reportBuilder.isValid());
    }

    @Test
    public void testCheckAttributeValue4() {
        // all is ok
        bindValidate(context, "96541");
        assertTrue(reportBuilder.isValid());
    }

    @Test
    public void testCheckAttributeValue5() {
        // doesn't match regexp
        bindValidate(context, "9Cou");
        assertFalse(reportBuilder.isValid());
    }

}
