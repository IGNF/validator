package fr.ign.validator.model;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import fr.ign.validator.data.Attribute;

public class AttributeTypeTest {

    @Test
    public void testByNameString() {
        AttributeType<?> type = AttributeType.forName("String");
        Assert.assertNotNull(type);
        Assert.assertEquals("String", type.getTypeName());
    }

    @Test
    public void testConsistencyForNameGetTypeName() {
        AttributeTypeFactory factory = AttributeTypeFactory.getInstance();
        Collection<String> names = factory.getTypeNames();
        Assert.assertFalse(names.isEmpty());
        for (String name : names) {
            AttributeType<?> type = factory.createAttributeTypeByName(name);
            Assert.assertEquals(name, type.getTypeName());
        }
    }

    /**
     * Ensures each type behaves properly when binding null value
     */
    @Test
    public void testBindNull() {
        AttributeTypeFactory factory = AttributeTypeFactory.getInstance();
        Collection<String> names = factory.getTypeNames();
        Assert.assertFalse(names.isEmpty());
        for (String name : names) {
            AttributeType<?> type = factory.createAttributeTypeByName(name);
            Assert.assertNull(type.bind(null));
        }
    }

    /**
     * Ensures creation of new attribute of null value works
     */
    @Test
    public void testNewAttributeNull() {
        AttributeTypeFactory factory = AttributeTypeFactory.getInstance();
        Collection<String> names = factory.getTypeNames();
        Assert.assertFalse(names.isEmpty());
        for (String name : names) {
            AttributeType<?> type = factory.createAttributeTypeByName(name);
            Attribute<?> attribute = type.newAttribute(null);
            Assert.assertSame(type, attribute.getType());
            Assert.assertNull(attribute.getBindedValue());
        }
    }

    /**
     * Ensures that format for null is NULL
     */
    @Test
    public void testFormatNull() {
        AttributeTypeFactory factory = AttributeTypeFactory.getInstance();
        Collection<String> names = factory.getTypeNames();
        Assert.assertFalse(names.isEmpty());
        for (String name : names) {
            AttributeType<?> type = factory.createAttributeTypeByName(name);
            Assert.assertNull(type.format(null));
        }
    }

}
