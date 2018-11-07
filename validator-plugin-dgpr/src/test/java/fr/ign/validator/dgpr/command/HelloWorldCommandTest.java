package fr.ign.validator.dgpr.command;

import java.io.IOException;

import junit.framework.TestCase;

/**
 *
 */
public class HelloWorldCommandTest extends TestCase {

	public void testBadCall(){
		HelloWorldCommand command = new HelloWorldCommand();
		String[] args = new String[] { "--nonoption", "plugin name" };
		assertEquals(1, command.run(args));
	}

	public void testHelloWorld() throws IOException {
		HelloWorldCommand command = new HelloWorldCommand();

		String[] args = new String[] { "--option", "Hello Validator" };
		assertEquals(0, command.run(args));
	}

}
