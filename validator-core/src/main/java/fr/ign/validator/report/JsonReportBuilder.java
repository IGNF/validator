package fr.ign.validator.report;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.ign.validator.error.ValidatorError;

/**
 * Create a JSON report file (experimental, ValidatorError serialization has to be improved)
 * 
 * @author MBorne
 */
public class JsonReportBuilder implements ReportBuilder, Closeable {

	private PrintStream out ;

	private ObjectMapper mapper = new ObjectMapper();
	
	public JsonReportBuilder(File file) {
		if ( file.exists() ){
			file.delete();
		}
		try {
			this.out = new PrintStream(file);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	
	@Override
	public void addError(ValidatorError error) {
		try {
			out.println(mapper.writeValueAsString(error));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void close() throws IOException {
		out.close();
	}

}
