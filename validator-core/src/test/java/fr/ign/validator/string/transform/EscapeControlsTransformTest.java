package fr.ign.validator.string.transform;

import org.junit.Assert;
import org.junit.Test;

public class EscapeControlsTransformTest {

	@Test
	public void testEscapeControlsUnchanged(){
		IsoControlEscaper transform = new IsoControlEscaper(false);
		String s = new String("Une chaîne accentuée");
		Assert.assertEquals(s,transform.transform(s));
	}

	@Test	
	public void testEscapeControlsStandardControlsAllowed(){
		IsoControlEscaper transform = new IsoControlEscaper(true);
		String s = new String("Backspace (\b), Form feed (\f), Newline (\n), Carriage return (\r), Tab (\t)");
		Assert.assertEquals(s,transform.transform(s));
	}

	@Test
	public void testEscapeControlsStandardControlsNotAllowed(){
		IsoControlEscaper transform = new IsoControlEscaper(false);
		String s = new String("Backspace (\b), Form feed (\f), Newline (\n), Carriage return (\r), Tab (\t)");
		String expected = new String("Backspace (\\b), Form feed (\\f), Newline (\\n), Carriage return (\\r), Tab (\\t)");
		Assert.assertEquals(expected,transform.transform(s));
	}

	@Test
	public void testEscapeControlsNonLatin1Characters(){
		IsoControlEscaper transform = new IsoControlEscaper(false);
		String s = new String("some latin1 characters : ©é, some non latin1 characters : ᆦ, some latin1 supplement : \u0092, some standard controls : \t\n\r\f");
		String expected = new String("some latin1 characters : ©é, some non latin1 characters : ᆦ, some latin1 supplement : \\u0092, some standard controls : \\t\\n\\r\\f");
		Assert.assertEquals(expected,transform.transform(s));
	}
	
}
