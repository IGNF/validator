package fr.ign.validator.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.ign.validator.exception.InvalidModelException;
import fr.ign.validator.exception.ModelNotFoundException;
import fr.ign.validator.io.json.ObjectMapperFactory;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;

/**
 * Helper class to load {@link DocumentModel} and {@link FeatureType} from JSON.
 *
 * @author MBorne
 *
 */
public class JsonModelReader extends AbstractModelReader {
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
        try {
            InputStream is = getInputStream(documentModelUrl);
            DocumentModel documentModel = objectMapper.readValue(is, DocumentModel.class);
            loadFeatureTypes(documentModel, documentModelUrl);
            loadStaticFiles(documentModel, documentModelUrl);
            return documentModel;
        } catch (IOException e) {
            String message = String.format(
                "Fail to load DocumentModel from %1s : %2s",
                documentModelUrl,
                e.getMessage()
            );
            log.error(MARKER, message, e);
            throw new InvalidModelException(message, e);
        }
    }

    @Override
    public FeatureType loadFeatureType(URL featureTypeUrl) throws ModelNotFoundException, InvalidModelException {
        log.info(MARKER, "Loading FeatureType from {} ...", featureTypeUrl);
        try {
            InputStream is = getInputStream(featureTypeUrl);
            return objectMapper.readValue(is, FeatureType.class);
        } catch (IOException e) {
            String message = String.format(
                "Fail to load FeatureType from %1s : %2s",
                featureTypeUrl,
                e.getMessage()
            );
            log.error(MARKER, message, e);
            throw new InvalidModelException(message, e);
        }
    }

}
