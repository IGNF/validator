package fr.ign.validator.xml;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import junit.framework.TestCase;

import org.junit.Test;

import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;


public class UnmarshallDocumentTest extends TestCase {
	
	@Test
	public void testUnmarshallDocumentModelGeofla(){
		File documentModelPath = new File(getClass().getResource("/xml/documentModel.xml").getPath()) ;
		
		try {
			JAXBContext context = JAXBContext.newInstance( FeatureType.class,DocumentModel.class );
			Unmarshaller unmarshaller = context.createUnmarshaller() ;
			DocumentModel documentModel = (DocumentModel) unmarshaller.unmarshal(documentModelPath) ;
			assertEquals("ccccc_CC_dddddddd",documentModel.getName());
			assertEquals(3,documentModel.getFileModels().size());
		} catch (JAXBException e) {
			fail(e.getMessage()) ;
		}
		
	}
	
}
