package fr.ign.validator.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import junit.framework.TestCase;

/**
 * 
 * @author FCerizay
 *
 */
public class IdgestExtractorTest extends TestCase {

	private IdgestExtractor extractor ;
	
	@Override
	protected void setUp() throws Exception {
		File servitudeFile = new File( 
			getClass().getResource("/SUP/SERVITUDE_041.TAB").getPath() 
		);
		extractor = new IdgestExtractor(servitudeFile) ;
	}
	
	
	
	public void testFindStrictEquals() throws IOException{
		
		extractor.findIdGest();
		
		File idgestFile = new File( 
				getClass().getResource("/SUP/idGest.txt").getPath() 
			);
		
		FileReader fr = new FileReader(idgestFile);
		BufferedReader br = new BufferedReader(fr);
		assertEquals("131000", br.readLine());
		
		br.close();
	}
}
