package fr.ign.validator.xml.binding;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import fr.ign.validator.tools.ResourceHelper;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;

/**
 * test marshalling/unmarshalling DocumentModel and FileModel
 * @author MBorne
 *
 */
public class DocumentModelTest {
	
	@Test
	public void testUnmarshallMarshall() throws JAXBException, IOException{
		File documentModelPath = ResourceHelper.getResourceFile(getClass(),"/config/sample-document/files.xml") ;
		
		JAXBContext context = JAXBContext.newInstance( FeatureType.class,DocumentModel.class );
		Unmarshaller unmarshaller = context.createUnmarshaller() ;
		DocumentModel documentModel = (DocumentModel) unmarshaller.unmarshal(documentModelPath) ;
		// name
		Assert.assertEquals("ccccc_CC_dddddddd",documentModel.getName());
		// regexp
		Assert.assertEquals("[0-9]{5}_CC_[0-9]{8}", documentModel.getRegexp());
		
		// fileModels
		Assert.assertEquals(3,documentModel.getFileModels().size());
		int index = 0;
		{
			FileModel fileModel = documentModel.getFileModels().get(index++);
			Assert.assertEquals("SIMPLE",fileModel.getName());
			Assert.assertEquals("table",fileModel.getType());
			Assert.assertEquals("(N_)?SIMPLE(_(02A|02B|[0-9]{3}))?",fileModel.getRegexp());
		}
		{
			FileModel fileModel = documentModel.getFileModels().get(index++);
			Assert.assertEquals("Donnees_geographiques",fileModel.getName());
			Assert.assertEquals("directory",fileModel.getType());
			Assert.assertEquals("Donnees_geographiques",fileModel.getRegexp());
		}
		{
			FileModel fileModel = documentModel.getFileModels().get(index++);
			Assert.assertEquals("COMMUNE",fileModel.getName());
			Assert.assertEquals("table",fileModel.getType());
			Assert.assertEquals("Donnees_geographiques/(N_)?COMMUNE(_[0-9]{5})?(_(02A|02B|[0-9]{3}))?",fileModel.getRegexp());
		}
		
		// test that unmarshalling/marshalling is symetric
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		StringWriter sw = new StringWriter();
		marshaller.marshal(documentModel, sw);
		String marshalled = sw.toString();
		Assert.assertEquals(marshalled, FileUtils.readFileToString(documentModelPath,StandardCharsets.UTF_8));
	}
	
	

}
