package fr.ign.validator.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import fr.ign.validator.exception.InvalidModelException;
import fr.ign.validator.exception.ModelNotFoundException;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;

/**
 * Load models using deprecated XML format
 *
 * @author MBorne
 */
public class XmlModelReader extends AbstractModelReader {

    private JAXBContext context;
    private Unmarshaller unmarshaller;

    public XmlModelReader() {
        log.trace(MARKER, "Initializing XmlModelReader...");
        try {
            this.context = JAXBContext.newInstance(FeatureType.class, DocumentModel.class);
            this.unmarshaller = context.createUnmarshaller();
        } catch (JAXBException e) {
            throw new RuntimeException("fail to load JAXB context", e);
        }
    }

    @Override
    public String getFormat() {
        return "xml";
    }

    @Override
    public DocumentModel loadDocumentModel(URL documentModelUrl) throws ModelNotFoundException, InvalidModelException {
        log.info(MARKER, "Loading DocumentModel from {} ...", documentModelUrl);

        /*
         * loading documentModel
         */
        InputStream is = getInputStream(documentModelUrl);
        try {
            DocumentModel documentModel = (DocumentModel) unmarshaller.unmarshal(is);
            loadFeatureTypes(documentModel, documentModelUrl);
            return documentModel;
        } catch (JAXBException | IOException e) {
            String message = String.format("Fail to load FeatureType from %1s", documentModelUrl);
            throw new InvalidModelException(message, e);
        }
    }

    @Override
    public FeatureType loadFeatureType(URL featureTypeUrl) throws ModelNotFoundException, InvalidModelException {
        log.info(MARKER, "loadFeatureType({}) ...", featureTypeUrl);
        InputStream is = getInputStream(featureTypeUrl);
        try {
            return (FeatureType) unmarshaller.unmarshal(is);
        } catch (JAXBException e) {
            String message = String.format("Fail to load FeatureType from %1s", featureTypeUrl);
            throw new InvalidModelException(message, e);
        }
    }

}
