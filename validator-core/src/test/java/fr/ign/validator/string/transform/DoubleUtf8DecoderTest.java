package fr.ign.validator.string.transform;

import org.junit.Assert;
import org.junit.Test;


public class DoubleUtf8DecoderTest {

	@Test
	public void testNotDoubleEncoded(){
		DoubleUtf8Decoder transform = new DoubleUtf8Decoder();
		Assert.assertEquals( "correctement encodée", transform.transform("correctement encodée") ) ;
	}

	@Test
	public void testDoubleEncoded(){
		String doubleEncoded = DoubleUtf8Decoder.utf8DeclaredAsLatin1("correctement encodée");
		DoubleUtf8Decoder transform = new DoubleUtf8Decoder();
		Assert.assertEquals( "correctement encodée", transform.transform(doubleEncoded) ) ;		
	}

}
