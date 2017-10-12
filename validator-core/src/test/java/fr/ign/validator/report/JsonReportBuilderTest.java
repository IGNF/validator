package fr.ign.validator.report;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ErrorLevel;
import fr.ign.validator.error.ErrorScope;
import fr.ign.validator.error.ValidatorError;

public class JsonReportBuilderTest {

	@Rule
	public TemporaryFolder folder= new TemporaryFolder();
	
	@Test
	public void test01() throws IOException {
		File file = folder.newFile("report.jsonl");
		JsonReportBuilder report = new JsonReportBuilder(file);
		
		{
			ValidatorError error = new ValidatorError(CoreErrorCodes.ATTRIBUTE_FILE_NOT_FOUND);
			error.setLevel(ErrorLevel.DEBUG);
			error.setScope(ErrorScope.DIRECTORY);
			error.setMessage("message 1");
			report.addError(error);
		}
		{
			ValidatorError error = new ValidatorError(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID);
			error.setLevel(ErrorLevel.DEBUG);
			error.setScope(ErrorScope.DIRECTORY);
			error.setMessage("message 2");
			report.addError(error);
		}
		
		report.close();
		
		File expectedFile = new File(getClass().getResource("/report/report-01.jsonl").getPath());
	
		@SuppressWarnings("unchecked")
		List<String> lines = FileUtils.readLines(file) ;
		assertEquals(2, lines.size());
		assertEquals(
			FileUtils.readLines(expectedFile),
			lines
		);
	}

}
