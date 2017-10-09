package fr.ign.validator.string.transform;

import junit.framework.TestCase;

public class DoubleUtf8DecoderTest extends TestCase {

	public void testNotDoubleEncoded(){
		DoubleUtf8Decoder transform = new DoubleUtf8Decoder();
		assertEquals( "correctement encodée", transform.transform("correctement encodée") ) ;
	}
	
	public void testDoubleEncoded(){
		String doubleEncoded = DoubleUtf8Decoder.utf8DeclaredAsLatin1("correctement encodée");
		DoubleUtf8Decoder transform = new DoubleUtf8Decoder();
		assertEquals( "correctement encodée", transform.transform(doubleEncoded) ) ;		
	}
}
