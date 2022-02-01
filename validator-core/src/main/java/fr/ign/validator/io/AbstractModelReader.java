package fr.ign.validator.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.exception.InvalidModelException;
import fr.ign.validator.exception.ModelNotFoundException;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FeatureTypeRef;
import fr.ign.validator.model.StaticTable;
import fr.ign.validator.model.TableModel;
import fr.ign.validator.tools.ModelHelper;

/**
 * Common implementation for JSON and XML ModelReader.
 * 
 * @author MBorne
 */
abstract class AbstractModelReader implements ModelReader {
    protected static final Logger log = LogManager.getRootLogger();
    protected static final Marker MARKER = MarkerManager.getMarker("ModelReader");

    @Override
    public DocumentModel loadDocumentModel(File documentModelPath) {
        try {
            DocumentModel documentModel = loadDocumentModel(documentModelPath.toURI().toURL());
            resolveStaticFilesPath(documentModel, documentModelPath.toURI().toURL());
            return documentModel;
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

    public void resolveStaticFilesPath(DocumentModel documentModel, URL documentUrl) {
        try {
            for (StaticTable staticTable : documentModel.getStaticTables()) {
                URL url = resolveStaticFileUrl(documentUrl, documentModel, staticTable.getName());
                staticTable.setUrl(url);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 
     * @param documentModel
     * @param documentModelUrl
     * @throws MalformedURLException
     */
    protected void loadFeatureTypes(DocumentModel documentModel, URL documentModelUrl) throws MalformedURLException {
        log.info(MARKER, "Loading FeatureTypes for {} ...", documentModel);
        for (TableModel tableModel : ModelHelper.getTableModels(documentModel)) {
            log.info(MARKER, "Loading FeatureType for {} ...", tableModel);
            URL featureTypeUrl = resolveFeatureTypeUrl(documentModelUrl, documentModel, tableModel);
            if (featureTypeUrl == null) {
                log.info(MARKER, "Loading FeatureType for {} : skipped (tableModel declared as auto)", tableModel);
                continue;
            }
            FeatureType featureType = loadFeatureType(featureTypeUrl);
            tableModel.setFeatureType(featureType);
            log.info(MARKER, "Loading FeatureType for {} : complete ({})", tableModel, featureType);
        }
        log.info(MARKER, "Loading FeatureTypes for {} : completed.", documentModel);
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
            // return null if value is "auto"
            if (ref.getValue().equalsIgnoreCase(FeatureTypeRef.AUTO)) {
                return null;
            }

            // complete URL if required
            if (ref.isURL()) {
                return new URL(ref.getValue());
            } else {
                return new URL(documentModelUrl, ref.getValue());
            }
        }

        return resolveConfigFileUrl(documentModelUrl, documentModel, tableModel.getName());
    }

    /**
     * Resolve Config file URL for a given filename.
     * 
     * @param documentModelUrl
     * @param documentModel
     * @param filename
     * @return
     * @throws MalformedURLException
     */
    protected URL resolveConfigFileUrl(URL documentModelUrl, DocumentModel documentModel, String filename)
        throws MalformedURLException {
        if (documentModelUrl.getProtocol().equals("file")) {
            /* config export convention */
            // validator-config-cnig/config/cnig_PLU_2017/files.(xml|json)
            // validator-config-cnig/config/cnig_PLU_2017/types/ZONE_URBA.(xml|json)
            return new URL(
                documentModelUrl,
                "./types/" + filename + "." + getFormat()
            );
        } else {
            /* URL convention */
            // https://www.geoportail-urbanisme.gouv.fr/standard/cnig_PLU_2017.(xml|json)
            // https://www.geoportail-urbanisme.gouv.fr/standard/cnig_PLU_2017/types/ZONE_URBA.(xml|json)
            return new URL(
                documentModelUrl,
                "./" + documentModel.getName() + "/types/" + filename + "." + getFormat()
            );
        }
    }

    /**
     * Resolve Static file URL for a given filename.
     * 
     * @param documentModelUrl
     * @param documentModel
     * @param filename
     * @return
     * @throws MalformedURLException
     */
    protected URL resolveStaticFileUrl(URL documentModelUrl, DocumentModel documentModel, String filename)
        throws MalformedURLException {
        if (documentModelUrl.getProtocol().equals("file")) {
            /* config export convention */
            // validator-config-cnig/config/cnig_PLU_2017/files.(xml|json)
            // validator-config-cnig/config/cnig_PLU_2017/codes/InformationUrbaType.csv
            return new URL(
                documentModelUrl,
                "./codes/" + filename + ".csv"
            );
        } else {
            /* URL convention */
            // https://www.geoportail-urbanisme.gouv.fr/standard/cnig_PLU_2017.(xml|json)
            // https://www.geoportail-urbanisme.gouv.fr/standard/cnig_PLU_2017/codes/InformationUrbaType.csv
            return new URL(
                documentModelUrl,
                "./" + documentModel.getName() + "/codes/" + filename + ".csv"
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
