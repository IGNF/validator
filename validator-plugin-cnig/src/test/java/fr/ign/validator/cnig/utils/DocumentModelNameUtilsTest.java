package fr.ign.validator.cnig.utils;

import junit.framework.TestCase;

public class DocumentModelNameUtilsTest extends TestCase {
	
	public void testIsCnigStandard(){
		assertFalse(DocumentModelNameUtils.isCnigStandard("test"));
		assertFalse(DocumentModelNameUtils.isCnigStandard("test_bad"));
		assertTrue(DocumentModelNameUtils.isCnigStandard("cnig_SCOT_2013"));
	}

	public void testIsGetStandard(){
		assertFalse(DocumentModelNameUtils.isCnigStandard("test"));
		assertFalse(DocumentModelNameUtils.isCnigStandard("test_bad"));		
	}
	
	public void testGetDocumentType(){
		assertNull(DocumentModelNameUtils.getDocumentType("cnig"));
		
		assertEquals("SCOT", DocumentModelNameUtils.getDocumentType("cnig_SCOT_2013"));
		assertEquals("PLU", DocumentModelNameUtils.getDocumentType("cnig_PLU_2013"));
		assertEquals("SUP", DocumentModelNameUtils.getDocumentType("cnig_SUP_AC2_2013"));
	}
	
	public void testGetVersion(){
		assertNull(DocumentModelNameUtils.getVersion("cnig"));
		
		assertNull(DocumentModelNameUtils.getVersion("cnig_PLU_BAD"));
		
		assertEquals("2013", DocumentModelNameUtils.getVersion("cnig_SCOT_2013"));
		assertEquals("2013", DocumentModelNameUtils.getVersion("cnig_PLU_2013"));
		assertEquals("2017", DocumentModelNameUtils.getVersion("cnig_PLU_2017"));
	}
	
	
	
}
