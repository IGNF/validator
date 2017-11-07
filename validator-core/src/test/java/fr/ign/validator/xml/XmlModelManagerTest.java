package fr.ign.validator.xml;

import java.io.File;

import javax.xml.bind.JAXBException;

import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FileModel;
import junit.framework.TestCase;

public class XmlModelManagerTest extends TestCase {

	private XmlModelManager modelLoader ;
	
	@Override
	protected void setUp() throws Exception {
		modelLoader = new XmlModelManager() ;
	}

	/**
	 * Check that XmlModelManager load required FeatureTypes
	 * @throws JAXBException
	 */
	public void testLoadDocumentModel() throws JAXBException{
		File documentModelPath = new File(getClass().getResource("/xml/sample-document/files.xml").getPath()) ;
		DocumentModel documentModel = modelLoader.loadDocumentModel(documentModelPath);
		assertEquals("ccccc_CC_dddddddd",documentModel.getName());
		assertEquals(3, documentModel.getFileModels().size());
		
		int index = 0;
		{
			FileModel fileModel = documentModel.getFileModels().get(index++);
			assertEquals("SIMPLE",fileModel.getName());
			assertNotNull(fileModel.getFeatureType());
			assertEquals("SIMPLE",fileModel.getFeatureType().getName());
		}
		{
			FileModel fileModel = documentModel.getFileModels().get(index++);
			assertEquals("Donnees_geographiques",fileModel.getName());
			assertNull(fileModel.getFeatureType());
		}
		{
			FileModel fileModel = documentModel.getFileModels().get(index++);
			assertEquals("COMMUNE",fileModel.getName());
			assertNotNull(fileModel.getFeatureType());
			assertEquals("COMMUNE",fileModel.getFeatureType().getName());
		}
	}

}
