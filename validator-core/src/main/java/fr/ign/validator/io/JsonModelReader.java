package fr.ign.validator.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.ign.validator.exception.InvalidModelException;
import fr.ign.validator.exception.ModelNotFoundException;
import fr.ign.validator.io.json.ObjectMapperFactory;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.TableModel;

public class JsonModelReader extends AbstractModelReader {
    private static final Logger log = LogManager.getRootLogger();
    private static final Marker MARKER = MarkerManager.getMarker("JsonModelReader");

    private ObjectMapper objectMapper;

    @Override
    public String getFormat() {
        return "json";
    }

    public JsonModelReader() {
        log.trace(MARKER, "Initializing JsonModelReader...");
        this.objectMapper = ObjectMapperFactory.createObjectMapper();
    }

    @Override
    public DocumentModel loadDocumentModel(URL documentModelUrl) throws ModelNotFoundException, InvalidModelException {
        log.info(MARKER, "Loading DocumentModel from {} ...", documentModelUrl);
        InputStream is = getInputStream(documentModelUrl);
        try {
            DocumentModel documentModel = objectMapper.readValue(is, DocumentModel.class);
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
        } catch (IOException e) {
            String message = String.format("Fail to parse DocumentModel : %1s : %2s", documentModelUrl, e.getMessage());
            log.error(MARKER, message, e);
            throw new InvalidModelException(message, e);
        }
    }

    @Override
    public FeatureType loadFeatureType(URL featureTypeUrl) throws ModelNotFoundException, InvalidModelException {
        log.info(MARKER, "Loading FeatureType from {} ...", featureTypeUrl);
        InputStream is = getInputStream(featureTypeUrl);
        try {
            return objectMapper.readValue(is, FeatureType.class);
        } catch (IOException e) {
            String message = String.format("Fail to parse FeatureType : %1s : %2s", featureTypeUrl, e.getMessage());
            log.error(MARKER, message, e);
            throw new InvalidModelException(message, e);
        }
    }

}
