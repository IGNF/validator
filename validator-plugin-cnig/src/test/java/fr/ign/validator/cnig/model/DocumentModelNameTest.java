package fr.ign.validator.cnig.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DocumentModelNameTest {

    @Test
    public void testIsCnigStandard() {
        assertFalse(DocumentModelName.isCnigStandard("test"));
        assertFalse(DocumentModelName.isCnigStandard("test_bad"));

        assertTrue(DocumentModelName.isCnigStandard("cnig_SCOT_2013"));
        assertTrue(DocumentModelName.isCnigStandard("cnig_PSMV_2013"));
    }

    @Test
    public void testGetDocumentType() {
        assertNull(DocumentModelName.getDocumentType("cnig"));

        assertEquals(DocumentModelName.TYPE_SCOT, DocumentModelName.getDocumentType("cnig_SCOT_2013"));
        assertEquals(DocumentModelName.TYPE_PLU, DocumentModelName.getDocumentType("cnig_PLU_2013"));
        assertEquals(DocumentModelName.TYPE_SUP, DocumentModelName.getDocumentType("cnig_SUP_AC2_2013"));
    }

    @Test
    public void testIsDocumentSup() {
        assertFalse(DocumentModelName.isDocumentModelSup("cnig"));

        assertFalse(DocumentModelName.isDocumentModelSup("cnig_SCOT_2013"));
        assertFalse(DocumentModelName.isDocumentModelSup("cnig_PLU_2013"));
        assertTrue(DocumentModelName.isDocumentModelSup("cnig_SUP_AC2_2013"));
        assertTrue(DocumentModelName.isDocumentModelSup("cnig_sup_ac2_2013"));
    }

    @Test
    public void testGetVersion() {
        assertNull(DocumentModelName.getVersion("cnig"));

        assertNull(DocumentModelName.getVersion("cnig_PLU_BAD"));

        assertEquals("2013", DocumentModelName.getVersion("cnig_SCOT_2013"));
        assertEquals("2013", DocumentModelName.getVersion("cnig_PLU_2013"));
        assertEquals("2017", DocumentModelName.getVersion("cnig_PLU_2017"));
    }

}
