package fr.ign.validator.validation.file;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import fr.ign.validator.Context;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.exception.InvalidModelException;
import fr.ign.validator.exception.ValidatorFatalError;
import fr.ign.validator.tools.Networking;
import fr.ign.validator.validation.Validator;

/**
 * Validate a file according to a given XSD schema.
 *
 * @see https://stackoverflow.com/a/7114306
 *
 * @author MBorne
 *
 */
public class XsdSchemaValidator implements Validator<DocumentFile> {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("XsdSchemaValidator");

    private static final String ATT_ID = "id";

    /**
     * Listen and report XSD validation errors with a pseudo XPath to ease error
     * location in data.
     */
    private final class XsdErrorHandler extends DefaultHandler {
        private final Context context;

        /**
         * Stacked elements in order to report error location
         */
        private List<String> elements = new ArrayList<>();

        private XsdErrorHandler(Context context) {
            this.context = context;
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {

            /*
             * retrieve id to simplify error location in data (except for root element where
             * it is useless)
             */
            if (this.elements.isEmpty()) {
                elements.add(qName);
            } else {
                elements.add(qName + getIdentifier(attributes));
            }

            super.startElement(uri, localName, qName, attributes);
        }

        /**
         * Get identifier from attributes
         *
         * @param attributes
         * @return
         */
        private String getIdentifier(Attributes attributes) {
            for (int i = 0; i < attributes.getLength(); i++) {
                String name = attributes.getLocalName(i);
                if (name.equals(ATT_ID)) {
                    return "[@id='" + attributes.getValue(i) + "']";
                }
            }
            return "";
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (!elements.isEmpty()) {
                elements.remove(elements.size() - 1);
            }
            super.endElement(uri, localName, qName);
        }

        @Override
        public void fatalError(SAXParseException e) {
            log.fatal(MARKER, "#{} - {}", e.getLineNumber(), e.getMessage());
            report(e);
        }

        @Override
        public void error(SAXParseException e) {
            log.error(MARKER, "#{} - {}", e.getLineNumber(), e.getMessage());
            report(e);
        }

        @Override
        public void warning(SAXParseException e) {
            log.warn(MARKER, "#{} - {}", e.getLineNumber(), e.getMessage());
            report(e);
        }

        /**
         * Create and report a {@link ValidatorError} from a {@link SAXParseException}
         */
        private void report(SAXParseException e) {
            ValidatorError validatorError = context.createError(CoreErrorCodes.XSD_SCHEMA_ERROR);
            // line number
            validatorError.setId(String.valueOf(e.getLineNumber()));
            // parse {xsdErrorCode}:{xsdErrorMessage} from message
            String[] messageParts = e.getMessage().split(":", 2);
            if (messageParts.length == 2) {
                validatorError.setXsdErrorCode(messageParts[0].trim());
                validatorError.setXsdErrorMessage(messageParts[1].trim());
            } else {
                validatorError.setXsdErrorMessage(e.getMessage());
            }
            // pseudo xpath
            validatorError.setXsdErrorPath(getCurrentPath());
            context.report(validatorError);
        }

        /**
         * Format elements stack to pseudo XPath
         *
         * @return
         */
        private String getCurrentPath() {
            return "//" + String.join("/", elements);
        }
    }

    @Override
    public void validate(Context context, DocumentFile documentFile) {
        URL xsdSchemaUrl = documentFile.getFileModel().getXsdSchema();
        File xmlFile = documentFile.getPath();
        if (xsdSchemaUrl == null) {
            log.trace(MARKER, "{} - skipped (no XSD schema provided)", xmlFile);
            return;
        }

        validate(context, xsdSchemaUrl, xmlFile);
    }

    /**
     * Validate xmlFile according to xsdSchema.
     *
     * @param context
     * @param xsdSchema
     * @param xmlFile
     */
    void validate(Context context, URL xsdSchema, File xmlFile) {
        log.info(MARKER, "Validate {} with XSD schema {} ...", xmlFile, xsdSchema);

        SAXParser parser = getSchemaAwareParser(xsdSchema);
        try (InputStream is = new BufferedInputStream(new FileInputStream(xmlFile))) {
            XsdErrorHandler handler = new XsdErrorHandler(context);
            parser.parse(is, handler);
        } catch (SAXException e) {
            String message = String.format("SAXParseException not catched for %1s", xmlFile);
            log.fatal(MARKER, message, e);
            throw new ValidatorFatalError(message, e);
        } catch (IOException e) {
            String message = String.format("Fail to read %1s", xmlFile);
            log.fatal(MARKER, message);
            throw new ValidatorFatalError(message, e);
        }
    }

