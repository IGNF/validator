package fr.ign.validator.cnig.cli;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import fr.ign.validator.cli.PreValidatorCnig;
import fr.ign.validator.tools.CompanionFileUtils;
import junit.framework.TestCase;

/**
 * 
 * @author FCerizay
 *
 */
public class PreValidatorCnigTest extends TestCase {

	public void testBadCall(){
		String[] args = new String[] { "--in", "notValid" };
		assertEquals(1,PreValidatorCnig.run(args));
	}
	
	public void testFindStrictEquals() throws IOException {
		File servitudeFile = new File(getClass().getResource("/SUP/SERVITUDE_041.TAB").getPath());

		String[] args = new String[] { "--input", servitudeFile.getAbsolutePath() };
		assertEquals(0,PreValidatorCnig.run(args));

		File idgestFile = new File(getClass().getResource("/SUP/idGest.txt").getPath());
		String idgest = FileUtils.readFileToString(idgestFile);
		assertEquals("131000", idgest);
		
		// check csv file deleted
		File csvFile = CompanionFileUtils.getCompanionFile(servitudeFile, "csv");
		assertFalse(csvFile.exists());
	}
}
