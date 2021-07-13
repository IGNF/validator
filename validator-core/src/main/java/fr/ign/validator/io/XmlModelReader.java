package fr.ign.validator.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.exception.InvalidModelException;
import fr.ign.validator.exception.ModelNotFoundException;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.SingleTableModel;

/**
 * Load models using deprecated XML format
 * 
 * @author MBorne
 */
public class XmlModelReader extends AbstractModelReader {
    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("XmlModelReader");

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
            /*
             * load feature types for TableModel
             */
            for (FileModel fileModel : documentModel.getFileModels()) {
                if (fileModel instanceof SingleTableModel) {
                    SingleTableModel tableModel = (SingleTableModel) fileModel;
                    URL featureTypeUrl = resolveFeatureTypeUrl(documentModelUrl, documentModel, tableModel);
                    FeatureType featureType = loadFeatureType(featureTypeUrl);
                    tableModel.setFeatureType(featureType);
                }
            }
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
