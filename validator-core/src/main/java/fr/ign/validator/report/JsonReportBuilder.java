package fr.ign.validator.report;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.ign.validator.error.ValidatorError;

/**
 * Create a JSON report file (experimental, ValidatorError serialization has to
 * be improved)
 *
 * @author MBorne
 */
public class JsonReportBuilder implements ReportBuilder, Closeable {

    private File file;

    private PrintStream out;

    private ObjectMapper mapper = new ObjectMapper();

    public JsonReportBuilder(File file) {
        this.file = file;
        if (file.exists()) {
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

    /**
     * Close the report.
     *
     * A PrintStream never throws an exception on a write error (ex : disk full) :
     * the error is checked here to avoid a truncated report with a successful
     * validation.
     *
     * @throws IOException if the report is incomplete
     */
    @Override
    public void close() throws IOException {
        boolean error = out.checkError();
        out.close();
        if (error || out.checkError()) {
            throw new IOException("Fail to write the validation report " + file + " (disk full?)");
        }
    }

}
