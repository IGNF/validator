package fr.ign.validator.repository.xml;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fr.ign.validator.model.DocumentModel;
import junit.framework.TestCase;

public class XmlDocumentModelRepositoryTest extends TestCase {

	private File configDir ;
		
	@Before
	public void setUp() {
		configDir = new File(getClass().getResource("/config").getPath());
	}
	
	@Test
	public void testListAll(){
		XmlDocumentModelRepository repository = new XmlDocumentModelRepository(configDir);
		List<String> documentModelNames = repository.listAll();
		assertEquals(1,documentModelNames.size());
		assertEquals("cnig_PLU_2014", documentModelNames.get(0));
	}
	
	@Test
	public void testFindOneByName() throws IOException{
		XmlDocumentModelRepository repository = new XmlDocumentModelRepository(configDir);
		DocumentModel documentModel = repository.findOneByName("cnig_PLU_2014");
		assertNotNull(documentModel);
		assertEquals("cnig_PLU_2014", documentModel.getName());
	}
	
	@Test
	public void testFindOneByNameNotFound(){
		XmlDocumentModelRepository repository = new XmlDocumentModelRepository(configDir);
		try {
			assertNull(repository.findOneByName("not_found"));
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
}
