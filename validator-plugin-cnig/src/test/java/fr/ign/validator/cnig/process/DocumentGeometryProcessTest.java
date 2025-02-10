package fr.ign.validator.cnig.process;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import fr.ign.validator.cnig.command.DocumentGeometryCommand;
import fr.ign.validator.tools.CompanionFileUtils;
import fr.ign.validator.tools.ResourceHelper;

/**
 *
 * Test DocumentGeometryProcess
 *
 * @author DDarras
 *
 */
public class DocumentGeometryProcessTest {

    DocumentGeometryProcess documentGeometryProcess;

    /**
     * Initialize test
     * @throws Exception
     */
    public DocumentGeometryProcessTest() throws Exception {
        List<File> inputFiles = new ArrayList<File>();
        inputFiles.add(ResourceHelper.getResourceFile(getClass(), "/geometry/fair_geometries.csv"));
        inputFiles.add(ResourceHelper.getResourceFile(getClass(), "/geometry/dubious_geometries.csv"));

        List<String> geometries = new ArrayList<String>();
        geometries.add("geom");
        geometries.add("geometry");
        geometries.add("WKT");

        this.documentGeometryProcess = new DocumentGeometryProcess(inputFiles, geometries);
    }

    @Test
    public void testDetectGeometries() throws Exception{
        this.documentGeometryProcess.detectGeometries();
    }
}
