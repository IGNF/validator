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
		File documentPath = new File(getClass().getResource("/documents/110068012_PM3_28_20161104").getPath()) ;
		DocumentModel documentModel = finder.findByDocumentPath(documentPath);
		assertNotNull(documentModel);
		assertEquals("cnig_SUP_PM3_2013",documentModel.getName());
	}
	
	public void test19182_CC_20150517() throws IOException{
		DocumentModelFinder finder = new DocumentModelFinder(repository);
		File documentPath = new File(getClass().getResource("/documents/19182_CC_20150517").getPath()) ;
		DocumentModel documentModel = finder.findByDocumentPath(documentPath);
		assertNotNull(documentModel);
		assertEquals("cnig_CC_2017",documentModel.getName());
	}
	
	public void test41175_PLU_20140603() throws IOException{
		DocumentModelFinder finder = new DocumentModelFinder(repository);
		File documentPath = new File(getClass().getResource("/documents/41175_PLU_20140603").getPath()) ;
		DocumentModel documentModel = finder.findByDocumentPath(documentPath);
		assertNotNull(documentModel);
		assertEquals("cnig_PLU_2014",documentModel.getName());
	}

	public void test50545_CC_20130902() throws IOException{
		DocumentModelFinder finder = new DocumentModelFinder(repository);
		File documentPath = new File(getClass().getResource("/documents/50545_CC_20130902").getPath()) ;
		DocumentModel documentModel = finder.findByDocumentPath(documentPath);
		assertNotNull(documentModel);
		assertEquals("cnig_CC_2017",documentModel.getName());
	}
	


}
