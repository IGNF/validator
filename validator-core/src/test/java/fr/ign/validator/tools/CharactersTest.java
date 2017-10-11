package fr.ign.validator.tools;

import junit.framework.TestCase;

public class CharactersTest extends TestCase {

	
	public void testToHexa(){
		assertEquals( "\\u00ff", Characters.toHexa(255) );
	}
	
	public void testToUri(){
		assertEquals( "http://www.fileformat.info/info/unicode/char/0fb9/index.htm", Characters.toURI(4025) );
	}
	
}
