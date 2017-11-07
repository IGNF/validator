package fr.ign.validator.xml.binding;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import junit.framework.TestCase;

/**
 * test marshalling/unmarshalling DocumentModel and FileModel
 * @author MBorne
 *
 */
public class DocumentModelTest extends TestCase {
	
	@Test
	public void testUnmarshallMarshall() throws JAXBException, IOException{
		File documentModelPath = new File(getClass().getResource("/xml/sample-document/files.xml").getPath()) ;
		
		JAXBContext context = JAXBContext.newInstance( FeatureType.class,DocumentModel.class );
		Unmarshaller unmarshaller = context.createUnmarshaller() ;
		DocumentModel documentModel = (DocumentModel) unmarshaller.unmarshal(documentModelPath) ;
		// name
		assertEquals("ccccc_CC_dddddddd",documentModel.getName());
		// regexp
		assertEquals("[0-9]{5}_CC_[0-9]{8}", documentModel.getRegexp());
		
		// fileModels
		assertEquals(3,documentModel.getFileModels().size());
		int index = 0;
		{
			FileModel fileModel = documentModel.getFileModels().get(index++);
			assertEquals("SIMPLE",fileModel.getName());
			assertEquals("table",fileModel.getType());
			assertEquals("(N_)?SIMPLE(_(02A|02B|[0-9]{3}))?",fileModel.getRegexp());
		}
		{
			FileModel fileModel = documentModel.getFileModels().get(index++);
			assertEquals("Donnees_geographiques",fileModel.getName());
			assertEquals("directory",fileModel.getType());
			assertEquals("Donnees_geographiques",fileModel.getRegexp());
		}
		{
			FileModel fileModel = documentModel.getFileModels().get(index++);
			assertEquals("COMMUNE",fileModel.getName());
			assertEquals("table",fileModel.getType());
			assertEquals("Donnees_geographiques/(N_)?COMMUNE(_[0-9]{5})?(_(02A|02B|[0-9]{3}))?",fileModel.getRegexp());
		}
		
		// test that unmarshalling/marshalling is symetric
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter sw = new StringWriter();
		marshaller.marshal(documentModel, sw);
		String marshalled = sw.toString();
		assertEquals(marshalled, FileUtils.readFileToString(documentModelPath));
	}
	
	

}
