package fr.ign.validator.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.exception.ValidatorFatalError;

/**
 * Malicious input files : GDAL must not read files or URLs referenced by a user
 * file, nor write outside the conversion directory.
 */
public class FileConverterSecurityTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private FileConverter fileConverter;

    @Before
    public void setUp() {
        fileConverter = FileConverter.getInstance();
        Assume.assumeNotNull(fileConverter.getVersion());
    }

    /**
     * A ".gml" file containing an OGR VRT referencing a server file.
     */
    @Test
    public void testVrtDisguisedAsGmlIsNotRead() throws IOException {
        File secret = folder.newFile("secret.csv");
        FileUtils.writeStringToFile(secret, "secret_col\nSERVER-SECRET-123\n", StandardCharsets.UTF_8);

        File source = new File(folder.getRoot(), "ZONE_URBA.gml");
        FileUtils.writeStringToFile(
            source,
            "<OGRVRTDataSource><OGRVRTLayer name=\"ZONE_URBA\"><SrcDataSource>" + secret.getAbsolutePath()
                + "</SrcDataSource><SrcLayer>secret</SrcLayer></OGRVRTLayer></OGRVRTDataSource>",
            StandardCharsets.UTF_8
        );
        File target = new File(folder.getRoot(), "ZONE_URBA.csv");
        assertThrows(
            ValidatorFatalError.class, () -> fileConverter.convertToCSV(source, target, StandardCharsets.UTF_8)
        );
        if (target.exists()) {
            assertFalse(FileUtils.readFileToString(target, StandardCharsets.UTF_8).contains("SERVER-SECRET-123"));
        }
    }

    @Test
    public void testGetInputDriver() throws IOException {
        assertEquals("ESRI Shapefile", FileConverter.getInputDriver(new File("a.SHP")));
        assertEquals("ESRI Shapefile", FileConverter.getInputDriver(new File("a.dbf")));
        assertEquals("MapInfo File", FileConverter.getInputDriver(new File("a.TAB")));
        assertEquals("GML", FileConverter.getInputDriver(new File("a.gml")));
        assertEquals("GPKG", FileConverter.getInputDriver(new File("a.gpkg")));
        assertEquals("CSV", FileConverter.getInputDriver(new File("a.csv")));
        assertEquals("GeoJSON", FileConverter.getInputDriver(new File("a.json")));
        assertThrows(IOException.class, () -> FileConverter.getInputDriver(new File("a.vrt")));
    }

    @Test
    public void testSensitiveEnvironmentVariables() {
        assertTrue(FileConverter.isSensitiveEnvironmentVariable("DB_PASSWORD"));
        assertTrue(FileConverter.isSensitiveEnvironmentVariable("DB_URL"));
        assertTrue(FileConverter.isSensitiveEnvironmentVariable("PGPASSWORD"));
        assertFalse(FileConverter.isSensitiveEnvironmentVariable("PATH"));
        assertFalse(FileConverter.isSensitiveEnvironmentVariable("GDAL_DATA"));
    }

}
