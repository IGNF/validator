package fr.ign.validator.command;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.tools.ResourceHelper;

/**
 * Regress test for metadata_to_json command
 * 
 * @author FCerizay
 */
public class MetadataToJsonCommandRegressTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void testBadCall() {
        MetadataToJsonCommand command = new MetadataToJsonCommand();
        String[] args = new String[] {
            "--in", "notValid"
        };
        Assert.assertEquals(1, command.run(args));
    }

    @Test
    public void testConvert01() throws IOException {
        performRegressTest("01.xml", "01-expected.json");
    }

    @Test
    public void testConvert02() throws IOException {
        performRegressTest("02.xml", "02-expected.json");
    }

    @Test
    public void testConvert03() throws IOException {
        performRegressTest("03.xml", "03-expected.json");
    }

    @Test
    public void testConvert04() throws IOException {
        performRegressTest("04.xml", "04-expected.json");
    }

    protected File getSampleMetadataDir() {
        return ResourceHelper.getResourceFile(getClass(), "/metadata/");
    }

    protected void performRegressTest(String inputName, String expectedName) throws IOException {
        File inputFile = new File(getSampleMetadataDir(), inputName);
        File expectedFile = new File(getSampleMetadataDir(), expectedName);
        File outputFile = testFolder.newFile("result.json");

        MetadataToJsonCommand command = new MetadataToJsonCommand();

        String[] args = new String[] {
            "--input", inputFile.getAbsolutePath(),
            "--output", outputFile.getAbsolutePath()
        };
        Assert.assertEquals(0, command.run(args));

        Assert.assertTrue("json file not produced for " + inputName, outputFile.exists());
        // uncomment to get expected content
        // System.out.println(FileUtils.readFileToString(outputFile));
        Assert.assertTrue(
            "Expected file not found : " + expectedFile,
            expectedFile.exists()
        );
        Assert.assertEquals(
            FileUtils.readFileToString(expectedFile).trim(),
            FileUtils.readFileToString(outputFile).trim()
        );
    }

}
