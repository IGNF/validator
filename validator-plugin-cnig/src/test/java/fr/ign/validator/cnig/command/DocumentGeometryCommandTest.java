package fr.ign.validator.cnig.command;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import fr.ign.validator.tools.ResourceHelper;

/**
 *
 * Test command DocumentGeometryCommand
 *
 * @author DDarras
 *
 */
public class DocumentGeometryCommandTest {

    DocumentGeometryCommand documentGeometryCommand = new DocumentGeometryCommand();

    @Test
    public void testBadCall() {
        DocumentGeometryCommand command = new DocumentGeometryCommand();
        String[] args = new String[] {
            "--in", "notValid"
        };
        assertEquals(1, command.run(args));
    }

    @Test
    public void testFindStrictEquals() throws IOException {
        File inFile = ResourceHelper.getResourceFile(getClass(), "/csv/DOC_URBA.csv");
        File outFile = ResourceHelper.getResourceFile(getClass(), "/csv/Document.csv");

        String inputString = inFile.getAbsolutePath();
        String outString = outFile.getAbsolutePath();

        DocumentGeometryCommand command = new DocumentGeometryCommand();
        String[] args = new String[] {
            "--input", inputString,
            "--output", outString
        };
        assertEquals(0, command.run(args));

        // Verifie l'output
        String outContent = FileUtils.readFileToString(outFile, StandardCharsets.UTF_8);
        assertEquals("131000", outContent);
    }

    @Test
    public void testParseFileOption() throws ParseException {
        // Empty option
        String mockEmptyString = "";
        ParseException emptyException = assertThrows(ParseException.class,
            () -> documentGeometryCommand.parseFileOption(mockEmptyString));
        assertEquals("Input is empty", emptyException.getMessage());

        // Invalid File location
        String mockInvalidString = "Invalid File Location";
        ParseException invalidException = assertThrows(ParseException.class,
            () -> documentGeometryCommand.parseFileOption(mockInvalidString));
        assertEquals("'Invalid File Location' cannot be found", invalidException.getMessage());

        // Correct option
        File urbaFile = ResourceHelper.getResourceFile(getClass(), "/geometry/fair_geometries.csv");
        File secteurFile = ResourceHelper.getResourceFile(getClass(), "/geometry/dubious_geometries.csv");
        File[] expected = {urbaFile, secteurFile};
        String urbaLocation = urbaFile.getAbsolutePath();
        String secteurLocation = secteurFile.getAbsolutePath();
        String mockCorrectString = urbaLocation + ", " + secteurLocation;
        assertArrayEquals(expected,
            documentGeometryCommand.parseFileOption(mockCorrectString).toArray());
    }

    @Test
    public void testParseGeometryOption(){
        // absent option
        String[] expectedAbsent = {"geom", "geometry"};
        assertArrayEquals(expectedAbsent,
            documentGeometryCommand.parseGeometryOption("", false).toArray());

        // present option
        String geometryColumnNameString = "column1, column2";
        String[] expectedPresent = {"geom", "geometry", "column1", "column2"};
        assertArrayEquals(expectedPresent,
            documentGeometryCommand.parseGeometryOption(geometryColumnNameString, true).toArray());
    }
}
