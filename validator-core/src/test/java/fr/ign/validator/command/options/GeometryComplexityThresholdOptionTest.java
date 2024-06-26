package fr.ign.validator.command.options;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.Before;
import org.junit.Test;

import fr.ign.validator.geometry.GeometryComplexityThreshold;

public class GeometryComplexityThresholdOptionTest {

    private Options options;

    private CommandLineParser parser;

    @Before
    public void setUp() {
        options = new Options();
        GeometryComplexityThresholdOption.buildOptions(options);
        parser = new DefaultParser();
    }

    @Test
    public void testParsingOk() throws ParseException {
        String[] args = {
            "--cnig-complexity-tolerance",
            "[[-1, 600, 500, 0.1, 50000], [200000, 3000, 2000, 10, 50005]]"
        };
        CommandLine commandLine = parser.parse(options, args);
        GeometryComplexityThreshold result = GeometryComplexityThresholdOption.parseCustomOptions(commandLine);

        assertNotNull(result);

        assertEquals(-1, result.getWarningThreshold().getPointCount());
        assertEquals(600, result.getWarningThreshold().getPartCount());
        assertEquals(500, result.getWarningThreshold().getRingCount());
        assertTrue(0.1 == result.getWarningThreshold().getDensity());
        assertEquals(50000, result.getWarningThreshold().getRingPointCount());

        assertEquals(200000, result.getErrorThreshold().getPointCount());
        assertEquals(3000, result.getErrorThreshold().getPartCount());
        assertEquals(2000, result.getErrorThreshold().getRingCount());
        assertTrue(10 == result.getErrorThreshold().getDensity());
        assertEquals(50005, result.getErrorThreshold().getRingPointCount());
    }

    @Test
    public void testNoParam() throws ParseException {
        String[] args = {};
        CommandLine commandLine = parser.parse(options, args);
        GeometryComplexityThreshold complexityThreshold = GeometryComplexityThresholdOption.parseCustomOptions(
            commandLine
        );

        assertNull(complexityThreshold);
    }

    @Test(expected = ParseException.class)
    public void testBadRegexp1() throws ParseException {
        String[] args = {
            "--cnig-complexity-tolerance",
            "[[aea], [0, 10, 20, 30], baba]"
        };
        CommandLine commandLine = parser.parse(options, args);
        GeometryComplexityThresholdOption.parseCustomOptions(commandLine);
    }

    @Test(expected = ParseException.class)
    public void testBadRegexp2() throws ParseException {
        String[] args = {
            "--cnig-complexity-tolerance",
            "test mauvaise regexp"
        };
        CommandLine commandLine = parser.parse(options, args);
        GeometryComplexityThresholdOption.parseCustomOptions(commandLine);
    }

    @Test(expected = ParseException.class)
    public void testNotEnoughParam() throws ParseException {
        String[] args = {
            "--cnig-complexity-tolerance",
            "[[5000, 600, 0.1], [100]]"
        };

        CommandLine commandLine = parser.parse(options, args);
        GeometryComplexityThresholdOption.parseCustomOptions(commandLine);
    }

    @Test(expected = ParseException.class)
    public void testNotNumericParam() throws ParseException {
        String[] args = {
            "--cnig-complexity-tolerance",
            "[[5000, 600, 500, 0.1, 5000], [ab, 3000, 2000, 10, ab]]"
        };
        CommandLine commandLine = parser.parse(options, args);
        GeometryComplexityThresholdOption.parseCustomOptions(commandLine);
    }

}
