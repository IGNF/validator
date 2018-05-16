package fr.ign.validator.command;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.rules.TemporaryFolder;

import junit.framework.TestCase;

/**
 * 
 * @author FCerizay
 *
 */
public class MetadataToJsonCommandRegressTest extends TestCase {

	protected TemporaryFolder testFolder ;
	
	@Override
	protected void setUp() throws Exception {
		testFolder = new TemporaryFolder();
		testFolder.create();
	}
	
	@Override
	protected void tearDown() throws Exception {
		testFolder.delete();
	}
	
	public void testBadCall(){
		MetadataToJsonCommand command = new MetadataToJsonCommand();
		String[] args = new String[] { "--in", "notValid" };
		assertEquals(1,command.run(args));
	}

	protected File getSampleMetadataDir(){
		return new File(getClass().getResource("/metadata/").getPath());
	}
	
	protected void performRegressTest(String inputName, String expectedName) throws IOException {
		File inputFile    = new File(getSampleMetadataDir(),inputName);
		File expectedFile = new File(getSampleMetadataDir(),expectedName);
		File outputFile = testFolder.newFile("result.json");

		MetadataToJsonCommand command = new MetadataToJsonCommand();

		String[] args = new String[] { 
			"--input", inputFile.getAbsolutePath(),
			"--output", outputFile.getAbsolutePath() 
		};
		assertEquals(0,command.run(args));
		
		assertTrue("json file not produced for "+inputName, outputFile.exists());
		// uncomment to get expected content
		// System.out.println(FileUtils.readFileToString(outputFile));
		assertTrue(
			"Expected file not found : "+expectedFile,
			expectedFile.exists()
		);
		assertEquals(
			FileUtils.readFileToString(expectedFile).trim(), 
			FileUtils.readFileToString(outputFile).trim()
		);
	}
	
	public void testConvert01() throws IOException {
		performRegressTest("01.xml","01-expected.json");
	}

	public void testConvert02() throws IOException {
		performRegressTest("02.xml","02-expected.json");
	}
	
	public void testConvert03() throws IOException {
		performRegressTest("03.xml","03-expected.json");
	}
	
	public void testConvert04() throws IOException {
		performRegressTest("04.xml","04-expected.json");
	}
}
