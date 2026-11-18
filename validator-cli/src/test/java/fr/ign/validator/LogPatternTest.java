package fr.ign.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.message.SimpleMessage;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * validator-debug.log is returned to the users : the absolute paths of the
 * server must not appear in it (message and exception), the SQL queries and the
 * URLs must be kept.
 */
public class LogPatternTest {

    private PatternLayout layout;

    @Before
    public void setUp() throws Exception {
        // the pattern of the FILE appender of the log4j2.xml embedded in the jar
        String pattern;
        try (InputStream in = getClass().getResourceAsStream("/log4j2.xml")) {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
            pattern = document.getElementsByTagName("Pattern").item(0).getTextContent();
        }
        layout = PatternLayout.newBuilder().withPattern(pattern).withCharset(StandardCharsets.UTF_8).build();
    }

    private String format(String message, Throwable thrown) {
        LogEvent event = Log4jLogEvent.newBuilder()
            .setLoggerName("test")
            .setLevel(Level.DEBUG)
            .setMessage(new SimpleMessage(message))
            .setThrown(thrown)
            .build();
        return layout.toSerializable(event);
    }

    @Test
    public void testAbsolutePathsAreReduced() {
        String line = format("Create file /srv/validator/work/42/out/DOC/DATA/ZONE_URBA.csv ...", null);
        assertTrue(line, line.contains("Create file .../ZONE_URBA.csv ..."));
        assertFalse(line, line.contains("/srv"));
    }

    @Test
    public void testFileUri() {
        String line = format("Loading from file:/home/validator/config/PLU/files.json ...", null);
        assertTrue(line, line.contains("Loading from file:.../files.json ..."));
        assertFalse(line, line.contains("/home"));
    }

    @Test
    public void testSeveralPathsInTheSameMessage() {
        String line = format("rename /tmp/a/b/x.csv to /tmp/a/b/y.csv...", null);
        assertTrue(line, line.contains("rename .../x.csv to .../y.csv..."));
    }

    @Test
    public void testExceptionMessageIsReduced() {
        String line = format("failure", new FileNotFoundException("/srv/validator/work/42/missing.csv (No such file)"));
        assertTrue(line, line.contains("FileNotFoundException: .../missing.csv (No such file)"));
        assertFalse(line, line.contains("/srv"));
    }

    @Test
    public void testSqlQueriesUrlsAndRelativePathsAreKept() {
        String sql = "SELECT count(*)/2 AS n, a FROM zone_urba WHERE b = 'x' AND c IN (1,2) LIMIT 1";
        assertTrue(format(sql, null).contains(sql));

        String url = "found (https://www.geoportail-urbanisme.gouv.fr/document/130009301)";
        assertTrue(format(url, null).contains(url));

        String relative = "Read DATA/ZONE_URBA.csv";
        assertTrue(format(relative, null).contains(relative));
    }

    @Test
    public void testLineFormat() {
        String line = format("hello", null);
        assertTrue(line, line.matches("(?s)\\d{4}-\\d{2}-\\d{2} .* DEBUG test \\[\\] hello\\R.*"));
    }

}
