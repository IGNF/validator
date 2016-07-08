package fr.ign.validator.xml;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import junit.framework.TestCase;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.type.BooleanType;
import fr.ign.validator.model.type.StringType;
import fr.ign.validator.xml.JaxbNamespacePrefixMapper;


public class MarshallFeatureTypeTest extends TestCase {

	public static final Logger logger = LogManager.getRootLogger() ;
	
	private FeatureType featureType ;
	private FeatureType childType ;
	
	@Override
	protected void setUp() throws Exception {
		featureType = new FeatureType() ;
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
		
		
		childType = new FeatureType() ;
		childType.setTypeName("CAPITALE");
		childType.setParent(featureType);
	}
	
	@Override
	protected void tearDown() throws Exception {
		featureType = null ;
	}
	
	@Test
	public void testMarshall() {
		try {
			JAXBContext context = JAXBContext.newInstance(FeatureType.class) ;
			Marshaller marshaller = context.createMarshaller() ;
			marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new JaxbNamespacePrefixMapper());
			StringWriter output = new StringWriter() ;
			marshaller.marshal(featureType, output);
			marshaller.marshal(childType, output);
			logger.debug(output.toString());
		} catch (JAXBException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
}
