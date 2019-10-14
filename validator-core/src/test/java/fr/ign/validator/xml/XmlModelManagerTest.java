package fr.ign.validator.xml;

import java.io.File;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ign.validator.tools.ResourceHelper;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FileModel;

public class XmlModelManagerTest {

	private XmlModelManager modelLoader ;
	
	@Before
	public void setUp() throws Exception {
		modelLoader = new XmlModelManager() ;
	}

	/**
	 * Check that XmlModelManager load required FeatureTypes
	 * @throws JAXBException
	 */
	@Test
	public void testLoadDocumentModel() throws JAXBException{
		File documentModelPath = ResourceHelper.getResourceFile(getClass(),"/config/sample-document/files.xml") ;
		DocumentModel documentModel = modelLoader.loadDocumentModel(documentModelPath);
		Assert.assertEquals("ccccc_CC_dddddddd",documentModel.getName());
		Assert.assertEquals(3, documentModel.getFileModels().size());
		
		int index = 0;
		{
			FileModel fileModel = documentModel.getFileModels().get(index++);
			Assert.assertEquals("SIMPLE",fileModel.getName());
			Assert.assertNotNull(fileModel.getFeatureType());
			Assert.assertEquals("SIMPLE",fileModel.getFeatureType().getName());
		}
		{
			FileModel fileModel = documentModel.getFileModels().get(index++);
			Assert.assertEquals("Donnees_geographiques",fileModel.getName());
			Assert.assertNull(fileModel.getFeatureType());
		}
		{
			FileModel fileModel = documentModel.getFileModels().get(index++);
			Assert.assertEquals("COMMUNE",fileModel.getName());
			Assert.assertNotNull(fileModel.getFeatureType());
			Assert.assertEquals("COMMUNE",fileModel.getFeatureType().getName());
		}
	}

}
