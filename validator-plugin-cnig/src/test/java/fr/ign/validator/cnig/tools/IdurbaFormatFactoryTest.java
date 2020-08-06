package fr.ign.validator.cnig.tools;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import fr.ign.validator.model.DocumentModel;

public class IdurbaFormatFactoryTest {

    protected DocumentModel createMockDocumentModel(String name) {
        DocumentModel documentModel = mock(DocumentModel.class);
        when(documentModel.getName()).thenReturn(name);
        return documentModel;
    }

    @Test
    public void testGetInstanceNotCNIG() {
        assertNull(IdurbaFormatFactory.getFormat(createMockDocumentModel("GEOFLA")));
    }

    @Test
    public void testGetInstanceSCOT2013() {
        assertNull(IdurbaFormatFactory.getFormat(createMockDocumentModel("cnig_SCOT_2013")));
    }

    @Test
    public void testGetInstanceSUP() {
        assertNull(IdurbaFormatFactory.getFormat(createMockDocumentModel("cnig_SUP_AC2_2013")));
    }

    @Test
    public void testGetInstanceDU2013() {
        assertTrue(IdurbaFormatFactory.getFormat(createMockDocumentModel("cnig_PLU_2013")) instanceof IdurbaFormatV1);
        assertTrue(IdurbaFormatFactory.getFormat(createMockDocumentModel("cnig_PLUi_2013")) instanceof IdurbaFormatV1);
        assertTrue(IdurbaFormatFactory.getFormat(createMockDocumentModel("cnig_POS_2013")) instanceof IdurbaFormatV1);
        assertTrue(IdurbaFormatFactory.getFormat(createMockDocumentModel("cnig_CC_2013")) instanceof IdurbaFormatV1);
        assertTrue(IdurbaFormatFactory.getFormat(createMockDocumentModel("cnig_PSMV_2013")) instanceof IdurbaFormatV1);
    }

    @Test
    public void testGetInstanceDU2014() {
        assertTrue(IdurbaFormatFactory.getFormat(createMockDocumentModel("cnig_PLU_2014")) instanceof IdurbaFormatV1);
        assertTrue(IdurbaFormatFactory.getFormat(createMockDocumentModel("cnig_PLUi_2014")) instanceof IdurbaFormatV1);
        assertTrue(IdurbaFormatFactory.getFormat(createMockDocumentModel("cnig_POS_2014")) instanceof IdurbaFormatV1);
        assertTrue(IdurbaFormatFactory.getFormat(createMockDocumentModel("cnig_CC_2014")) instanceof IdurbaFormatV1);
        assertTrue(IdurbaFormatFactory.getFormat(createMockDocumentModel("cnig_PSMV_2014")) instanceof IdurbaFormatV1);
    }

    @Test
    public void testGetInstanceDU2017() {
        assertTrue(IdurbaFormatFactory.getFormat(createMockDocumentModel("cnig_PLU_2017")) instanceof IdurbaFormatV2);
        assertTrue(IdurbaFormatFactory.getFormat(createMockDocumentModel("cnig_PLUi_2017")) instanceof IdurbaFormatV2);
        assertTrue(IdurbaFormatFactory.getFormat(createMockDocumentModel("cnig_POS_2017")) instanceof IdurbaFormatV2);
        assertTrue(IdurbaFormatFactory.getFormat(createMockDocumentModel("cnig_CC_2017")) instanceof IdurbaFormatV2);
        assertTrue(IdurbaFormatFactory.getFormat(createMockDocumentModel("cnig_PSMV_2017")) instanceof IdurbaFormatV2);
    }

    @Test
    public void testGetInstanceSCOT2018() {
        assertTrue(IdurbaFormatFactory.getFormat(createMockDocumentModel("cnig_SCOT_2018")) instanceof IdurbaFormatV2);
    }

}
