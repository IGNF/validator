package fr.ign.validator.io;

import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

public class ModelReaderFactoryTest {

    @Test
    public void testXml() throws MalformedURLException {
        URL url = new URL("https://example.org/my-model.xml");
        ModelReader reader = ModelReaderFactory.createModelReader(url);
        assertTrue(reader instanceof XmlModelReader);
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
