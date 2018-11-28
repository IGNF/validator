package fr.ign.validator.cnig.standard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.repository.DocumentModelRepository;
import fr.ign.validator.repository.xml.XmlDocumentModelRepository;


public class DocumentModelFinderRegressTest {

	private DocumentModelRepository repository ;
	
	@Before
	public void setUp() throws Exception {
		File configDir = new File(getClass().getResource("/config").getPath()) ;
		this.repository = new XmlDocumentModelRepository(configDir);
	}
	
	@Test
	public void testSup() throws IOException{
		DocumentModelFinder finder = new DocumentModelFinder(repository);
		File documentPath = new File(getClass().getResource("/documents/110068012_PM3_28_20161104").getPath()) ;
		DocumentModel documentModel = finder.findByDocumentPath(documentPath);
		assertNotNull(documentModel);
		assertEquals("cnig_SUP_PM3_2013",documentModel.getName());
	}
	
	@Test
	public void test19182_CC_20150517() throws IOException{
		DocumentModelFinder finder = new DocumentModelFinder(repository);
		File documentPath = new File(getClass().getResource("/documents/19182_CC_20150517").getPath()) ;
		DocumentModel documentModel = finder.findByDocumentPath(documentPath);
		assertNotNull(documentModel);
		assertEquals("cnig_CC_2017",documentModel.getName());
	}
	
	@Test
	public void test41175_PLU_20140603() throws IOException{
		DocumentModelFinder finder = new DocumentModelFinder(repository);
		File documentPath = new File(getClass().getResource("/documents/41175_PLU_20140603").getPath()) ;
		DocumentModel documentModel = finder.findByDocumentPath(documentPath);
		assertNotNull(documentModel);
		assertEquals("cnig_PLU_2014",documentModel.getName());
	}
	
	@Test
	public void test50545_CC_20130902() throws IOException{
		DocumentModelFinder finder = new DocumentModelFinder(repository);
		File documentPath = new File(getClass().getResource("/documents/50545_CC_20130902").getPath()) ;
		DocumentModel documentModel = finder.findByDocumentPath(documentPath);
		assertNotNull(documentModel);
		assertEquals("cnig_CC_2017",documentModel.getName());
	}
	
	@Test
	public void test251702833_scot() throws IOException{
		DocumentModelFinder finder = new DocumentModelFinder(repository);
		File documentPath = new File(getClass().getResource("/documents/251702833_scot").getPath()) ;
		DocumentModel documentModel = finder.findByDocumentPath(documentPath);
		assertNotNull(documentModel);
		assertEquals("cnig_SCoT_2013",documentModel.getName());
	}

}
