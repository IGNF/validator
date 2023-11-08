package fr.ign.validator.command.options;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.geotools.geometry.jts.WKTWriter2;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;

import fr.ign.validator.tools.ResourceHelper;

public class DocumentEmpriseOptionTest {

    private Options options;

    private CommandLineParser parser;

    @Before
    public void setUp() {
        options = new Options();
        DocumentEmpriseOption.buildOptions(options);
        parser = new DefaultParser();
    }

    @Test
    public void testWKTParsingOk() throws ParseException {
        String[] args = {
            "--cnig-document-emprise",
            "POLYGON((1.186438253 47.90390196, 1.098884284 47.90390196, 1.098884284 47.83421197, 1.186438253 47.83421197, 1.186438253 47.90390196))"
        };
        CommandLine commandLine = parser.parse(options, args);
        Geometry result = DocumentEmpriseOption.parseCustomOptions(commandLine);

        assertNotNull(result);

        WKTWriter2 writer = new WKTWriter2();
        String wkt = writer.write(result);

        assertEquals("POLYGON ((1.186438253 47.90390196, 1.098884284 47.90390196, 1.098884284 47.83421197, 1.186438253 47.83421197, 1.186438253 47.90390196))", wkt);
    }

    @Test
    public void testFileParsingOk() throws ParseException {

        File emprisePath = ResourceHelper.getResourceFile(getClass(), "/command-options/emprise.wkt");
        String[] args = {
            "--cnig-document-emprise",
            emprisePath.getAbsolutePath()
        };
        CommandLine commandLine = parser.parse(options, args);
        Geometry result = DocumentEmpriseOption.parseCustomOptions(commandLine);

        assertNotNull(result);

        WKTWriter2 writer = new WKTWriter2();
        String wkt = writer.write(result);

        assertEquals("POLYGON ((1.186438253 47.90390196, 1.098884284 47.90390196, 1.098884284 47.83421197, 1.186438253 47.83421197, 1.186438253 47.90390196))", wkt);
    }

    @Test
    @Ignore
    public void testGeoJSONOk() throws ParseException {
        String[] args = {
            "--cnig-document-emprise",
            ""
        };
        CommandLine commandLine = parser.parse(options, args);
        Geometry result = DocumentEmpriseOption.parseCustomOptions(commandLine);

        assertNotNull(result);

        WKTWriter2 writer = new WKTWriter2();
        String wkt = writer.write(result);

        assertEquals("POLYGON((1.186438253 47.90390196, 1.098884284 47.90390196, 1.098884284 47.83421197, 1.186438253 47.83421197, 1.186438253 47.90390196))", wkt);
    }

    @Test(expected = ParseException.class)
    public void testNoParam() throws ParseException {
        String[] args = {
            "--cnig-document-emprise"
        };
        CommandLine commandLine = parser.parse(options, args);
        DocumentEmpriseOption.parseCustomOptions(commandLine);
    }

    @Test(expected = ParseException.class)
    public void testBadParam() throws ParseException {
        String[] args = {
            "--cnig-document-emprise",
            "je ne suis pas au bon format"
        };
        CommandLine commandLine = parser.parse(options, args);
        DocumentEmpriseOption.parseCustomOptions(commandLine);
    }

    @Test(expected = ParseException.class)
    public void testBadWKT() throws ParseException {
        String[] args = {
            "--cnig-document-emprise",
            "PRESQUE(1 1, 2 1, 2 3, 1 1)"
        };
        CommandLine commandLine = parser.parse(options, args);
        DocumentEmpriseOption.parseCustomOptions(commandLine);
    }

    @Test(expected = ParseException.class)
    public void testBadGeoJSON() throws ParseException {
        String[] args = {
            "--cnig-document-emprise",
            "{ type:\"PRESQUE\", geometry: [1, 1] }"
        };
        CommandLine commandLine = parser.parse(options, args);
        DocumentEmpriseOption.parseCustomOptions(commandLine);
    }

    @Test(expected = ParseException.class)
    public void testFileNotExists() throws ParseException {
        String[] args = {
            "--cnig-document-emprise",
            "/path/vers/file.json"
        };
        CommandLine commandLine = parser.parse(options, args);
        DocumentEmpriseOption.parseCustomOptions(commandLine);
    }

}
