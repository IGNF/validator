package fr.ign.validator.cnig.idurba;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import fr.ign.validator.cnig.idurba.IdurbaHelperFactory;
import fr.ign.validator.cnig.idurba.impl.IdurbaHelperV1;
import fr.ign.validator.cnig.idurba.impl.IdurbaHelperV2;
import fr.ign.validator.model.DocumentModel;
import junit.framework.TestCase;


public class IdurbaHelperFactoryTest extends TestCase {

	protected DocumentModel createMockDocumentModel(String name){
		DocumentModel documentModel = mock(DocumentModel.class);
		when(documentModel.getName()).thenReturn(name);
		return documentModel;
	}

	public void testGetInstanceNotCNIG(){
		assertNull(IdurbaHelperFactory.getInstance(createMockDocumentModel("GEOFLA")));
	}

	public void testGetInstanceSCOT(){
		assertNull(IdurbaHelperFactory.getInstance(createMockDocumentModel("cnig_SCOT_2013")));
	}
	
	public void testGetInstanceSUP(){
		assertNull(IdurbaHelperFactory.getInstance(createMockDocumentModel("cnig_SUP_AC2_2013")));
	}
	
	public void testGetInstanceDU2013(){
		assertTrue(
			IdurbaHelperFactory.getInstance(createMockDocumentModel("cnig_PLU_2013")) instanceof IdurbaHelperV1
		);
		assertTrue(
			IdurbaHelperFactory.getInstance(createMockDocumentModel("cnig_POS_2013")) instanceof IdurbaHelperV1
		);
		assertTrue(
			IdurbaHelperFactory.getInstance(createMockDocumentModel("cnig_CC_2013")) instanceof IdurbaHelperV1
		);
		assertTrue(
			IdurbaHelperFactory.getInstance(createMockDocumentModel("cnig_PSMV_2013")) instanceof IdurbaHelperV1
		);
	}
	public void testGetInstanceDU2014(){
		assertTrue(
			IdurbaHelperFactory.getInstance(createMockDocumentModel("cnig_PLU_2014")) instanceof IdurbaHelperV1
		);
		assertTrue(
			IdurbaHelperFactory.getInstance(createMockDocumentModel("cnig_POS_2014")) instanceof IdurbaHelperV1
		);
		assertTrue(
			IdurbaHelperFactory.getInstance(createMockDocumentModel("cnig_CC_2014")) instanceof IdurbaHelperV1
		);
		assertTrue(
			IdurbaHelperFactory.getInstance(createMockDocumentModel("cnig_PSMV_2014")) instanceof IdurbaHelperV1
		);
	}
	public void testGetInstanceDU2017(){
		assertTrue(
			IdurbaHelperFactory.getInstance(createMockDocumentModel("cnig_PLU_2017")) instanceof IdurbaHelperV2
		);
		assertTrue(
			IdurbaHelperFactory.getInstance(createMockDocumentModel("cnig_POS_2017")) instanceof IdurbaHelperV2
		);
		assertTrue(
			IdurbaHelperFactory.getInstance(createMockDocumentModel("cnig_CC_2017")) instanceof IdurbaHelperV2
		);
		assertTrue(
			IdurbaHelperFactory.getInstance(createMockDocumentModel("cnig_PSMV_2017")) instanceof IdurbaHelperV2
		);
	}
	
}
