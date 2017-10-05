package fr.ign.validator.string.transform;

import java.nio.charset.StandardCharsets;

import junit.framework.TestCase;

public class EscapeForCharsetTest extends TestCase {
	public void testEscapeNonLatin1Characters(){
		EscapeForCharset escaper = new EscapeForCharset(StandardCharsets.ISO_8859_1);
		String s = new String("some latin1 characters : ©é, some non latin1 characters : ᆦ, some latin1 supplement : \u0092, some standard controls : \t\n\r\f");
		String expected = new String("some latin1 characters : ©é, some non latin1 characters : \\u11a6, some latin1 supplement : \u0092, some standard controls : \t\n\r\f");
		assertEquals(expected,escaper.transform(s));
	}
	public void testEscapeUsAscii(){
		EscapeForCharset escaper = new EscapeForCharset(StandardCharsets.US_ASCII);
		String s = new String("some latin1 characters : ©é, some non latin1 characters : ᆦ, some latin1 supplement : \u0092, some standard controls : \t\n\r\f");
		String expected = new String("some latin1 characters : \\u00a9\\u00e9, some non latin1 characters : \\u11a6, some latin1 supplement : \\u0092, some standard controls : \t\n\r\f");
		assertEquals(expected,escaper.transform(s));
	}
}
