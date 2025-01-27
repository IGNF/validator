package fr.ign.validator.cnig.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import fr.ign.validator.tools.CompanionFileUtils;
import fr.ign.validator.tools.ResourceHelper;

/**
 *
 * Test command DocumentGeometryCommand
 *
 * @author DDarras
 *
 */
public class DocumentGeometryCommandTest {

    @Test
    public void testBadCall() {
        DocumentGeometryCommand command = new DocumentGeometryCommand();
        String[] args = new String[] {
            "--in", "notValid"
        };
        assertEquals(1, command.run(args));
    }

    @Test
    public void testFindStrictEquals() throws IOException {
        
    }
}
