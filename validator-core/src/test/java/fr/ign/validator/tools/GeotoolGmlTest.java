package fr.ign.validator.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.geotools.GML;
import org.geotools.GML.Version;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.junit.Ignore;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeDescriptor;
import org.xml.sax.SAXException;

@Ignore("geotools GML example")
public class GeotoolGmlTest extends TestCase {

	@Test
	public void testGml() throws IOException, ParserConfigurationException, SAXException{
		System.setProperty( "proxySet", "true" );
		System.setProperty( "proxyHost", "proxy.ign.fr" );
		System.setProperty( "proxyPort", "3128" ); 
		
		InputStream in = getClass().getResourceAsStream( "/gml/ZONE_URBA.gml");
		
		GML gml = new GML(Version.GML3);
		gml.setLegacy(true);
		
		SimpleFeatureIterator it = gml.decodeFeatureIterator(in) ;
		
		int count = 0 ;
		while ( it.hasNext() ){
			count++ ;
			
			SimpleFeature feature = it.next() ;
			SimpleFeatureType featureType = feature.getFeatureType() ;
			
			List<String> attributeNames = new ArrayList<String>() ;
			
			for (AttributeDescriptor attribute : featureType.getAttributeDescriptors()) {
				attributeNames.add(attribute.getName().toString());
			}
			
			assertTrue(attributeNames.contains("NOMFIC"));			
			assertTrue(attributeNames.contains("LIBELLE"));
			assertTrue(attributeNames.contains("LIBELONG"));
			assertTrue(attributeNames.contains("TYPEZONE"));
			assertTrue(attributeNames.contains("DESTDOMI"));
			assertTrue(attributeNames.contains("NOMFIC"));
			assertTrue(attributeNames.contains("URLFIC"));
			assertTrue(attributeNames.contains("INSEE"));
			assertTrue(attributeNames.contains("DATAPPRO"));
			assertTrue(attributeNames.contains("DATVALID"));
		}
			
		
		assertEquals(66,count);
	}
	
}
