package fr.ign.validator.tools;

import static org.junit.Assert.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.exception.ValidatorFatalError;
import fr.ign.validator.tools.ogr.OgrVersion;

/**
 * Regress test for format conversions
 * 
 * @author MBorne
 */
public class FileConverterTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    FileConverter fileConverter;

    @Before
    public void setUp() {
        fileConverter = FileConverter.getInstance();
    }

    @Test
    public void testGetVersion() {
        OgrVersion version = fileConverter.getVersion();
        System.out.println(version);
    }

    @Test
    public void testConvertShpLatin1ToCSV() throws IOException {
        File source = ResourceHelper.getResourceFile(getClass(), "/data/shp_latin1/PRESCRIPTION_PCT.dbf");
        File target = folder.newFile("PRESCRIPTION_PCT.csv");
        fileConverter.convertToCSV(source, target, StandardCharsets.ISO_8859_1);
        Assert.assertTrue(target.exists());
        List<String> lines = FileUtils.readLines(target, StandardCharsets.UTF_8);
        Assert.assertEquals(60, lines.size());
        Assert.assertEquals("WKT,LIBELLE,TXT,TYPEPSC,NOMFIC,URLFIC,INSEE,DATAPPRO,DATVALID", lines.get(0));
        Assert.assertEquals(
            "\"POINT (557311.155866353 6742836.60415676)\",Bâtiment agricole,,16,,,41269,2013/09/26,",
            lines.get(1)
        );
    }

    @Test
    public void testConvertShpLatin1ToCSVBadCharset() throws IOException {
        File source = ResourceHelper.getResourceFile(getClass(), "/data/shp_latin1/PRESCRIPTION_PCT.dbf");
        File target = folder.newFile("PRESCRIPTION_PCT.csv");
        fileConverter.convertToCSV(source, target, StandardCharsets.UTF_8);
        Assert.assertTrue(target.exists());
        /* as there is a mistake, output encoding is kept as latin1 */
        Assert.assertFalse(CharsetDetector.isValidUTF8(target));
        List<String> lines = FileUtils.readLines(target, StandardCharsets.ISO_8859_1);
        Assert.assertEquals(60, lines.size());
        Assert.assertEquals("WKT,LIBELLE,TXT,TYPEPSC,NOMFIC,URLFIC,INSEE,DATAPPRO,DATVALID", lines.get(0));
        Assert.assertEquals(
            "\"POINT (557311.155866353 6742836.60415676)\",Bâtiment agricole,,16,,,41269,2013/09/26,",
            lines.get(1)
        );
    }

    @Test
    public void testConvertShpUtf8ToCSV() throws IOException {
        File source = ResourceHelper.getResourceFile(getClass(), "/data/shp_utf8/PRESCRIPTION_PCT.dbf");
        File target = folder.newFile("PRESCRIPTION_PCT.csv");
        fileConverter.convertToCSV(source, target, StandardCharsets.UTF_8);
        Assert.assertTrue(target.exists());
        List<String> lines = FileUtils.readLines(target, StandardCharsets.UTF_8);
        Assert.assertEquals(60, lines.size());
        Assert.assertEquals("WKT,LIBELLE,TXT,TYPEPSC,NOMFIC,URLFIC,INSEE,DATAPPRO,DATVALID", lines.get(0));
        Assert.assertEquals(
            "\"POINT (557311.155866353 6742836.60415676)\",Bâtiment agricole,,16,,,41269,2013/09/26,",
            lines.get(1)
        );
    }

    @Test
    public void testConvertShpUtf8ToCSVBadCharset() throws IOException {
        File source = ResourceHelper.getResourceFile(getClass(), "/data/shp_utf8/PRESCRIPTION_PCT.dbf");
        File target = folder.newFile("PRESCRIPTION_PCT.csv");
        fileConverter.convertToCSV(source, target, StandardCharsets.ISO_8859_1);
        Assert.assertTrue(target.exists());
        /* as there is a mistake, double UTF-8 encoding */
        Assert.assertTrue(CharsetDetector.isValidUTF8(target));
        List<String> lines = FileUtils.readLines(target, StandardCharsets.UTF_8);
        Assert.assertEquals(60, lines.size());
        Assert.assertEquals("WKT,LIBELLE,TXT,TYPEPSC,NOMFIC,URLFIC,INSEE,DATAPPRO,DATVALID", lines.get(0));
        // double UTF-8 encoded â -> Ã¢
        Assert.assertEquals(
            "\"POINT (557311.155866353 6742836.60415676)\",BÃ¢timent agricole,,16,,,41269,2013/09/26,",
            lines.get(1)
        );
    }

    @Test
    public void testConvertTabLatin1ToCSV() throws IOException {
        File source = ResourceHelper.getResourceFile(getClass(), "/data/tab_latin1/PRESCRIPTION_PCT.TAB");
        File target = folder.newFile("PRESCRIPTION_PCT.csv");
        fileConverter.convertToCSV(source, target, StandardCharsets.ISO_8859_1);
        Assert.assertTrue(target.exists());
        List<String> lines = FileUtils.readLines(target, StandardCharsets.UTF_8);
        Assert.assertEquals(60, lines.size());
        Assert.assertEquals("WKT,LIBELLE,TXT,TYPEPSC,NOMFIC,URLFIC,INSEE,DATAPPRO,DATVALID", lines.get(0));
        Assert.assertEquals(
            "\"POINT (557311.156 6742836.604)\",Bâtiment agricole,,16,,,41269,2013/09/26,",
            lines.get(1)
        );
    }

    @Test
    public void testConvertTabUtf8ToCSV() throws IOException {
        File source = ResourceHelper.getResourceFile(getClass(), "/data/tab_utf8/PRESCRIPTION_PCT.tab");
        File target = folder.newFile("PRESCRIPTION_PCT.csv");
        fileConverter.convertToCSV(source, target, StandardCharsets.UTF_8);
        Assert.assertTrue(target.exists());
        List<String> lines = FileUtils.readLines(target, StandardCharsets.UTF_8);
        Assert.assertEquals(60, lines.size());
        Assert.assertEquals("WKT,LIBELLE,TXT,TYPEPSC,NOMFIC,URLFIC,INSEE,DATAPPRO,DATVALID", lines.get(0));
        Assert.assertEquals("\"POINT (557311.17 6742836.6)\",Bâtiment agricole,,16,,,41269,2013/09/26,", lines.get(1));
    }

    @Test
    public void testConvertGmlUtf8ToCSV() throws IOException {
        File source = ResourceHelper.getResourceFile(getClass(), "/gml/PRESCRIPTION_LIN.gml");
        File target = folder.newFile("PRESCRIPTION_LIN.csv");
        fileConverter.convertToCSV(source, target, StandardCharsets.UTF_8);
        Assert.assertTrue(target.exists());
        List<String> lines = FileUtils.readLines(target, StandardCharsets.UTF_8);
        Assert.assertEquals(92, lines.size());
        Assert.assertEquals("WKT,gml_id,LIBELLE,TYPEPSC,TYPEPSC2,INSEE,DATAPPRO", lines.get(0));
        Assert.assertEquals(
            "\"MULTILINESTRING ((226546.544185517 6755336.26169925,226707.572437799 6755556.91246038,226751.823845957 6755621.52764606,226758.594781693 6755631.41445423,226780.994538211 6755664.12220385,226790.366741745 6755677.80733694))\",PRESCRIPTION_LIN.2666,Marge de recul,11,11001,56118,20140123",
            lines.get(1)
        );
    }

    @Test
    public void testConvertGmlInvalid() throws IOException {
        File source = ResourceHelper.getResourceFile(getClass(), "/gml/INVALID.gml");
        File target = folder.newFile("INVALID.csv");
        assertThrows(ValidatorFatalError.class, () -> {
            fileConverter.convertToCSV(source, target, StandardCharsets.UTF_8);
        });
        Assert.assertFalse(target.exists());
    }

    /**
     * Regress test about GDAL coordinate precision while converting to CSV
     * 
     * @see validator-core/src/test/resources/data/POINT.README.md
     * 
     * @throws IOException
     */
    @Test
    public void testCoordinatePrecision() throws IOException {
        File source = ResourceHelper.getResourceFile(getClass(), "/data/POINT.shp");
        File target = folder.newFile("POINT.csv");

        String expectedTargetName = getExpectedPointName();
        Assume.assumeNotNull(expectedTargetName);
        File expectedTarget = ResourceHelper.getResourceFile(getClass(), expectedTargetName);
        List<String> expectedLines = FileUtils.readLines(expectedTarget, StandardCharsets.UTF_8);
        try {
            fileConverter.convertToCSV(source, target, StandardCharsets.UTF_8);
            Assert.assertTrue(target.exists());
            List<String> lines = FileUtils.readLines(target, StandardCharsets.UTF_8);
            Assert.assertEquals(11, lines.size());
            for (int i = 0; i < lines.size(); i++) {
                String expected = expectedLines.get(i);
                String actual = lines.get(i);
                Assert.assertEquals(expected, actual);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    /**
     * Find expected CSV POINT file according to the GDAL current version
     * 
     * @deprecated GDAL >= 2.3 is now required (POINT_EXPECTED_1.10.x.csv and
     *             POINT_EXPECTED_1.11.x.csv kept for trace)
     * 
     * @return
     */
    private String getExpectedPointName() {
        return "/data/POINT_EXPECTED_2.2.x.csv";
    }

    /**
     * Test dbf to csv with backslash as last char
     */
    @Test
    public void testRegressBugBackslash01() throws IOException {
        File source = ResourceHelper.getResourceFile(getClass(), "/data/bug-backslash/source.dbf");
        File target = folder.newFile("output.csv");
        try {
            fileConverter.convertToCSV(source, target, StandardCharsets.UTF_8);
            Assert.assertTrue(target.exists());
            List<String> lines = FileUtils.readLines(target, StandardCharsets.UTF_8);
            Assert.assertEquals(2, lines.size());
            Assert.assertEquals("URLFIC,INSEE", lines.get(0));
            Assert.assertEquals(
                "\\\\ALPICITE-NAS\\etudes\\ORNON\\ELABORATION DU PLU\\14.GPU\\GPU ORNON\\38285_PLU_20171018\\,38285",
                lines.get(1)
            );
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    /**
     * Test csv to dbf to csv with backslash as last char
     */
    @Test
    public void testRegressBugBackslash02() throws IOException {
        File source = ResourceHelper.getResourceFile(getClass(), "/data/bug-backslash/source.csv");
        File targetDbf = folder.newFile("output.dbf");
        File targetCsv = folder.newFile("output.csv");
        try {
            /* csv -> dbf */
            fileConverter.convertToCSV(source, targetDbf, StandardCharsets.UTF_8);
            Assert.assertTrue(targetDbf.exists());
            /* dbf -> csv */
            fileConverter.convertToCSV(targetDbf, targetCsv, StandardCharsets.UTF_8);
            Assert.assertTrue(targetCsv.exists());
            List<String> lines = FileUtils.readLines(targetCsv, StandardCharsets.UTF_8);
            Assert.assertEquals(2, lines.size());
            Assert.assertEquals("URLFIC,INSEE", lines.get(0));
            Assert.assertEquals(
                "\\\\ALPICITE-NAS\\etudes\\ORNON\\ELABORATION DU PLU\\14.GPU\\GPU ORNON\\38285_PLU_20171018\\,38285",
                lines.get(1)
            );
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

}
