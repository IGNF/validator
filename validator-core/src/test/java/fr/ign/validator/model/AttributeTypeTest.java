package fr.ign.validator.model;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.ign.validator.data.Attribute;
import fr.ign.validator.io.json.ObjectMapperFactory;
import fr.ign.validator.model.file.TableModel;
import fr.ign.validator.model.type.StringType;

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
     * Test jackson's config
     * 
     * @throws IOException
     */
    @Test
    public void testJsonIO() throws IOException {
        AttributeType<?> attribute = new StringType();
        attribute.setName("TEST");
        attribute.setDescription("Test description");

        // save to JSON
        ObjectMapper mapper = ObjectMapperFactory.createObjectMapper();
        String result = mapper.writeValueAsString(attribute);
        assertEquals(
            "{\"type\":\"String\",\"name\":\"TEST\",\"description\":\"Test description\",\"constraints\":{\"required\":true,\"unique\":false}}",
            result
        );

        // read from JSON
        AttributeType<?> newAttribute = mapper.readValue(result, AttributeType.class);
        assertEquals(attribute.getName(), newAttribute.getName());
        assertEquals(attribute.getDescription(), newAttribute.getDescription());
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
