package fr.ign.validator.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import fr.ign.validator.exception.InvalidModelException;
import fr.ign.validator.exception.ModelNotFoundException;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FeatureTypeRef;
import fr.ign.validator.model.TableModel;

/**
 * Common implementation for JSON and XML ModelReader.
 * 
 * @author MBorne
 */
abstract class AbstractModelReader implements ModelReader {

    @Override
    public DocumentModel loadDocumentModel(File documentModelPath) {
        try {
            return loadDocumentModel(documentModelPath.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FeatureType loadFeatureType(File path) throws ModelNotFoundException, InvalidModelException {
        try {
            return loadFeatureType(path.toURI().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Resolve FeatureType URL for a given FileModel.
     * 
     * @param documentModelUrl
     * @param documentModel
     * @param tableModel
     * @return
     * @throws MalformedURLException
     */
    protected URL resolveFeatureTypeUrl(URL documentModelUrl, DocumentModel documentModel, TableModel tableModel)
        throws MalformedURLException {

        FeatureTypeRef ref = tableModel.getFeatureTypeRef();
        if (ref != null && !ref.isEmpty()) {
            if (ref.isURL()) {
                return new URL(ref.getValue());
            } else {
                return new URL(documentModelUrl, ref.getValue());
            }
        }

        if (documentModelUrl.getProtocol().equals("file")) {
            /* config export convention */
            // validator-config-cnig/config/cnig_PLU_2017/files.(xml|json)
            // validator-config-cnig/config/cnig_PLU_2017/types/ZONE_URBA.(xml|json)
            return new URL(
                documentModelUrl,
                "./types/" + tableModel.getName() + "." + getFormat()
            );
        } else {
            /* URL convention */
            // https://www.geoportail-urbanisme.gouv.fr/standard/cnig_PLU_2017.(xml|json)
            // https://www.geoportail-urbanisme.gouv.fr/standard/cnig_PLU_2017/types/ZONE_URBA.(xml|json)
            return new URL(
                documentModelUrl,
                "./" + documentModel.getName() + "/types/" + tableModel.getName() + "." + getFormat()
            );
        }
    }

    /**
     * Open URL, throws ModelNotFoundException on failure
     * 
     * @param url
     * @return
     */
    protected InputStream getInputStream(URL url) throws ModelNotFoundException {
        try {
            return url.openStream();
        } catch (IOException e) {
            throw new ModelNotFoundException(url, e);
        }
    }

}
