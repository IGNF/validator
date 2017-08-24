package fr.ign.validator.model.type;


import java.io.File;

import junit.framework.TestCase;

public class FilenameTypeTest extends TestCase {

	private FilenameType filenameType = new FilenameType() ;
	
	public void testBindFormatWithFragment(){
		String name = new String("a-file.txt#page=12");
		File binded = filenameType.bind(name);
		assertEquals(name, filenameType.format(binded) );
	}
	
	public void testBindFormatWithSpecialChars(){
		char c = 0x0092;
		String name = new String("a-file.txt#page=12"+c);
		File binded = filenameType.bind(name);
		// should reproduce "Malformed input or input contains unmappable chacraters" on some system
		assertEquals(name, filenameType.format(binded) );
	}
}

