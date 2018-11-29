package fr.ign.validator.cnig.standard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import fr.ign.validator.cnig.CnigRegressHelper;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.repository.DocumentModelRepository;


public class DocumentModelFinderRegressTest {

	private DocumentModelRepository repository ;
	
	@Before
	public void setUp() throws Exception {
		this.repository = CnigRegressHelper.getDocumentModelRepository();
	}
	
	@Test
	public void testSup() throws IOException{
		assertFindDocumentModel("cnig_SUP_PM3_2013", "110068012_PM3_28_20161104");
	}

	@Test
	public void test19182_CC_20150517() throws IOException{
		assertFindDocumentModel("cnig_CC_2017", "19182_CC_20150517");
	}
	
	@Test
	public void test41175_PLU_20140603() throws IOException{
		assertFindDocumentModel("cnig_PLU_2013", "41175_PLU_20140603");
	}

	@Test
	public void test50545_CC_20130902() throws IOException{
		assertFindDocumentModel("cnig_CC_2017", "50545_CC_20130902");
	}
	
	@Test
	public void test251702833_scot() throws IOException{
		assertFindDocumentModel("cnig_SCoT_2013", "251702833_scot");
	}

	/**
	 * Find model for documentName and compare with expectedDocumentModelName
	 * @param expectedDocumentModelName
	 * @param documentName
	 * @throws IOException
	 */
	private void assertFindDocumentModel(String expectedDocumentModelName, String documentName) throws IOException {
		DocumentModelFinder finder = new DocumentModelFinder(repository);
		File documentPath = CnigRegressHelper.getSampleDocument(documentName, null) ;
		DocumentModel documentModel = finder.findByDocumentPath(documentPath);
		assertNotNull(documentModel);
		assertEquals(
			expectedDocumentModelName+" expected for "+documentName,
			expectedDocumentModelName,
			documentModel.getName()
		);
	}
	
}
