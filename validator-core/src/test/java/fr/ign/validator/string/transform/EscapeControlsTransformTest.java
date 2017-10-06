package fr.ign.validator.string.transform;

import fr.ign.validator.string.transform.IsoControlEscaper;
import junit.framework.TestCase;

public class EscapeControlsTransformTest extends TestCase {

	public void testEscapeControlsUnchanged(){
		IsoControlEscaper transform = new IsoControlEscaper(false);
		String s = new String("Une chaîne accentuée");
		assertEquals(s,transform.transform(s));
	}

	public void testEscapeControlsStandardControlsAllowed(){
		IsoControlEscaper transform = new IsoControlEscaper(true);
		String s = new String("Backspace (\b), Form feed (\f), Newline (\n), Carriage return (\r), Tab (\t)");
		assertEquals(s,transform.transform(s));
	}
	
	public void testEscapeControlsStandardControlsNotAllowed(){
		IsoControlEscaper transform = new IsoControlEscaper(false);
		String s = new String("Backspace (\b), Form feed (\f), Newline (\n), Carriage return (\r), Tab (\t)");
		String expected = new String("Backspace (\\b), Form feed (\\f), Newline (\\n), Carriage return (\\r), Tab (\\t)");
		assertEquals(expected,transform.transform(s));
	}
	
	public void testEscapeControlsNonLatin1Characters(){
		IsoControlEscaper transform = new IsoControlEscaper(false);
		String s = new String("some latin1 characters : ©é, some non latin1 characters : ᆦ, some latin1 supplement : \u0092, some standard controls : \t\n\r\f");
		String expected = new String("some latin1 characters : ©é, some non latin1 characters : ᆦ, some latin1 supplement : \\u0092, some standard controls : \\t\\n\\r\\f");
		assertEquals(expected,transform.transform(s));
	}
	
}
