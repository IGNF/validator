package fr.ign.validator.xml;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.TableModel;
import fr.ign.validator.tools.ResourceHelper;

public class XmlModelManagerTest {

	private XmlModelManager modelLoader;

	@Before
	public void setUp() throws Exception {
		modelLoader = new XmlModelManager();
	}

	/**
	 * Read cnig_PLU_2014 and performs regress test
	 * @throws JsonProcessingException 
	 */
	@Test
	public void tesDocumentModelCnigPlu2014() throws JsonProcessingException {
		File documentModelPath = ResourceHelper.getResourceFile(getClass(), "/config/cnig_PLU_2014/files.xml");
		DocumentModel documentModel = modelLoader.loadDocumentModel(documentModelPath);
		assertIsValid(documentModel);

		Assert.assertEquals("cnig_PLU_2014", documentModel.getName());
		Assert.assertEquals(31, documentModel.getFileModels().size());
	}

	/**
	 * Check that XmlModelManager load required FeatureTypes
	 */
	@Test
	public void testLoadDocumentModel() {
		File documentModelPath = ResourceHelper.getResourceFile(getClass(), "/config/sample-document/files.xml");
		DocumentModel documentModel = modelLoader.loadDocumentModel(documentModelPath);
		assertIsValid(documentModel);
		
		Assert.assertEquals("ccccc_CC_dddddddd", documentModel.getName());
		Assert.assertEquals(3, documentModel.getFileModels().size());

		int index = 0;
		{
			FileModel fileModel = documentModel.getFileModels().get(index++);
			Assert.assertEquals("SIMPLE", fileModel.getName());
			Assert.assertNotNull(fileModel.getFeatureType());
			Assert.assertEquals("SIMPLE", fileModel.getFeatureType().getName());
		}
		{
			FileModel fileModel = documentModel.getFileModels().get(index++);
			Assert.assertEquals("Donnees_geographiques", fileModel.getName());
			Assert.assertNull(fileModel.getFeatureType());
		}
		{
			FileModel fileModel = documentModel.getFileModels().get(index++);
			Assert.assertEquals("COMMUNE", fileModel.getName());
			Assert.assertNotNull(fileModel.getFeatureType());
			Assert.assertEquals("COMMUNE", fileModel.getFeatureType().getName());
		}
	}
	
	
	/**
	 * Performs basic consistency checks on DocumentModel
	 * @param documentModel
	 */
	private void assertIsValid(DocumentModel documentModel) {
		List<FileModel> fileModels = documentModel.getFileModels();
		for (FileModel fileModel : fileModels) {
			Assert.assertNotNull(fileModel.getName());
			Assert.assertNotNull(fileModel.getMandatory());
			if ( fileModel instanceof TableModel ) {
				FeatureType featureType = fileModel.getFeatureType();
				Assert.assertNotNull(featureType);
				assertIsValid(featureType);
			}
		}
	}

	private void assertIsValid(FeatureType featureType) {
		Assert.assertNotNull(featureType.getName());
		Assert.assertNotNull(featureType.getTypeName());
		Assert.assertEquals(featureType.getName(), featureType.getTypeName());

		Assert.assertFalse(
			"featureType must have at least one attribute", 
			featureType.getAttributeCount() == 0
		);
	}

}
