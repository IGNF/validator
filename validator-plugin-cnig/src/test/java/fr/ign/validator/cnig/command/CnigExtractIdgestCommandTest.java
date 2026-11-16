package fr.ign.validator.cnig.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import fr.ign.validator.tools.CompanionFileUtils;
import fr.ign.validator.tools.ResourceHelper;

/**
 *
 * Test command CnigExtractIdgestCommand
 *
 * @author FCerizay
 *
 */
public class CnigExtractIdgestCommandTest {

    @Test
    public void testBadCall() {
        CnigExtractIdgestCommand command = new CnigExtractIdgestCommand();
        String[] args = new String[] {
            "--in", "notValid"
        };
        // expected error output is captured (clean test output)
        PrintStream stderr = System.err;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setErr(new PrintStream(captured, true, StandardCharsets.UTF_8));
        try {
            assertEquals(1, command.run(args));
        } finally {
            System.setErr(stderr);
        }
        assertTrue(captured.toString(StandardCharsets.UTF_8).contains("fail to read IdGest from notValid"));
    }

    @Test
    public void testFindStrictEquals() throws IOException {
        File servitudeFile = ResourceHelper.getResourceFile(getClass(), "/SUP/SERVITUDE_041.TAB");

        CnigExtractIdgestCommand command = new CnigExtractIdgestCommand();
        String[] args = new String[] {
            "--input", servitudeFile.getAbsolutePath()
        };
        assertEquals(0, command.run(args));

        File idgestFile = ResourceHelper.getResourceFile(getClass(), "/SUP/idGest.txt");
        String idgest = FileUtils.readFileToString(idgestFile, StandardCharsets.UTF_8);
        assertEquals("131000", idgest);

        // check csv file deleted
        File csvFile = CompanionFileUtils.getCompanionFile(servitudeFile, "csv");
        assertFalse(csvFile.exists());
    }
}
