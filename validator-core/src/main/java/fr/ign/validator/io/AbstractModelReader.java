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
import fr.ign.validator.model.FileModel;

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
     * Resolve FeatureType URL for a given FileModel
     * 
     * TODO support explicit featureType reference in FileModel
     *
     * @param documentModelUrl
     * @param documentModel
     * @param documentFile
     * @return
     * @throws MalformedURLException
     */
    protected URL resolveFeatureTypeUrl(URL documentModelUrl, DocumentModel documentModel, FileModel documentFile)
        throws MalformedURLException {
        String parentUrl = getParentURL(documentModelUrl);
        if (documentModelUrl.getProtocol().equals("file")) {
            /* config export convention */
            // validator-config-cnig/config/cnig_PLU_2017/files.xml
            // validator-config-cnig/config/cnig_PLU_2017/types/ZONE_URBA.xml
            return new URL(parentUrl + "/types/" + documentFile.getName() + "." + getFormat());
        } else {
            /* URL convention */
            // https://www.geoportail-urbanisme.gouv.fr/standard/cnig_PLU_2017.xml
            // https://www.geoportail-urbanisme.gouv.fr/standard/cnig_PLU_2017/types/ZONE_URBA.xml
            return new URL(
                parentUrl + "/" + documentModel.getName() + "/types/" + documentFile.getName() + "." + getFormat()
            );
        }
    }

    /**
     * Get parent URL
     * 
     * @param url
     * @return
     */
    protected String getParentURL(URL url) {
        String path = url.toString();
        int lastSlashPos = path.lastIndexOf('/');
        if (lastSlashPos >= 0) {
            return path.substring(0, lastSlashPos); // strip off the slash
        } else {
            String message = String.format("Fail to get parent URL for : %1s", url);
            throw new RuntimeException(message);
        }
    }

    /**
     * Open URL, throws ModelNotFoundException on failure
     * 
     * @param url
     * @return
     */
    protected InputStream getInputStream(URL url) {
        try {
            return url.openStream();
        } catch (IOException e) {
            throw new ModelNotFoundException(url, e);
        }
    }

}
