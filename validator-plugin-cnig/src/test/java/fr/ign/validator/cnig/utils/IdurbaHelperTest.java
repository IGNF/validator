package fr.ign.validator.cnig.utils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import fr.ign.validator.cnig.utils.idurba.IdurbaHelperV1;
import fr.ign.validator.cnig.utils.idurba.IdurbaHelperV2;
import fr.ign.validator.model.DocumentModel;
import junit.framework.TestCase;


public class IdurbaHelperTest extends TestCase {

	protected DocumentModel createMockDocumentModel(String name){
		DocumentModel documentModel = mock(DocumentModel.class);
		when(documentModel.getName()).thenReturn(name);
		return documentModel;
	}

	public void testGetInstanceNotCNIG(){
		assertNull(IdurbaHelper.getInstance(createMockDocumentModel("GEOFLA")));
	}

	public void testGetInstanceSCOT(){
		assertNull(IdurbaHelper.getInstance(createMockDocumentModel("cnig_SCOT_2013")));
	}
	
	public void testGetInstanceSUP(){
		assertNull(IdurbaHelper.getInstance(createMockDocumentModel("cnig_SUP_AC2_2013")));
	}
	
	public void testGetInstanceDU2013(){
		assertTrue(
			IdurbaHelper.getInstance(createMockDocumentModel("cnig_PLU_2013")) instanceof IdurbaHelperV1
		);
		assertTrue(
			IdurbaHelper.getInstance(createMockDocumentModel("cnig_POS_2013")) instanceof IdurbaHelperV1
		);
		assertTrue(
			IdurbaHelper.getInstance(createMockDocumentModel("cnig_CC_2013")) instanceof IdurbaHelperV1
		);
		assertTrue(
			IdurbaHelper.getInstance(createMockDocumentModel("cnig_PSMV_2013")) instanceof IdurbaHelperV1
		);
	}
	public void testGetInstanceDU2014(){
		assertTrue(
			IdurbaHelper.getInstance(createMockDocumentModel("cnig_PLU_2014")) instanceof IdurbaHelperV1
		);
		assertTrue(
			IdurbaHelper.getInstance(createMockDocumentModel("cnig_POS_2014")) instanceof IdurbaHelperV1
		);
		assertTrue(
			IdurbaHelper.getInstance(createMockDocumentModel("cnig_CC_2014")) instanceof IdurbaHelperV1
		);
		assertTrue(
			IdurbaHelper.getInstance(createMockDocumentModel("cnig_PSMV_2014")) instanceof IdurbaHelperV1
		);
	}
	public void testGetInstanceDU2017(){
		assertTrue(
			IdurbaHelper.getInstance(createMockDocumentModel("cnig_PLU_2017")) instanceof IdurbaHelperV2
		);
		assertTrue(
			IdurbaHelper.getInstance(createMockDocumentModel("cnig_POS_2017")) instanceof IdurbaHelperV2
		);
		assertTrue(
			IdurbaHelper.getInstance(createMockDocumentModel("cnig_CC_2017")) instanceof IdurbaHelperV2
		);
		assertTrue(
			IdurbaHelper.getInstance(createMockDocumentModel("cnig_PSMV_2017")) instanceof IdurbaHelperV2
		);
	}
	
}
