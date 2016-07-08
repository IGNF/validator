package fr.ign.validator.tools;

import java.io.File;
import java.util.Collection;

import org.junit.Test;

import junit.framework.TestCase;

public class FileUtilsTest extends TestCase {

	@Test
	public void testListFilesAndDirs(){
		File directory = new File(getClass().getResource("/geofla").getPath()) ;
		String[] extensions = {"xml"};
		Collection<File> files = FileUtils.listFilesAndDirs(directory,extensions);
		assertEquals(4,files.size());		
	}
	
}
