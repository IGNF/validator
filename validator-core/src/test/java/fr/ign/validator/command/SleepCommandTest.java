package fr.ign.validator.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class SleepCommandTest {

    @Test
    public void testSleep1() throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(os);

        SleepCommand command = new SleepCommand();
        command.setStdout(out);
        String[] args = {
            "--duration", "1"
        };
        assertEquals(0, command.run(args));

        String result = os.toString(StandardCharsets.UTF_8);
        assertTrue(result.contains("sleep for 1 second(s)..."));
        assertTrue(result.contains("sleep for 1 second(s) : completed"));
    }

}
