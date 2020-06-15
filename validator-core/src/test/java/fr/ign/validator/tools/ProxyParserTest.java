package fr.ign.validator.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import fr.ign.validator.exception.InvalidProxyException;

public class ProxyParserTest {

	@Test
	public void testNull() {
		Map<String,String> properties = ProxyParser.parse(null);
		assertTrue(properties.isEmpty());
	}

	@Test
	public void testEmpty() {
		Map<String,String> properties = ProxyParser.parse("");
		assertTrue(properties.isEmpty());
	}

	@Test
	public void testHostPort() {
		Map<String,String> properties = ProxyParser.parse("proxy.ign.fr:3128");
		assertFalse(properties.isEmpty());
		
		assertEquals("proxy.ign.fr",properties.get("proxyHost"));
		assertEquals("3128",properties.get("proxyPort"));
		assertEquals(2, properties.keySet().size());
	}

	@Test
	public void testHttpHostPort() {
		Map<String,String> properties = ProxyParser.parse("http://proxy.ign.fr:3128");
		assertFalse(properties.isEmpty());
		
		assertEquals("proxy.ign.fr",properties.get("proxyHost"));
		assertEquals("3128",properties.get("proxyPort"));
		assertEquals(2, properties.keySet().size());
	}
	
	
	@Test
	public void testHttpUserPasswordHostPort() {
		Map<String,String> properties = ProxyParser.parse("http://username:userpass@proxy.ign.fr:3128");
		assertFalse(properties.isEmpty());
		
		assertEquals("proxy.ign.fr",properties.get("proxyHost"));
		assertEquals("3128",properties.get("proxyPort"));
		assertEquals("username",properties.get("proxyUser"));
		assertEquals("userpass",properties.get("proxyPassword"));

		assertEquals(4, properties.keySet().size());
	}

	@Test
	public void testInvalidUrl() {
		assertThrowForInvalid("http://invalid-no-port.fr");
		assertThrowForInvalid("http:///invalid-no-port.fr:3128");
		assertThrowForInvalid("ftp://invalid-not-http.fr:3128");
		assertThrowForInvalid("https:///invalid-not-http.fr:3128");		
	}

	private void assertThrowForInvalid(String input) {
		boolean thrown = false;
		try {
			ProxyParser.parse(input);
		}catch(InvalidProxyException e){
			thrown = true;
		}
		assertTrue("Should have thrown an InvalidProxyException for : "+input, thrown);
	}

}
