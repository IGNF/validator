package fr.ign.validator.cnig.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import fr.ign.validator.tools.CompanionFileUtils;
import fr.ign.validator.tools.ResourceHelper;

/**
 * 
 * @author FCerizay
 *
 */
public class CnigExtractIdgestCommandTest {

	@Test
	public void testBadCall(){
		CnigExtractIdgestCommand command = new CnigExtractIdgestCommand();
		String[] args = new String[] { "--in", "notValid" };
		assertEquals(1,command.run(args));
	}

	@Test
	public void testFindStrictEquals() throws IOException {
		File servitudeFile = ResourceHelper.getResourceFile(getClass(),"/SUP/SERVITUDE_041.TAB");

		CnigExtractIdgestCommand command = new CnigExtractIdgestCommand();
		String[] args = new String[] { "--input", servitudeFile.getAbsolutePath() };
		assertEquals(0,command.run(args));

		File idgestFile = ResourceHelper.getResourceFile(getClass(), "/SUP/idGest.txt");
		String idgest = FileUtils.readFileToString(idgestFile);
		assertEquals("131000", idgest);
		
		// check csv file deleted
		File csvFile = CompanionFileUtils.getCompanionFile(servitudeFile, "csv");
		assertFalse(csvFile.exists());
	}
}
