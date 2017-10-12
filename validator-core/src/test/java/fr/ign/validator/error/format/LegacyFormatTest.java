package fr.ign.validator.error.format;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.error.ErrorLevel;
import fr.ign.validator.error.ErrorScope;
import fr.ign.validator.error.ValidatorError;

public class LegacyFormatTest {
	
	private LegacyFormat format ;
	
	@Before
	public void setUp() {
		format = new LegacyFormat();
	}
	
	@Test
	public void testDirectory(){
		ValidatorError error = new ValidatorError(ErrorCode.valueOf("FAKE"));
		error.setScope(ErrorScope.DIRECTORY);
		error.setLevel(ErrorLevel.WARNING);
		error.setDocumentModel("my_standard");
		error.setFile("my_file");
		error.setMessage("my message");
		assertEquals("Directory | FAKE | WARNING | my_file | my message",format.write(error));
	}
	
	@Test
	public void testDirectoryWithEmpty(){
		ValidatorError error = new ValidatorError(ErrorCode.valueOf("FAKE"));
		error.setScope(ErrorScope.DIRECTORY);
		error.setLevel(ErrorLevel.WARNING);
		error.setDocumentModel("my_standard");
		error.setFile("");
		error.setMessage("my message");
		assertEquals("Directory | FAKE | WARNING |  | my message",format.write(error));
	}
	
	@Test
	public void testDirectoryWithNulls(){
		ValidatorError error = new ValidatorError(ErrorCode.valueOf("FAKE"));
		error.setScope(ErrorScope.DIRECTORY);
		error.setLevel(ErrorLevel.WARNING);
		error.setDocumentModel("my_standard");
		error.setFile(null);
		error.setMessage("my message");
		// FIXME null should be replaced by empty string
		assertEquals("Directory | FAKE | WARNING | null | my message",format.write(error));
	}

}
