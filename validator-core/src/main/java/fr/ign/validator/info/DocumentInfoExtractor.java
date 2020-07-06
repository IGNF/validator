package fr.ign.validator.info;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import org.locationtech.jts.geom.Envelope;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.exception.InvalidCharsetException;
import fr.ign.validator.exception.InvalidMetadataException;
import fr.ign.validator.info.model.DocumentFileInfo;
import fr.ign.validator.info.model.DocumentInfo;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.gmd.MetadataISO19115;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.MetadataModel;
import fr.ign.validator.model.file.TableModel;
import fr.ign.validator.tools.EnvelopeUtils;
import fr.ign.validator.tools.TableReader;

/**
 * 
 * Extracts informations from a validation directory of a document
 * 
 * @author CBouche
 * @author MBorne
 *
 */
public class DocumentInfoExtractor {
    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("DocumentInfoExtractor");

    /**
     * Gets informations on directory
     * 
     * @param documentName
     * @param validationDirectory
     * @return
     * @throws MismatchedDimensionException
     * @throws IOException
     * @throws FactoryException
     * @throws TransformException
     */
    public DocumentInfo parseDocument(Context context, Document document) {
        DocumentInfo documentInfo = new DocumentInfo(document);
        parseDocumentFiles(context, document, documentInfo);
        documentInfo.sortFiles();
        documentInfo.setMetadata(findMetadata(document));
        documentInfo.setDocumentExtent(computeDocumentExtent(context, documentInfo.getFiles()));
        return documentInfo;
    }

    /**
     * @param context
     * @param document
     * @param documentInfo
     * @throws IOException
     * @throws FactoryException
     * @throws MismatchedDimensionException
     * @throws TransformException
     */
    private void parseDocumentFiles(Context context, Document document, DocumentInfo documentInfo) {
        List<DocumentFile> documentFiles = document.getDocumentFiles();
        for (DocumentFile documentFile : documentFiles) {
            DocumentFileInfo documentFileInfo = new DocumentFileInfo();

            FileModel fileModel = documentFile.getFileModel();

            documentFileInfo.setType(fileModel.getType());
            documentFileInfo.setModelName(fileModel.getName());
            documentFileInfo.setName(documentFile.getPath().getName());
            documentFileInfo.setPath(context.relativize(documentFile.getPath()));
            if (fileModel instanceof TableModel) {
                parseTable(context, fileModel, documentFileInfo);
            }
            documentInfo.addFile(documentFileInfo);
        }
    }

    /**
     * Retreive boundingBox and featureCount from normalized file
     * 
     * @param context
     * @param fileModel
     * @param documentFileInfo
     */
    private void parseTable(Context context, FileModel fileModel, DocumentFileInfo documentFileInfo) {
        File csvFile = new File(context.getDataDirectory(), fileModel.getName() + ".csv");

        Envelope boundingBox = new Envelope();
        int totalFeatures = 0;
        try {
            TableReader reader = TableReader.createTableReader(csvFile, StandardCharsets.UTF_8);
            // retreive geometry column
            int indexWktColumn = reader.findColumn("WKT");
            while (reader.hasNext()) {
                String[] row = reader.next();
                // count features
                totalFeatures++;
                // compute bounding box
                if (indexWktColumn >= 0) {
                    String wkt = row[indexWktColumn];
                    boundingBox.expandToInclude(EnvelopeUtils.getEnvelope(wkt));
                }
            }

        } catch (IOException | InvalidCharsetException e) {
            log.error(MARKER, "Fail to extract infos from " + fileModel.getName() + ".csv");
            return;
        }

        documentFileInfo.setTotalFeatures(totalFeatures);
        documentFileInfo.setBoundingBox(boundingBox);
    }

    /**
     * Gets fileIdentifier from metadataFiles
     * 
     * @param document
     * @return
     */
    private Metadata findMetadata(Document document) {
        List<DocumentFile> metadataFiles = document.getDocumentFiles(MetadataModel.class);
        if (metadataFiles.isEmpty()) {
            return null;
        }
        if (metadataFiles.size() > 1) {
            log.warn(MARKER, "Il y a {} fiche de métadonnée, utilisation de la première", metadataFiles.size());
        }

        File metadataPath = metadataFiles.get(0).getPath();
        try {
            return MetadataISO19115.readFile(metadataPath);
        } catch (InvalidMetadataException e) {
            log.warn(MARKER, "Erreur dans la lecture de la fiche de métadonnée");
        }
        return null;
    }

    /**
     * Compute global extends from
     * 
     * @param repertory
     * @return
     */
    private Envelope computeDocumentExtent(Context context, List<DocumentFileInfo> documentFiles) {
        Envelope result = new Envelope();
        for (DocumentFileInfo documentFile : documentFiles) {
            if (!documentFile.hasExtent())
                continue;

            result.expandToInclude(documentFile.getBoundingBox());
        }
        if (result.isNull()) {
            context.report(CoreErrorCodes.NO_SPATIAL_DATA);
        }
        return result;
    }

}