    /**
     * Compiled schemas by URL (a schema is downloaded and compiled once for all the
     * XML files of a document, javax.xml.validation.Schema is immutable and
     * thread-safe)
     */
    private static final Map<String, Schema> SCHEMAS = new ConcurrentHashMap<>();

    /**
     * Get the compiled XSD schema for a given URL (cached).
     *
     * @param xsdSchemaUrl
     * @return
     * @throws InvalidModelException
     */
    static Schema getSchema(URL xsdSchemaUrl) throws InvalidModelException {
        Schema schema = SCHEMAS.get(xsdSchemaUrl.toString());
        if (schema == null) {
            schema = loadSchema(xsdSchemaUrl);
            SCHEMAS.put(xsdSchemaUrl.toString(), schema);
        }
        return schema;
    }

    /**
     * Download and compile a XSD schema. The remote schema and its imports are read
     * with {@link Networking#openStream(URL)} (timeouts, the JDK defaults are
     * infinite).
     *
     * @param xsdSchemaUrl
     * @return
     * @throws InvalidModelException
     */
    private static Schema loadSchema(URL xsdSchemaUrl) throws InvalidModelException {
        log.info(MARKER, "Load XSD schema {} ...", xsdSchemaUrl);
        try {
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            schemaFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            schemaFactory.setResourceResolver(new RemoteResourceResolver());
            if (!isHttp(xsdSchemaUrl)) {
                return schemaFactory.newSchema(xsdSchemaUrl);
            }
            try (InputStream is = Networking.openStream(xsdSchemaUrl)) {
                return schemaFactory.newSchema(new StreamSource(is, xsdSchemaUrl.toString()));
            }
        } catch (SAXException | IOException | UncheckedIOException e) {
            throw new InvalidModelException("fail to load XSD schema from " + xsdSchemaUrl, e);
        }
    }

    private static boolean isHttp(URL url) {
        return url.getProtocol().equals("http") || url.getProtocol().equals("https");
    }

    /**
     * Reads the remote imported or included schemas with
     * {@link Networking#openStream(URL)} (local files are left to the default
     * resolution).
     */
    private static class RemoteResourceResolver implements LSResourceResolver {

        private final DOMImplementationLS domImplementation;

        RemoteResourceResolver() {
            try {
                domImplementation = (DOMImplementationLS) DOMImplementationRegistry.newInstance()
                    .getDOMImplementation("LS");
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId,
            String baseURI) {
            if (systemId == null) {
                return null;
            }
            try {
                URL url = baseURI == null ? new URL(systemId) : new URL(new URL(baseURI), systemId);
                if (!isHttp(url)) {
                    return null;
                }
                log.debug(MARKER, "Load XSD schema {} ...", url);
                LSInput input = domImplementation.createLSInput();
                input.setSystemId(url.toString());
                input.setPublicId(publicId);
                // read in memory (the stream is not always closed by the parser)
                try (InputStream is = Networking.openStream(url)) {
                    input.setByteStream(new ByteArrayInputStream(is.readAllBytes()));
                }
                return input;
            } catch (IOException e) {
                throw new UncheckedIOException("fail to load " + systemId, e);
            }
        }
    }

    /**
     *
     * @param xsdSchemaUrl
     * @return
     */
    private SAXParser getSchemaAwareParser(URL xsdSchemaUrl) {
        try {
            Schema schema = getSchema(xsdSchemaUrl);

            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setSchema(schema);
            SAXParser parser = factory.newSAXParser();
            parser.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            parser.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            return parser;
        } catch (SAXException e) {
            throw new InvalidModelException("fail to load XSD schema from " + xsdSchemaUrl, e);
        } catch (ParserConfigurationException e) {
            throw new InvalidModelException("fail to create SAXParser for " + xsdSchemaUrl, e);
        }
    }

}
