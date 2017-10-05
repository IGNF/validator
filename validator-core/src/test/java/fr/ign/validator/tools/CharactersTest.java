package fr.ign.validator.tools;

import junit.framework.TestCase;

public class CharactersTest extends TestCase {

	public void testToHexa(){
		assertEquals( "\\u00ff", Characters.toHexa(255) );
	}
	
}
