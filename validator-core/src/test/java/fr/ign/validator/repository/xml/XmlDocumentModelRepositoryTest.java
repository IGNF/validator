package fr.ign.validator.repository.xml;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ign.validator.ResourceHelper;
import fr.ign.validator.model.DocumentModel;


public class XmlDocumentModelRepositoryTest {

	private File configDir ;
		
	@Before
	public void setUp() {
		configDir = ResourceHelper.getResourcePath("/config");
	}

	@Test
	public void testListAll(){
		XmlDocumentModelRepository repository = new XmlDocumentModelRepository(configDir);
		List<String> documentModelNames = repository.listAll();
		Assert.assertEquals(1,documentModelNames.size());
		Assert.assertEquals("cnig_PLU_2014", documentModelNames.get(0));
	}
	
	@Test
	public void testFindOneByName() throws IOException{
		XmlDocumentModelRepository repository = new XmlDocumentModelRepository(configDir);
		DocumentModel documentModel = repository.findOneByName("cnig_PLU_2014");
		Assert.assertNotNull(documentModel);
		Assert.assertEquals("cnig_PLU_2014", documentModel.getName());
	}
	
	@Test
	public void testFindOneByNameNotFound(){
		XmlDocumentModelRepository repository = new XmlDocumentModelRepository(configDir);
		try {
			Assert.assertNull(repository.findOneByName("not_found"));
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		}
	}
}
