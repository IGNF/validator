package fr.ign.validator.reader;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import fr.ign.validator.exception.InvalidMetadataException;
import junit.framework.TestCase;

public class MetatadaReaderTest extends TestCase {
	
	@Test
	public void test50403plu20120425(){
		File file = new File(getClass().getResource("/metadata/fr-210800405-50403plu20120425.xml").getPath()) ;
		try {
			MetadataReader reader = new MetadataReader(file);
			assertEquals( "fr-210800405-08042plu20140213.xml", reader.getFileIdentifier() ) ;
			assertEquals( "fr-210800405-08042plu20140213", reader.getMDIdentifier() ) ;
			assertEquals( StandardCharsets.UTF_8, reader.getCharacterSetCode() ) ;
		} catch (InvalidMetadataException e) {
			fail(e.getMessage());
		}
	}
	
	public void testWithoutMDIdentifier(){
		File file = new File(getClass().getResource("/metadata/fr-210800405-08042-plu20140213-without-MD_Identifier.xml").getPath()) ;
		try {
			MetadataReader reader = new MetadataReader(file);
			assertNull( reader.getMDIdentifier() ) ;
		} catch (InvalidMetadataException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void test35002plu20111215(){
		File file = new File(getClass().getResource("/metadata/fr-213500028-35002plu20111215.xml").getPath()) ;
		try {
			MetadataReader reader = new MetadataReader(file);
			assertEquals( "fr-213500028-35002plu20111215", reader.getFileIdentifier() ) ;
			assertEquals( "fr-213500028-35002plu20111215", reader.getMDIdentifier() ) ;
			assertEquals( StandardCharsets.ISO_8859_1, reader.getCharacterSetCode() ) ;
		} catch (InvalidMetadataException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void test41158plu20130225(){
		File file = new File(getClass().getResource("/metadata/fr-210800405-41158plu20130225.xml").getPath()) ;
		try {
			MetadataReader reader = new MetadataReader(file);
			assertEquals( "fr-210800405-41158plu20130225", reader.getFileIdentifier() ) ;
			assertEquals( "fr-210800405-08042plu20140213", reader.getMDIdentifier() ) ;
			//Note : file encoding = ISO-8859-1, data-encoding=UTF-8
			assertEquals( StandardCharsets.ISO_8859_1, reader.getCharacterSetCode() ) ;
		} catch (InvalidMetadataException e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void test08042plu20140213(){
		File file = new File(getClass().getResource("/metadata/fr-210800405-08042-plu20140213.xml").getPath()) ;
		try {
			MetadataReader reader = new MetadataReader(file);
			assertEquals( "fr-210800405-08042-plu20140213", reader.getFileIdentifier() ) ;
			assertEquals( "fr-210800405-08042plu20140213", reader.getMDIdentifier() ) ;
			//Note : file encoding = ISO-8859-1, data-encoding=UTF-8
			assertEquals( StandardCharsets.ISO_8859_1, reader.getCharacterSetCode() ) ;
		} catch (InvalidMetadataException e) {
			fail(e.getMessage());
		}
	}
	

	@Test
	public void test27676pos20100611(){
		File file = new File(getClass().getResource("/metadata/fr-210800405-27676pos20100611.xml").getPath()) ;
		try {
			MetadataReader reader = new MetadataReader(file);
			assertEquals( "fr-210800405-27676pos20100611", reader.getFileIdentifier() ) ;
			assertEquals( "fr-210800405-08042plu20140213", reader.getMDIdentifier() ) ;
			//Note : file encoding = ISO-8859-1, data-encoding=UTF-8
			assertEquals( StandardCharsets.ISO_8859_1, reader.getCharacterSetCode() ) ;
		} catch (InvalidMetadataException e) {
			fail(e.getMessage());
		}
	}
	
	
	@Test
	public void test50553plui20140217(){
		File file = new File(getClass().getResource("/metadata/fr-245000468-50553plui20140217.xml").getPath()) ;
		try {
			MetadataReader reader = new MetadataReader(file);
			assertEquals( "fr-245000468-50553plui20140217", reader.getFileIdentifier() ) ;
			assertEquals( "fr-245000468-50553plui20140217.xml", reader.getMDIdentifier() ) ;			
			assertTrue( MetadataReader.isMetadataFile(file) ) ;
			
			assertNull( reader.getCharacterSetCode() ) ;
			assertFalse( null == reader.getFileIdentifier() );
			
		} catch (InvalidMetadataException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testMissingCharsetIsNull(){
		File file = new File(getClass().getResource("/metadata/missing-charset.xml").getPath()) ;
		try {
			MetadataReader reader = new MetadataReader(file);
			assertNull( reader.getCharacterSetCode() ) ;
		} catch (InvalidMetadataException e) {
			fail(e.getMessage());
		}
	}
	
	
	public void testIsMetadataFile(){
		File okFile = new File(getClass().getResource("/metadata/missing-charset.xml").getPath()) ;
		File nokFile = new File(getClass().getResource("/metadata/not-a-metadatafile.xml").getPath()) ;
		
		assertTrue( MetadataReader.isMetadataFile(okFile) ) ;
		assertFalse( MetadataReader.isMetadataFile(nokFile) ) ;
	}
	
	public void test000053015plu20140908(){
		File file = new File(getClass().getResource("/metadata/fr-000053015-plu20140908.xml").getPath()) ;
		try {
			MetadataReader reader = new MetadataReader(file);
			assertEquals( "urn:isogeo:metadata:uuid:66484d70-3f8c-44cf-b0e5-98ac84426a2c", reader.getFileIdentifier() ) ;
			assertEquals( "66484d70-3f8c-44cf-b0e5-98ac84426a2c", reader.getMDIdentifier() ) ;
		} catch (InvalidMetadataException e) {
			fail(e.getMessage());
		}
	}
}
