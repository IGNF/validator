package fr.ign.validator.cnig.standard;

import java.io.File;
import java.io.IOException;

import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.repository.DocumentModelRepository;
import fr.ign.validator.repository.xml.XmlDocumentModelRepository;
import junit.framework.TestCase;

public class DocumentModelFinderRegressTest extends TestCase {

	private DocumentModelRepository repository ;
	
	@Override
	public void setUp() throws Exception {
		File configDir = new File(getClass().getResource("/config").getPath()) ;
		this.repository = new XmlDocumentModelRepository(configDir);
	}
	
	public void testSup() throws IOException{
		DocumentModelFinder finder = new DocumentModelFinder(repository);
		File documentPath = new File(getClass().getResource("/SUP_PM3_28/110068012_PM3_28_20161104").getPath()) ;
		DocumentModel documentModel = finder.findByDocumentPath(documentPath);
		assertNotNull(documentModel);
		assertEquals("cnig_SUP_PM3_2013",documentModel.getName());
	}

	public void test50545_CC_20130902() throws IOException{
		DocumentModelFinder finder = new DocumentModelFinder(repository);
		File documentPath = new File(getClass().getResource("/DU_50545/50545_CC_20130902").getPath()) ;
		DocumentModel documentModel = finder.findByDocumentPath(documentPath);
		assertNotNull(documentModel);
		assertEquals("cnig_CC_2014",documentModel.getName());
	}

}
