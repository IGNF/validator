package fr.ign.validator.io;

import java.io.InputStream;
import java.net.MalformedURLException;
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
import fr.ign.validator.model.file.TableModel;

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
    public DocumentModel loadDocumentModel(URL documentModelUrl) {
        log.info(MARKER, "loadDocumentModel({}) ...", documentModelUrl);

        /*
         * loading documentModel
         */
        try {
            InputStream is = getInputStream(documentModelUrl);
            DocumentModel documentModel = (DocumentModel) unmarshaller.unmarshal(is);
            /*
             * load feature types for TableModel
             */
            for (FileModel documentFile : documentModel.getFileModels()) {
                if (!(documentFile instanceof TableModel)) {
                    continue;
                }
                URL featureTypeUrl = resolveFeatureTypeUrl(documentModelUrl, documentModel, documentFile);
                FeatureType featureType = loadFeatureType(featureTypeUrl);
                documentFile.setFeatureType(featureType);
            }
            return documentModel;
        } catch (JAXBException e) {
            String message = String.format("Fail to parse DocumentModel : %1s", documentModelUrl);
            throw new InvalidModelException(message, e);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FeatureType loadFeatureType(URL featureTypeUrl) throws ModelNotFoundException, InvalidModelException {
        log.info(MARKER, "loadFeatureType({}) ...", featureTypeUrl);
        try {
            InputStream is = getInputStream(featureTypeUrl);
            return (FeatureType) unmarshaller.unmarshal(is);
        } catch (JAXBException e) {
            String message = String.format("Fail to parse FeatureType : %1s", featureTypeUrl);
            throw new InvalidModelException(message, e);
        }
    }

}
