package fr.ign.validator.tools;

import junit.framework.TestCase;

public class CharactersTest extends TestCase {

	public void testToHexa(){
		assertEquals( "\\u00ff", Characters.toHexa(255) );
	}
	
	public void testEscapeControlsUnchanged(){
		String s = new String("Une chaîne accentuée");
		assertEquals(s,Characters.escapeControls(s,false));
	}

	public void testEscapeControlsStandardControlsAllowed(){
		String s = new String("Backspace (\b), Form feed (\f), Newline (\n), Carriage return (\r), Tab (\t)");
		assertEquals(s,Characters.escapeControls(s,true));
	}
	
	public void testEscapeControlsStandardControlsNotAllowed(){
		String s = new String("Backspace (\b), Form feed (\f), Newline (\n), Carriage return (\r), Tab (\t)");
		String expected = new String("Backspace (\\b), Form feed (\\f), Newline (\\n), Carriage return (\\r), Tab (\\t)");
		assertEquals(expected,Characters.escapeControls(s,false));
	}
	
	public void testEscapeControlsNonLatin1Characters(){
		String s = new String("some latin1 characters : ©é, some non latin1 characters : ᆦ, some latin1 supplement : \u0092, some standard controls : \t\n\r\f");
		String expected = new String("some latin1 characters : ©é, some non latin1 characters : ᆦ, some latin1 supplement : \\u0092, some standard controls : \\t\\n\\r\\f");
		assertEquals(expected,Characters.escapeControls(s,false));
	}

	public void testEscapeNonLatin1Characters(){
		String s = new String("some latin1 characters : ©é, some non latin1 characters : ᆦ, some latin1 supplement : \u0092, some standard controls : \t\n\r\f");
		String expected = new String("some latin1 characters : ©é, some non latin1 characters : \\u11a6, some latin1 supplement : \u0092, some standard controls : \t\n\r\f");
		assertEquals(expected,Characters.escapeNonLatin1(s));
	}
}
