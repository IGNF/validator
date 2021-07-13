package fr.ign.validator.validation.file;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import fr.ign.validator.Context;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.exception.InvalidModelException;
import fr.ign.validator.exception.ValidatorFatalError;
import fr.ign.validator.validation.Validator;

/**
 * Validate a file according to a given XSD schema.
 * 
 * @author MBorne
 *
 */
public class XsdSchemaValidator implements Validator<DocumentFile> {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("XsdSchemaValidator");

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
        javax.xml.validation.Validator xsdValidator = getXsdSchemaValidator(xsdSchema);

        try {
            /* create custom error handler reporting errors in validation report */
            xsdValidator.setErrorHandler(new ErrorHandler() {
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

                private void report(SAXParseException e) {
                    context.report(
                        context.createError(CoreErrorCodes.XSD_SCHEMA_ERROR)
                            .setMessageParam("LINE_NUMBER", "#" + e.getLineNumber())
                            .setMessageParam("MESSAGE", e.getMessage())
                    );
                }
            });

            xsdValidator.validate(
                new StreamSource(
                    new BufferedInputStream(
                        new FileInputStream(xmlFile)
                    )
                )
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
     * Load XSD schema from URL.
     * 
     * @param xsdSchemaUrl
     * @return
     */
    private javax.xml.validation.Validator getXsdSchemaValidator(URL xsdSchemaUrl) {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            Schema schema = schemaFactory.newSchema(xsdSchemaUrl);
            javax.xml.validation.Validator xsdValidator = schema.newValidator();
            return xsdValidator;
        } catch (SAXException e) {
            throw new InvalidModelException("fail to load XSD schema from " + xsdSchemaUrl, e);
        }
    }

}
