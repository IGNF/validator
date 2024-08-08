package fr.ign.validator.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import fr.ign.validator.exception.InvalidModelException;

public class ModelReaderFactoryTest {

    @Test
    public void testXml() throws MalformedURLException {
        URL url = new URL("https://example.org/my-model.xml");
        RuntimeException thrown = null;
        try {
            ModelReaderFactory.createModelReader(url);
        } catch (RuntimeException e) {
            thrown = e;
        }
        assertNotNull(thrown);
        assertTrue(thrown instanceof InvalidModelException);
        assertEquals(
            "Fail to load https://example.org/my-model.xml (XML model support has been removed, use JSON format)",
            thrown.getMessage()
        );
    }

    @Test
    public void testJson() throws MalformedURLException {
        URL url = new URL("https://example.org/my-model.json");
        ModelReader reader = ModelReaderFactory.createModelReader(url);
        assertTrue(reader instanceof JsonModelReader);
    }

    @Test
    public void testNoExtension() throws MalformedURLException {
        URL url = new URL("https://example.org/api/document-model?name=cnig_PLU_v2014");
        ModelReader reader = ModelReaderFactory.createModelReader(url);
        assertTrue(reader instanceof JsonModelReader);
    }

}
