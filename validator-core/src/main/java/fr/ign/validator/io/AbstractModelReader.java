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
     * 
     * @param documentModel
     * @param documentModelUrl
     */
    protected void loadStaticFiles(DocumentModel documentModel, URL documentModelUrl) throws MalformedURLException {
        log.info(MARKER, "Loading StaticFiles for {} ...", documentModel);
        for (StaticTable staticTable : documentModel.getStaticTables()) {
            log.info(MARKER, "Loading StaticFile for {} ...", staticTable.getName());
            URL staticTypeURL = resolveStaticTypeUrl(documentModelUrl, documentModel, staticTable);
            staticTable.setData(staticTypeURL);
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
     * Resolve FeatureType URL for a given FileModel.
     * 
     * @param documentModelUrl
     * @param documentModel
     * @param tableModel
     * @return
     * @throws MalformedURLException
     */
    protected URL resolveStaticTypeUrl(URL documentModelUrl, DocumentModel documentModel, StaticTable staticTable)
        throws MalformedURLException {

        String reference = staticTable.getDataReference();
        if (reference != null && !reference.isEmpty()) {
            // complete URL if required
            try {
                URL url = new URL(reference);
                return url;
            } catch (MalformedURLException e) {
                return new URL(documentModelUrl, reference);
            }
        }

        if (documentModelUrl.getProtocol().equals("file")) {
            /* config export convention */
            // validator-config-cnig/config/cnig_PLU_2017/files.(xml|json)
            // validator-config-cnig/config/cnig_PLU_2017/codes/InformationUrbaType.csv
            return new URL(
                documentModelUrl,
                "./codes/" + staticTable.getName() + ".csv"
            );
        } else {
            /* URL convention */
            // https://www.geoportail-urbanisme.gouv.fr/standard/cnig_PLU_2017.(xml|json)
            // https://www.geoportail-urbanisme.gouv.fr/standard/cnig_PLU_2017/codes/InformationUrbaType.csv
            return new URL(
                documentModelUrl,
                "./" + documentModel.getName() + "/codes/" + staticTable.getName() + ".csv"
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
