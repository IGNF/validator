package fr.ign.validator.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

public class ProxyParserTest {

    @Test
    public void testNull() throws ParseException {
        Map<String, String> properties = ProxyParser.parse(null);
        assertTrue(properties.isEmpty());
    }

    @Test
    public void testEmpty() throws ParseException {
        Map<String, String> properties = ProxyParser.parse("");
        assertTrue(properties.isEmpty());
    }

    @Test
    public void testHostPort() throws ParseException {
        Map<String, String> properties = ProxyParser.parse("proxy.ign.fr:3128");
        assertFalse(properties.isEmpty());

        assertEquals("true", properties.get("proxySet"));
        assertEquals("proxy.ign.fr", properties.get("http.proxyHost"));
        assertEquals("3128", properties.get("http.proxyPort"));
        assertEquals("proxy.ign.fr", properties.get("https.proxyHost"));
        assertEquals("3128", properties.get("https.proxyPort"));
        assertEquals(5, properties.keySet().size());
    }

    @Test
    public void testHttpHostPort() throws ParseException {
        Map<String, String> properties = ProxyParser.parse("http://proxy.ign.fr:3128");
        assertFalse(properties.isEmpty());

        assertEquals("true", properties.get("proxySet"));
        assertEquals("proxy.ign.fr", properties.get("http.proxyHost"));
        assertEquals("3128", properties.get("http.proxyPort"));
        assertEquals("proxy.ign.fr", properties.get("https.proxyHost"));
        assertEquals("3128", properties.get("https.proxyPort"));
        assertEquals(5, properties.keySet().size());
    }

    @Test
    public void testHttpUserPasswordHostPort() throws ParseException {
        Map<String, String> properties = ProxyParser.parse("http://username:userpass@proxy.ign.fr:3128");
        assertFalse(properties.isEmpty());

        assertEquals("true", properties.get("proxySet"));
        assertEquals("proxy.ign.fr", properties.get("http.proxyHost"));
        assertEquals("3128", properties.get("http.proxyPort"));
        assertEquals("username", properties.get("http.proxyUser"));
        assertEquals("userpass", properties.get("http.proxyPassword"));

        assertEquals("proxy.ign.fr", properties.get("https.proxyHost"));
        assertEquals("3128", properties.get("https.proxyPort"));
        assertEquals("username", properties.get("https.proxyUser"));
        assertEquals("userpass", properties.get("https.proxyPassword"));

        assertEquals(9, properties.keySet().size());
    }

    @Test
    public void testInvalidUrl() throws ParseException {
        assertThrowForInvalid("http://invalid-no-port.fr");
        assertThrowForInvalid("http:///invalid-no-port.fr:3128");
        assertThrowForInvalid("ftp://invalid-not-http.fr:3128");
        assertThrowForInvalid("https:///invalid-not-http.fr:3128");
    }

    private void assertThrowForInvalid(String input) {
        boolean thrown = false;
        try {
            ProxyParser.parse(input);
        } catch (ParseException e) {
            thrown = true;
        }
        assertTrue("Should have thrown exception for : " + input, thrown);
    }

}
