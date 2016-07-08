package fr.ign.validator.loader;

import java.io.File;

import javax.xml.bind.JAXBException;

import org.junit.Test;

import fr.ign.validator.loader.ModelLoader;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;
import junit.framework.TestCase;

public class ModelLoaderTest extends TestCase {

	private ModelLoader modelLoader ;
	
	@Override
	protected void setUp() throws Exception {
		modelLoader = new ModelLoader() ;
	}
	
	@Test
	public void testLoadFeatureTypeCommune(){
		File srcFile = new File(getClass().getResource("/xml/COMMUNE.xml").getPath()) ;
		
		try {
			FeatureType featureType = modelLoader.loadFeatureType(srcFile) ;
			assertEquals("COMMUNE", featureType.getName()) ;
			assertEquals("La table des communes", featureType.getDescription()) ;			
			assertEquals(3, featureType.getAttributeCount());
			assertFalse(featureType.isSpatial()); //GEOMETRY non définie
//	        <attribute>
//	            <name>INSEE</name>
//	            <type>String</type>
//	            <regexp>[0-9]{5}</regexp>
//	            <nullable>false</nullable>
//	        </attribute>
			{
				AttributeType<?> attribute = featureType.getAttribute(0) ;
				assertEquals( "INSEE", attribute.getName() ) ;
				assertEquals( "String", attribute.getTypeName() ) ;
				assertEquals( "[0-9]{5}", attribute.getRegexp() ) ;
				assertFalse(attribute.isNullable()) ;
				assertNull(attribute.getListOfValues());
			}
//	        <attribute>
//	            <name>CODE_DEPT</name>
//	            <type>String</type>
//	            <nullable>false</nullable>
//	            <listOfValues>
//	                <value>01</value>
//	                <value>02</value>
//	            </listOfValues>
//	        </attribute>
			{
				AttributeType<?> attribute = featureType.getAttribute(1) ;
				assertEquals( "CODE_DEPT", attribute.getName() ) ;
				assertEquals( "String", attribute.getTypeName() ) ;
				assertNull( attribute.getRegexp() ) ;
				assertFalse(attribute.isNullable()) ;

				assertNotNull(attribute.getListOfValues());
				assertEquals(2, attribute.getListOfValues().size());
			}
//	        <attribute>
//	        	<name>DETRUIT</name>
//	        	<type>Boolean</type>
//	        	<nullable>true</nullable>
//	        </attribute>
			{
				AttributeType<?> attribute = featureType.getAttribute(2) ;
				assertEquals( "DETRUIT", attribute.getName() ) ;
				assertEquals( "Boolean", attribute.getTypeName() ) ;
				assertTrue(attribute.isNullable()) ;
				assertNull(attribute.getListOfValues());
			}

			
		} catch (JAXBException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
	
	
	@Test
	public void testSimpleWithCDATA(){
		File srcFile = new File(getClass().getResource("/xml/SIMPLE.xml").getPath()) ;
		
		try {
			FeatureType featureType = modelLoader.loadFeatureType(srcFile) ;
			assertEquals("SIMPLE", featureType.getName()) ;
			assertEquals("TABLE TEST", featureType.getDescription()) ;			
			assertEquals(3, featureType.getAttributeCount());
			assertTrue(featureType.isSpatial());
			
//	        <attribute>
//	            <name>INSEE</name>
//	            <type>String</type>
//	            <regexp>[0-9]{5}</regexp>
//	            <nullable>false</nullable>
//	        </attribute>
			{
				AttributeType<?> attribute = featureType.getAttribute(0) ;
				assertEquals( "ID", attribute.getName() ) ;
				assertEquals( "Integer", attribute.getTypeName() ) ;
				assertNull( attribute.getRegexp() ) ;
				assertFalse(attribute.isNullable()) ;
				assertNull(attribute.getListOfValues());
			}
//	        <attribute>
//	            <name>CODE_DEPT</name>
//	            <type>String</type>
//	            <nullable>false</nullable>
//	            <listOfValues>
//	                <value>01</value>
//	                <value>02</value>
//	            </listOfValues>
//	        </attribute>
			{
				AttributeType<?> attribute = featureType.getAttribute(1) ;
				assertEquals( "NAME", attribute.getName() ) ;
				assertEquals( "String", attribute.getTypeName() ) ;
				assertNull( attribute.getRegexp() ) ;
				assertFalse(attribute.isNullable()) ;

				assertNull(attribute.getListOfValues());
			}
//	        <attribute>
//	        	<name>DETRUIT</name>
//	        	<type>Boolean</type>
//	        	<nullable>true</nullable>
//	        </attribute>
			{
				AttributeType<?> attribute = featureType.getAttribute(2) ;
				assertEquals( "GEOMETRY", attribute.getName() ) ;
				assertEquals( "Geometry", attribute.getTypeName() ) ;
				assertTrue(attribute.isNullable()) ;
				assertNull(attribute.getListOfValues());
			}
			
		} catch (JAXBException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}
	
	@Test
	public void testDocumentModelGeofla(){
		File documentModelPath = new File(getClass().getResource("/geofla/files.xml").getPath()) ;
		try {
			DocumentModel documentModel = modelLoader.loadDocumentModel(documentModelPath) ;
			assertEquals(2,documentModel.getFileModels().size()) ;
			assertEquals(2,documentModel.getFeatureCatalogue().getFeatureTypes().size()) ;
		} catch (JAXBException e) {
			fail(e.getMessage()) ;
		}
	}
	
	
}
