package fr.ign.validator.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class DocumentValidatorCommandTest {

    @Test
    public void testHelp() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(os);

        DocumentValidatorCommand command = new DocumentValidatorCommand();
        command.setStdout(out);
        String[] args = {
            "--help"
        };
        assertEquals(0, command.run(args));

        String result = os.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("usage: document_validator"));
        assertTrue(result.contains("--input"));
    }

}
