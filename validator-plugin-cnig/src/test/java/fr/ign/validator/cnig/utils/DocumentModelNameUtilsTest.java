package fr.ign.validator.cnig.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class DocumentModelNameUtilsTest {
	
	@Test
	public void testIsCnigStandard(){
		assertFalse(DocumentModelNameUtils.isCnigStandard("test"));
		assertFalse(DocumentModelNameUtils.isCnigStandard("test_bad"));
		assertTrue(DocumentModelNameUtils.isCnigStandard("cnig_SCOT_2013"));
	}
	
	@Test
	public void testIsGetStandard(){
		assertFalse(DocumentModelNameUtils.isCnigStandard("test"));
		assertFalse(DocumentModelNameUtils.isCnigStandard("test_bad"));		
	}
	
	@Test
	public void testGetDocumentType(){
		assertNull(DocumentModelNameUtils.getDocumentType("cnig"));
		
		assertEquals("SCOT", DocumentModelNameUtils.getDocumentType("cnig_SCOT_2013"));
		assertEquals("PLU", DocumentModelNameUtils.getDocumentType("cnig_PLU_2013"));
		assertEquals("SUP", DocumentModelNameUtils.getDocumentType("cnig_SUP_AC2_2013"));
	}
	
	@Test
	public void testGetVersion(){
		assertNull(DocumentModelNameUtils.getVersion("cnig"));
		
		assertNull(DocumentModelNameUtils.getVersion("cnig_PLU_BAD"));
		
		assertEquals("2013", DocumentModelNameUtils.getVersion("cnig_SCOT_2013"));
		assertEquals("2013", DocumentModelNameUtils.getVersion("cnig_PLU_2013"));
		assertEquals("2017", DocumentModelNameUtils.getVersion("cnig_PLU_2017"));
	}
	
	
	
}
