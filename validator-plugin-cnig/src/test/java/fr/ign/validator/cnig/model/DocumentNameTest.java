package fr.ign.validator.cnig.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DocumentNameTest {

    @Test
    public void testNull() {
        DocumentName documentName = new DocumentName(null);
        assertFalse(documentName.isValid());
    }

    @Test
    public void testEmpty() {
        DocumentName documentName = new DocumentName("");
        assertFalse(documentName.isValid());
    }

    @Test
    public void testInvalid1() {
        DocumentName documentName = new DocumentName("scot");
        assertFalse(documentName.isValid());
    }

    @Test
    public void testInvalid2() {
        DocumentName documentName = new DocumentName("25349_PLUiche_20010101");
        assertFalse(documentName.isValid());
    }

    @Test
    public void testPlu() {
        DocumentName documentName = new DocumentName("25349_PLU_20010101");
        assertTrue(documentName.isValid());
        assertEquals(DocumentType.PLU, documentName.getDocumentType());
        assertEquals("25349", documentName.getTerritory());
        assertEquals("20010101", documentName.getDate());
    }

    @Test
    public void testPluPartial() {
        DocumentName documentName = new DocumentName("25349_PLU_20010101_C");
        assertTrue(documentName.isValid());
        assertEquals(DocumentType.PLU, documentName.getDocumentType());
        assertEquals("25349", documentName.getTerritory());
        assertEquals("20010101", documentName.getDate());
        assertEquals("C", documentName.getPart());
    }

    @Test
    public void testPluCaseInsensitive() {
        DocumentName documentName = new DocumentName("25349_pLu_20010101");
        assertTrue(documentName.isValid());
        assertEquals(DocumentType.PLU, documentName.getDocumentType());
        assertEquals("25349", documentName.getTerritory());
        assertEquals("20010101", documentName.getDate());
    }

    @Test
    public void testScot2013() {
        DocumentName documentName = new DocumentName("123456789_scot");
        assertTrue(documentName.isValid());
        assertEquals(DocumentType.SCoT, documentName.getDocumentType());
        assertEquals("123456789", documentName.getManager());
        assertNull(documentName.getTerritory());
    }

    @Test
    public void testScot2018() {
        DocumentName documentName = new DocumentName("123456789_scot_20010101");
        assertTrue(documentName.isValid());
        assertEquals(DocumentType.SCoT, documentName.getDocumentType());
        assertEquals("123456789", documentName.getManager());
        assertEquals("20010101", documentName.getDate());
    }

    @Test
    public void testScotPartial() {
        DocumentName documentName = new DocumentName("123456789_scot_20010101_B");
        assertTrue(documentName.isValid());
        assertEquals(DocumentType.SCoT, documentName.getDocumentType());
        assertEquals("123456789", documentName.getManager());
        assertEquals("20010101", documentName.getDate());
        assertEquals("B", documentName.getPart());
    }

    @Test
    public void testSup() {
        DocumentName documentName = new DocumentName("130011349_PM1_07348_20200703");
        assertTrue(documentName.isValid());
        assertEquals(DocumentType.SUP, documentName.getDocumentType());
        assertEquals("130011349", documentName.getManager());
        assertEquals("PM1", documentName.getCategory());
        assertEquals("07348", documentName.getTerritory());
        assertEquals("20200703", documentName.getDate());
    }
}
