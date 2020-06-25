package fr.ign.validator.cnig.idurba;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import fr.ign.validator.cnig.idurba.impl.IdurbaHelperV1;
import fr.ign.validator.cnig.idurba.impl.IdurbaHelperV2;
import fr.ign.validator.model.DocumentModel;

public class IdurbaHelperFactoryTest {

    protected DocumentModel createMockDocumentModel(String name) {
        DocumentModel documentModel = mock(DocumentModel.class);
        when(documentModel.getName()).thenReturn(name);
        return documentModel;
    }

    @Test
    public void testGetInstanceNotCNIG() {
        assertNull(IdurbaHelperFactory.getInstance(createMockDocumentModel("GEOFLA")));
    }

    @Test
    public void testGetInstanceSCOT() {
        assertNull(IdurbaHelperFactory.getInstance(createMockDocumentModel("cnig_SCOT_2013")));
    }

    @Test
    public void testGetInstanceSUP() {
        assertNull(IdurbaHelperFactory.getInstance(createMockDocumentModel("cnig_SUP_AC2_2013")));
    }

    @Test
    public void testGetInstanceDU2013() {
        assertTrue(
            IdurbaHelperFactory.getInstance(createMockDocumentModel("cnig_PLU_2013")) instanceof IdurbaHelperV1
        );
        assertTrue(
            IdurbaHelperFactory.getInstance(createMockDocumentModel("cnig_PLUi_2013")) instanceof IdurbaHelperV1
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

    @Test
    public void testGetInstanceDU2014() {
        assertTrue(
            IdurbaHelperFactory.getInstance(createMockDocumentModel("cnig_PLU_2014")) instanceof IdurbaHelperV1
        );
        assertTrue(
            IdurbaHelperFactory.getInstance(createMockDocumentModel("cnig_PLUi_2014")) instanceof IdurbaHelperV1
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

    @Test
    public void testGetInstanceDU2017() {
        assertTrue(
            IdurbaHelperFactory.getInstance(createMockDocumentModel("cnig_PLU_2017")) instanceof IdurbaHelperV2
        );
        assertTrue(
            IdurbaHelperFactory.getInstance(createMockDocumentModel("cnig_PLUi_2017")) instanceof IdurbaHelperV2
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
