package fr.ign.validator.normalize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.locationtech.jts.geom.Envelope;

import fr.ign.validator.Context;
import fr.ign.validator.info.DocumentInfoExtractor;
import fr.ign.validator.info.model.TableStats;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.report.InMemoryReportBuilder;

/**
 * Ensure that the stats computed by {@link TableNormalizer} are the same as the
 * stats read from the normalized file.
 */
public class TableNormalizerStatsTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private Context context;

    private FeatureType featureType;

    @Before
    public void setUp() {
        context = new Context();
        context.setReportBuilder(new InMemoryReportBuilder());
        featureType = new FeatureType();
        AttributeType<?> id = AttributeType.forName("String");
        id.setName("id");
        featureType.addAttribute(id);
        AttributeType<?> wkt = AttributeType.forName("Geometry");
        wkt.setName("WKT");
        featureType.addAttribute(wkt);
    }

    private TableStats normalize(List<String> wkts, File target) throws IOException {
        File source = folder.newFile();
        StringBuilder sb = new StringBuilder("id,WKT\n");
        for (int i = 0; i < wkts.size(); i++) {
            sb.append(i).append(",\"").append(wkts.get(i)).append("\"\n");
        }
        File csv = new File(source.getParentFile(), source.getName() + ".csv");
        FileUtils.writeStringToFile(csv, sb.toString(), StandardCharsets.UTF_8);
        TableNormalizer normalizer = new TableNormalizer(context, featureType, target);
        try {
            normalizer.append(csv);
        } finally {
            normalizer.close();
        }
        return context.getTableStatsCache().get(target);
    }

    private void assertSameStats(TableStats expected, TableStats actual) {
        assertEquals(expected.getTotalFeatures(), actual.getTotalFeatures());
        Envelope e = expected.getBoundingBox();
        Envelope a = actual.getBoundingBox();
        assertEquals(e.isNull(), a.isNull());
        if (!e.isNull()) {
            assertEquals(Double.doubleToLongBits(e.getMinX()), Double.doubleToLongBits(a.getMinX()));
            assertEquals(Double.doubleToLongBits(e.getMaxX()), Double.doubleToLongBits(a.getMaxX()));
            assertEquals(Double.doubleToLongBits(e.getMinY()), Double.doubleToLongBits(a.getMinY()));
            assertEquals(Double.doubleToLongBits(e.getMaxY()), Double.doubleToLongBits(a.getMaxY()));
        }
    }

    private static String randomPolygon(Random random, double cx, double cy, double r) {
        int n = 3 + random.nextInt(30);
        StringBuilder sb = new StringBuilder("(");
        String first = null;
        for (int k = 0; k < n; k++) {
            double a = 2 * Math.PI * k / n;
            double rr = r * (0.5 + random.nextDouble());
            String c = String.format(Locale.ROOT, "%.9f %.9f", cx + rr * Math.cos(a), cy + rr * Math.sin(a));
            if (first == null) {
                first = c;
            }
            sb.append(c).append(",");
        }
        return sb.append(first).append(")").toString();
    }

    @Test
    public void testReprojectedGeometries() throws IOException {
        context.setProjection("EPSG:2154");
        Random random = new Random(42);
        for (int run = 0; run < 20; run++) {
            List<String> wkts = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                double cx = 300000 + random.nextDouble() * 700000;
                double cy = 6200000 + random.nextDouble() * 900000;
                switch (i % 6) {
                case 0:
                    wkts.add("POLYGON(" + randomPolygon(random, cx, cy, 1000) + ")");
                    break;
                case 1:
                    // hole outside the shell : the bounding box only uses the shell
                    wkts.add(
                        "POLYGON(" + randomPolygon(random, cx, cy, 1000) + ","
                            + randomPolygon(random, cx + 50000, cy, 1000) + ")"
                    );
                    break;
                case 2:
                    wkts.add(
                        "MULTIPOLYGON((" + randomPolygon(random, cx, cy, 500) + "),("
                            + randomPolygon(random, cx + 3000, cy - 3000, 500) + "))"
                    );
                    break;
                case 3:
                    wkts.add(String.format(Locale.ROOT, "POINT(%.6f %.6f)", cx, cy));
                    break;
                case 4:
                    wkts.add(String.format(Locale.ROOT, "LINESTRING(%.3f %.3f,%.3f %.3f)", cx, cy, cx + 10, cy + 10));
                    break;
                default:
                    wkts.add(random.nextBoolean() ? "" : "not a wkt");
                }
            }
            File target = folder.newFile("normalized-" + run + ".csv");
            TableStats computed = normalize(wkts, target);
            assertNotNull(computed);
            assertSameStats(DocumentInfoExtractor.readTableStats(target), computed);
        }
    }

    @Test
    public void testUnsupportedGeometriesAndZeros() throws IOException {
        context.setProjection("CRS:84");
        List<String> wkts = new ArrayList<>();
        wkts.add("POINT(0 0)");
        wkts.add("POINT(-0.0000000000000000001 0.00000000000000000001)");
        wkts.add("MULTIPOINT((1 2),(3 4))");
        wkts.add("CURVEPOLYGON(CIRCULARSTRING(0 0,1 1,2 0,1 -1,0 0))");
        wkts.add("POINT Z (5 6 7)");
        wkts.add("LINESTRING(-0.1234567890123456789 45.12345678901234567,1e-20 -1e-20)");
        File target = folder.newFile("unsupported.csv");
        TableStats computed = normalize(wkts, target);
        assertNotNull(computed);
        assertSameStats(DocumentInfoExtractor.readTableStats(target), computed);
    }

    @Test
    public void testModifiedFileIsReadAgain() throws IOException {
        context.setProjection("CRS:84");
        List<String> wkts = new ArrayList<>();
        wkts.add("POINT(1 2)");
        File target = folder.newFile("modified.csv");
        assertNotNull(normalize(wkts, target));
        // ex : DocUrbaFilter
        FileUtils.writeStringToFile(target, "id,WKT\n", StandardCharsets.UTF_8);
        assertNull(context.getTableStatsCache().get(target));
    }

}
