package fr.ign.validator.validation.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.sun.net.httpserver.HttpServer;

import fr.ign.validator.Context;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.exception.InvalidModelException;
import fr.ign.validator.report.InMemoryReportBuilder;

/**
 * Remote XSD schema with a relative include : read with the timeouts of
 * Networking, downloaded once.
 */
public class XsdSchemaValidatorRemoteTest {

    private static final String MAIN_XSD = "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">"
        + "<xs:include schemaLocation=\"types.xsd\"/>"
        + "<xs:element name=\"root\"><xs:complexType><xs:sequence>"
        + "<xs:element name=\"code\" type=\"CodeType\"/>"
        + "</xs:sequence></xs:complexType></xs:element>"
        + "</xs:schema>";

    private static final String TYPES_XSD = "<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">"
        + "<xs:simpleType name=\"CodeType\"><xs:restriction base=\"xs:string\">"
        + "<xs:pattern value=\"[A-Z]{2}\"/>"
        + "</xs:restriction></xs:simpleType>"
        + "</xs:schema>";

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private HttpServer server;

    private final Map<String, AtomicInteger> requests = new ConcurrentHashMap<>();

    @Before
    public void setUp() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        serve("/xsd/main.xsd", MAIN_XSD);
        serve("/xsd/types.xsd", TYPES_XSD);
        server.start();
    }

    private void serve(String path, String content) {
        requests.put(path, new AtomicInteger());
        server.createContext(path, exchange -> {
            requests.get(path).incrementAndGet();
            byte[] body = content.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        });
    }

    @After
    public void tearDown() {
        server.stop(0);
    }

    private int validate(URL xsdUrl, String xml) throws IOException {
        File xmlFile = folder.newFile();
        FileUtils.writeStringToFile(xmlFile, xml, StandardCharsets.UTF_8);
        InMemoryReportBuilder report = new InMemoryReportBuilder();
        Context context = new Context();
        context.setReportBuilder(report);
        new XsdSchemaValidator().validate(context, xsdUrl, xmlFile);
        return report.getErrorsByCode(CoreErrorCodes.XSD_SCHEMA_ERROR).size();
    }

    @Test
    public void testRemoteSchemaWithInclude() throws IOException {
        URL xsdUrl = new URL("http://127.0.0.1:" + server.getAddress().getPort() + "/xsd/main.xsd");

        assertEquals(0, validate(xsdUrl, "<root><code>AB</code></root>"));
        // cvc-pattern-valid and cvc-type.3.1.3
        assertTrue(validate(xsdUrl, "<root><code>abc</code></root>") > 0);

        // the schema and its include are downloaded once
        assertEquals(1, requests.get("/xsd/main.xsd").get());
        assertEquals(1, requests.get("/xsd/types.xsd").get());
        assertSame(XsdSchemaValidator.getSchema(xsdUrl), XsdSchemaValidator.getSchema(xsdUrl));
    }

    /**
     * Local schema including a local schema (file: URLs are read by the resolver,
     * the parser itself can't access external schemas)
     */
    @Test
    public void testLocalSchemaWithInclude() throws IOException {
        File xsdDirectory = folder.newFolder("xsd");
        FileUtils.writeStringToFile(new File(xsdDirectory, "main.xsd"), MAIN_XSD, StandardCharsets.UTF_8);
        FileUtils.writeStringToFile(new File(xsdDirectory, "types.xsd"), TYPES_XSD, StandardCharsets.UTF_8);
        URL xsdUrl = new File(xsdDirectory, "main.xsd").toURI().toURL();

        assertEquals(0, validate(xsdUrl, "<root><code>AB</code></root>"));
        assertTrue(validate(xsdUrl, "<root><code>abc</code></root>") > 0);
    }

    /**
     * Only http(s) and file URLs are allowed for the imported schemas
     */
    @Test
    public void testUnsupportedProtocol() throws IOException {
        File xsdFile = folder.newFile("ftp.xsd");
        FileUtils.writeStringToFile(
            xsdFile,
            MAIN_XSD.replace("types.xsd", "ftp://127.0.0.1/types.xsd"),
            StandardCharsets.UTF_8
        );
        URL xsdUrl = xsdFile.toURI().toURL();
        InvalidModelException e = assertThrows(
            InvalidModelException.class, () -> XsdSchemaValidator.getSchema(xsdUrl)
        );
        // rejected by the resolver (not a connection failure)
        String causes = "";
        for (Throwable t = e; t != null; t = t.getCause()) {
            causes += t.getMessage() + " / ";
        }
        assertTrue(causes, causes.contains("unsupported protocol"));
    }

}
