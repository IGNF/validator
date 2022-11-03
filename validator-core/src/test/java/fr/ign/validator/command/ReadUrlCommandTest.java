package fr.ign.validator.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import fr.ign.validator.tools.Networking;

public class ReadUrlCommandTest {

    @Test
    public void testReadGithub() throws IOException {
        Networking.configureHttpClient();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(os);

        ReadUrlCommand command = new ReadUrlCommand();
        command.setStdout(out);
        String[] args = {
            "--url", "https://raw.githubusercontent.com/IGNF/validator/master/README.md"
        };
        assertEquals(0, command.run(args));

        String result = os.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("This program validates a dataset"));
    }

}
