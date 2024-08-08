package fr.ign.validator.tools;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class NetworkingTest {

    /**
     * Ensure that proxy is correctly defined from CLI option (--proxy)
     *
     * @throws ParseException
     */
    @Test
    public void testConfigureHttpClientSample() {
        Networking.configureHttpClient("http://proxy.home:3128");
        Properties systemSettings = System.getProperties();
        assertEquals("true", systemSettings.get("proxySet"));
        assertEquals("proxy.home", systemSettings.get("http.proxyHost"));
        assertEquals("proxy.home", systemSettings.get("http.proxyHost"));
        assertEquals("3128", systemSettings.get("http.proxyPort"));
        assertEquals("proxy.home", systemSettings.get("https.proxyHost"));
        assertEquals("3128", systemSettings.get("https.proxyPort"));
    }

    /**
     * Ensure that proxy is correctly defined from environment variables while
     * running tests so that URL content can be retreived.
     *
     * @throws IOException
     * @throws MalformedURLException
     * @throws ParseException
     */
    @Test
    public void testReadUrl() throws MalformedURLException, IOException {
        Networking.configureHttpClient();

        String url = "https://raw.githubusercontent.com/IGNF/validator/master/README.md";
        InputStream in = new URL(url).openStream();
        try {
            String content = IOUtils.toString(in, StandardCharsets.UTF_8);
            assertTrue(content.contains("# Validator"));
        } finally {
            in.close();
        }
    }

}
