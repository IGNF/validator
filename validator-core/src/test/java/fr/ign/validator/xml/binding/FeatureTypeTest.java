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
import org.junit.Test;

import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.type.BooleanType;
import fr.ign.validator.model.type.StringType;
import junit.framework.TestCase;

/**
 * Test feature type marshalling
 * @author MBorne
 *
 */
public class FeatureTypeTest extends TestCase {

	public static final Logger logger = LogManager.getRootLogger() ;
	
	protected JAXBContext context ;
	protected Unmarshaller unmarshaller ;
	protected Marshaller marshaller ;

	@Override
	public void setUp() throws Exception {
		 context = JAXBContext.newInstance(FeatureType.class) ;
		 unmarshaller = context.createUnmarshaller();
		 marshaller = context.createMarshaller() ;
		 marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
	}
	
	@Test
	public void testMarshallUnmarshallCommune() throws JAXBException, IOException {
		File featureTypePath = new File(getClass().getResource("/xml/sample-document/types/COMMUNE.xml").getPath()) ;

		FeatureType featureType = (FeatureType)unmarshaller.unmarshal(featureTypePath);
		assertNotNull(featureType);
		assertEquals("COMMUNE",featureType.getName());
		assertEquals("La table des communes",featureType.getDescription());
		assertFalse(featureType.isSpatial());
		assertEquals(3,featureType.getAttributeCount());
		
		int index = 0;
		{
			AttributeType<?> attributeType = featureType.getAttribute(index++);
			assertEquals("INSEE",attributeType.getName());
			assertEquals("String",attributeType.getTypeName());
			assertEquals(false, attributeType.isNullable());
			assertEquals("[0-9]{5}", attributeType.getRegexp());
			assertNull(attributeType.getListOfValues());
		}
		{
			AttributeType<?> attributeType = featureType.getAttribute(index++);
			assertEquals("CODE_DEPT",attributeType.getName());
			assertEquals("String",attributeType.getTypeName());
			assertEquals(false, attributeType.isNullable());
			assertNull(attributeType.getRegexp());
			assertNotNull(attributeType.getListOfValues());
			assertEquals("01,02", Strings.join(attributeType.getListOfValues(),','));
		}
		{
			AttributeType<?> attributeType = featureType.getAttribute(index++);
			assertEquals("DETRUIT",attributeType.getName());
			assertEquals("Boolean",attributeType.getTypeName());
			assertEquals(true, attributeType.isNullable());
			assertNull(attributeType.getRegexp());
			assertNull(attributeType.getListOfValues());
		}
		
		StringWriter output = new StringWriter() ;
		marshaller.marshal(featureType, output);
		assertEquals(
			FileUtils.readFileToString(featureTypePath),
			output.toString()
		);
	}
	
	

	@Test
	public void testUnmarshallWithCDATA() throws JAXBException{
		File srcFile = new File(getClass().getResource("/xml/sample-document/types/SIMPLE.xml").getPath()) ;
		
		FeatureType featureType = (FeatureType)unmarshaller.unmarshal(srcFile) ;
		assertEquals("SIMPLE", featureType.getName()) ;
		assertEquals("TABLE TEST", featureType.getDescription()) ;			
		assertEquals(3, featureType.getAttributeCount());
		assertTrue(featureType.isSpatial());
		
		int index = 0 ;
		{
			AttributeType<?> attribute = featureType.getAttribute(index++) ;
			assertEquals( "ID", attribute.getName() ) ;
			assertEquals( "Integer", attribute.getTypeName() ) ;
			assertNull( attribute.getRegexp() ) ;
			assertFalse(attribute.isNullable()) ;
			assertNull(attribute.getListOfValues());
		}

		{
			AttributeType<?> attribute = featureType.getAttribute(index++) ;
			assertEquals( "NAME", attribute.getName() ) ;
			assertEquals( "String", attribute.getTypeName() ) ;
			assertNull( attribute.getRegexp() ) ;
			assertFalse(attribute.isNullable()) ;

			assertNull(attribute.getListOfValues());
		}
	
		{
			AttributeType<?> attribute = featureType.getAttribute(index++) ;
			assertEquals( "GEOMETRY", attribute.getName() ) ;
			assertEquals( "Geometry", attribute.getTypeName() ) ;
			assertTrue(attribute.isNullable()) ;
			assertNull(attribute.getListOfValues());
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
