package fr.ign.validator.dgpr.command;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

/**
 * Test demo command
 */
public class HelloWorldCommandTest {

    @Test
    public void testBadCall() {
        HelloWorldCommand command = new HelloWorldCommand();
        String[] args = new String[] {
            "--nonoption", "plugin name"
        };
        assertEquals(1, command.run(args));
    }

    @Test
    public void testHelloWorld() throws IOException {
        HelloWorldCommand command = new HelloWorldCommand();

        String[] args = new String[] {
            "--option", "Hello Validator"
        };
        assertEquals(0, command.run(args));
    }

}
