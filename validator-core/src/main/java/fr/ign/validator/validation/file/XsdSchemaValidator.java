package fr.ign.validator.validation.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
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
            elements.add(qName);
            super.startElement(uri, localName, qName, attributes);
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
        try {
            XsdErrorHandler handler = new XsdErrorHandler(context);
            parser.parse(
                new BufferedInputStream(
                    new FileInputStream(xmlFile)
                ), handler
            );
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
     * 
     * @param xsdSchemaUrl
     * @return
     */
    private SAXParser getSchemaAwareParser(URL xsdSchemaUrl) {
        try {
            // get schema
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            schemaFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            schemaFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            Schema schema = schemaFactory.newSchema(xsdSchemaUrl);

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
