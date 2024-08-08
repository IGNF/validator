package fr.ign.validator.command;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.skyscreamer.jsonassert.JSONAssert;

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
    public void testConvert01() throws Exception {
        performRegressTest("01.xml", "01-expected.json");
    }

    @Test
    public void testConvert02() throws Exception {
        performRegressTest("02.xml", "02-expected.json");
    }

    @Test
    public void testConvert03() throws Exception {
        performRegressTest("03.xml", "03-expected.json");
    }

    @Test
    public void testConvert04() throws Exception {
        performRegressTest("04.xml", "04-expected.json");
    }

    protected File getSampleMetadataDir() {
        return ResourceHelper.getResourceFile(getClass(), "/metadata/");
    }

    protected void performRegressTest(String inputName, String expectedName) throws IOException, JSONException {
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
        Assert.assertTrue("Expected file not found : " + expectedFile, expectedFile.exists());

        String expected = FileUtils.readFileToString(expectedFile, StandardCharsets.UTF_8);
        String actual = FileUtils.readFileToString(outputFile, StandardCharsets.UTF_8);
        JSONAssert.assertEquals(expected, actual, false);
    }

}
