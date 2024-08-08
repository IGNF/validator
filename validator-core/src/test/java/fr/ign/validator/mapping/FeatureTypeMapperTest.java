package fr.ign.validator.mapping;

import org.junit.Assert;
import org.junit.Test;

import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.type.BooleanType;

/**
 * Test FeatureTypeMapper
 *
 * @author MBorne
 *
 */
public class FeatureTypeMapperTest {

    @Test
    public void testOneToOne() {
        FeatureType featureType = new FeatureType();
        {
            AttributeType<?> attribute = new BooleanType();
            attribute.setName("A");
            featureType.addAttribute(attribute);
        }
        String[] header = new String[] {
            "A"
        };

        FeatureTypeMapper mapper = FeatureTypeMapper.createMapper(header, featureType);
        Assert.assertTrue(mapper.getMissingAttributes().isEmpty());
        Assert.assertTrue(mapper.getUnexpectedAttributes().isEmpty());
    }

    @Test
    public void testManyToMany() {
        FeatureType featureType = new FeatureType();
        {
            AttributeType<?> attribute = new BooleanType();
            attribute.setName("A");
            featureType.addAttribute(attribute);
        }
        {
            AttributeType<?> attribute = new BooleanType();
            attribute.setName("B");
            featureType.addAttribute(attribute);
        }
        String[] header = new String[] {
            "A", "C"
        };

        FeatureTypeMapper mapper = FeatureTypeMapper.createMapper(header, featureType);

        Assert.assertEquals(1, mapper.getMissingAttributes().size());
        Assert.assertEquals("B", mapper.getMissingAttributes().get(0));

        Assert.assertEquals(1, mapper.getUnexpectedAttributes().size());
        Assert.assertEquals("C", mapper.getUnexpectedAttributes().get(0));
    }

}
