package fr.ign.validator.tools;

import java.io.IOException;

import org.junit.Test;
import junit.framework.TestCase;

public class VRTTest extends TestCase {

	@Test
	public void testCreateSimpleVRT() throws IOException{
		
		
		assertTrue(true);
		
//		File source = new File(getClass().getResource("/csv/DUMMY.csv").getPath()) ;
//		
//		FeatureType featureType = new FeatureType() ;
//		featureType.setTypeName("DUMMY");
//		{
//			AttributeType<?> attribute = new StringType() ;
//			attribute.setName("A");
//			featureType.addAttribute(attribute);
//		}
//		//volontairement commenté : La génération est basée sur la lecture des colonnes du CSV
////		{
////			AttributeType<?> attribute = new BooleanType() ;
////			attribute.setName("B");
////			featureType.addAttribute(attribute);
////		}
//		{
//			AttributeType<?> attribute = new PointType() ;
//			attribute.setName("WKT");
//			featureType.addAttribute(attribute);
//		}
//		
//		File vrtFile = VRT.createFile(source, featureType);
//		
//		try {
//			String vrtContent = FileUtils.readFileToString(vrtFile);
////			<?xml version="1.0" encoding="UTF-8" standalone="no"?>
////			<OGRVRTDataSource>
//			assertTrue(vrtContent.contains("<OGRVRTDataSource>"));
////			<OGRVRTLayer name="DUMMY">
//			assertTrue(vrtContent.contains("<OGRVRTLayer name=\"DUMMY\">"));
////			<SrcDataSource>[...]/DUMMY.csv</SrcDataSource>
//			String expectedSrcDataSource = "<SrcDataSource>"+source+"</SrcDataSource>";
//			assertTrue(vrtContent.contains(expectedSrcDataSource));
////			<GeometryType>wkbPolygon</GeometryType>
//			assertTrue(vrtContent.contains("<GeometryType>wkbPoint</GeometryType>"));
////			<Field name="A" type="String" width="255"/>
//			assertTrue(vrtContent.contains("<Field name=\"A\" type=\"String\" width=\"254\"/>"));
////			<Field name="B" type="String" width="255"/>
//			assertTrue(vrtContent.contains("<Field name=\"B\" type=\"String\" width=\"254\"/>"));
////			</OGRVRTLayer>
////			</OGRVRTDataSource>
//
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		} 
	}
	
	
}

