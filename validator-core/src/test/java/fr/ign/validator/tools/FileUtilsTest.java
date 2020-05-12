package fr.ign.validator.tools;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Collection;

import org.junit.Test;

public class FileUtilsTest {

	@Test
	public void testListFilesAndDirs(){
		File directory = ResourceHelper.getResourceFile(getClass(),"/config/geofla") ;
		String[] extensions = {"xml"};
		Collection<File> files = FileUtils.listFilesAndDirs(directory,extensions);
		assertEquals(4,files.size());		
	}
	
}
