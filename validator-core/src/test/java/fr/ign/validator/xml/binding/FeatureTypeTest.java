package fr.ign.validator.xml.binding;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Strings;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ign.validator.ResourceHelper;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.type.BooleanType;
import fr.ign.validator.model.type.StringType;

/**
 * Test feature type marshalling
 * @author MBorne
 *
 */
public class FeatureTypeTest {

	public static final Logger logger = LogManager.getRootLogger() ;
	
	protected JAXBContext context ;
	protected Unmarshaller unmarshaller ;
	protected Marshaller marshaller ;

	@Before
	public void setUp() throws Exception {
		 context = JAXBContext.newInstance(FeatureType.class) ;
		 unmarshaller = context.createUnmarshaller();
		 marshaller = context.createMarshaller() ;
		 marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
	}
	
	@Test
	public void testMarshallUnmarshallCommune() throws JAXBException, IOException {
		File featureTypePath = ResourceHelper.getResourcePath("/xml/sample-document/types/COMMUNE.xml") ;

		FeatureType featureType = (FeatureType)unmarshaller.unmarshal(featureTypePath);
		Assert.assertNotNull(featureType);
		Assert.assertEquals("COMMUNE",featureType.getName());
		Assert.assertEquals("La table des communes",featureType.getDescription());
		Assert.assertFalse(featureType.isSpatial());
		Assert.assertEquals(3,featureType.getAttributeCount());
		
		int index = 0;
		{
			AttributeType<?> attributeType = featureType.getAttribute(index++);
			Assert.assertEquals("INSEE",attributeType.getName());
			Assert.assertEquals("String",attributeType.getTypeName());
			Assert.assertEquals(false, attributeType.isNullable());
			Assert.assertEquals("[0-9]{5}", attributeType.getRegexp());
			Assert.assertEquals(true, attributeType.isIdentifier());
			Assert.assertNull(attributeType.getListOfValues());
		}
		{
			AttributeType<?> attributeType = featureType.getAttribute(index++);
			Assert.assertEquals("CODE_DEPT",attributeType.getName());
			Assert.assertEquals("String",attributeType.getTypeName());
			Assert.assertEquals(false, attributeType.isNullable());
			Assert.assertNull(attributeType.getRegexp());
			Assert.assertEquals(false, attributeType.isIdentifier());
			Assert.assertNotNull(attributeType.getListOfValues());
			Assert.assertEquals("01,02", Strings.join(attributeType.getListOfValues(),','));
		}
		{
			AttributeType<?> attributeType = featureType.getAttribute(index++);
			Assert.assertEquals("DETRUIT",attributeType.getName());
			Assert.assertEquals("Boolean",attributeType.getTypeName());
			Assert.assertEquals(true, attributeType.isNullable());
			Assert.assertNull(attributeType.getRegexp());
			Assert.assertEquals(false, attributeType.isIdentifier());
			Assert.assertNull(attributeType.getListOfValues());
		}
		
		StringWriter output = new StringWriter() ;
		marshaller.marshal(featureType, output);
		Assert.assertEquals(
			FileUtils.readFileToString(featureTypePath),
			output.toString()
		);
	}
	
	

	@Test
	public void testUnmarshallWithCDATA() throws JAXBException{
		File srcFile = ResourceHelper.getResourcePath("/xml/sample-document/types/SIMPLE.xml") ;

		FeatureType featureType = (FeatureType)unmarshaller.unmarshal(srcFile) ;
		Assert.assertEquals("SIMPLE", featureType.getName()) ;
		Assert.assertEquals("TABLE TEST", featureType.getDescription()) ;			
		Assert.assertEquals(3, featureType.getAttributeCount());
		Assert.assertTrue(featureType.isSpatial());
		
		int index = 0 ;
		{
			AttributeType<?> attribute = featureType.getAttribute(index++) ;
			Assert.assertEquals( "ID", attribute.getName() ) ;
			Assert.assertEquals( "Integer", attribute.getTypeName() ) ;
			Assert.assertNull( attribute.getRegexp() ) ;
			Assert.assertFalse(attribute.isNullable()) ;
			Assert.assertEquals(false, attribute.isIdentifier());
			Assert.assertNull(attribute.getListOfValues());
		}

		{
			AttributeType<?> attribute = featureType.getAttribute(index++) ;
			Assert.assertEquals( "NAME", attribute.getName() ) ;
			Assert.assertEquals( "String", attribute.getTypeName() ) ;
			Assert.assertNull( attribute.getRegexp() ) ;
			Assert.assertFalse(attribute.isNullable()) ;
			Assert.assertEquals(false, attribute.isIdentifier());

			Assert.assertNull(attribute.getListOfValues());
		}
	
		{
			AttributeType<?> attribute = featureType.getAttribute(index++) ;
			Assert.assertEquals( "GEOMETRY", attribute.getName() ) ;
			Assert.assertEquals( "Geometry", attribute.getTypeName() ) ;
			Assert.assertTrue(attribute.isNullable()) ;
			Assert.assertEquals(false, attribute.isIdentifier());
			Assert.assertNull(attribute.getListOfValues());
		}
		
	}
	
	
	
	/**
	 * FIXME this test should be completed to serialize/unserialize parent
	 * @throws JAXBException
	 */
	@Test
	public void testMarshallUnmarshallWithParent() throws JAXBException {
		FeatureType featureType = new FeatureType() ;

		featureType.setTypeName("COMMUNE");
		featureType.setDescription("La table des communes");
		
		List<AttributeType<?>> attributes=new ArrayList<AttributeType<?>>();
		// CODE
		{
			StringType attribute = new StringType() ;
			attribute.setName("INSEE");
			attribute.setRegexp("[0-9]{5}");
			attributes.add(attribute);
		}
		// CODE
		{
			BooleanType attribute = new BooleanType() ;
			attribute.setName("DETRUITE");
			attributes.add(attribute);
		}		
		// CODE_DEPT
		{
			StringType attribute = new StringType() ;
			attribute.setName("CODE_DEPT");
			List<String> listOfValues = new ArrayList<String>();
			listOfValues.add("01") ;
			listOfValues.add("02") ;
			attribute.setListOfValues(listOfValues);
			attributes.add(attribute);
		}
		featureType.setAttributes(attributes);
		
		FeatureType childType = new FeatureType() ;
		childType.setTypeName("CAPITALE");
		childType.setParent(featureType);

		JAXBContext context = JAXBContext.newInstance(FeatureType.class) ;
		Marshaller marshaller = context.createMarshaller() ;
		marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
		StringWriter output = new StringWriter() ;
		marshaller.marshal(featureType, output);
		marshaller.marshal(childType, output);
	}
	
}
