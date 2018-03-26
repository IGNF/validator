package fr.ign.validator.string.transform;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import fr.ign.validator.string.transform.StringSimplifier;
import junit.framework.TestCase;

public class StringSimplifierTest extends TestCase {

	public void testReplace(){
		StringSimplifier replacer = new StringSimplifier();
		replacer.addReplacement("ê", "e");
		assertEquals("bete", replacer.transform("bête"));
	}
	

	public void testLoadCSVTestSample(){
		File file = new File(getClass().getResource("/replacer/sample.csv").getPath()) ;
		StringSimplifier replacer = new StringSimplifier();
		try {
			replacer.loadCSV(file);
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		Map<String, String> replacements = replacer.getReplacements();
		assertEquals( 2, replacements.size() );
		assertTrue( replacements.containsKey("ê") );
		assertTrue( replacements.containsKey("\u009c") ); // \\u should be evalued
	}
	

	public void testLoadCommon(){
		StringSimplifier replacer = new StringSimplifier();
		replacer.loadCommon();
		Map<String, String> replacements = replacer.getReplacements();
		assertTrue( replacements.size() >= 8 );
	}
	
	public void testLoadLatin1(){
		StringSimplifier replacer = new StringSimplifier();
		replacer.loadCharset(StandardCharsets.ISO_8859_1);
		Map<String, String> replacements = replacer.getReplacements();
		assertFalse( replacements.isEmpty() );
		assertTrue( replacements.size() >= 2 );
	}
	
	public void testTransformCommonAndLatin1(){
		StringSimplifier replacer = new StringSimplifier();
		// load order is important
		replacer.loadCommon();
		replacer.loadCharset(StandardCharsets.ISO_8859_1);
		
		assertEquals("oe", replacer.transform("\u009c"));
	}
}
