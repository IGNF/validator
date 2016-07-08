package fr.ign.validator.tools;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class CharsetDetectorTest {
	
	@Test
	public void testDetectUTF8() throws IOException {
		File srcFile = new File(getClass().getResource("/csv/sample-utf8.csv").getPath()) ;
		Charset charset = CharsetDetector.detectCharset(srcFile) ;
		assertEquals(StandardCharsets.UTF_8, charset);
	}
	
	
	@Test
	public void testDetectLATIN1() throws IOException {
		File srcFile = new File(getClass().getResource("/csv/sample-latin1.csv").getPath()) ;
		Charset charset = CharsetDetector.detectCharset(srcFile) ;
		assertEquals(StandardCharsets.ISO_8859_1, charset);
	}
	
	@Test
	public void testBisDetectUTF8() throws IOException {
		File srcFile = new File(getClass().getResource("/csv/AC2_ASSIETTE_SUP_S_014_utf8.csv").getPath()) ;
		Charset charset = CharsetDetector.detectCharset(srcFile) ;
		assertEquals(StandardCharsets.UTF_8, charset);
	}
	
	
	@Test
	public void testBisDetectLATIN1() throws IOException {
		File srcFile = new File(getClass().getResource("/csv/AC2_ASSIETTE_SUP_S_014_latin1.csv").getPath()) ;
		Charset charset = CharsetDetector.detectCharset(srcFile) ;
		assertEquals(StandardCharsets.ISO_8859_1, charset);
	}
	
	@Test
	public void testGenerateurDetectUTF8() throws IOException {
		File srcFile = new File(getClass().getResource("/csv/AC2_GENERATEUR_SUP_S_014_utf8.csv").getPath()) ;
		Charset charset = CharsetDetector.detectCharset(srcFile) ;
		assertEquals(StandardCharsets.UTF_8, charset);
	}
	

	
}
