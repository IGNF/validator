package fr.ign.validator.info;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.geotools.geometry.jts.WKTWriter2;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.io.WKTWriter;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.exception.InvalidMetadataException;
import fr.ign.validator.info.model.DocumentFileInfo;
import fr.ign.validator.info.model.DocumentInfo;
import fr.ign.validator.info.model.TableStats;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.gmd.MetadataISO19115;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.TableModel;
import fr.ign.validator.model.file.MetadataModel;
import fr.ign.validator.model.file.MultiTableModel;
import fr.ign.validator.model.file.SingleTableModel;
import fr.ign.validator.tools.EnvelopeUtils;
import fr.ign.validator.tools.TableReader;
import fr.ign.validator.tools.UnionUtils;

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
            if (fileModel instanceof SingleTableModel) {
                parseTable(context, (TableModel) fileModel, documentFileInfo);
            } else if (fileModel instanceof MultiTableModel) {
                parseTables(context, (MultiTableModel) fileModel, documentFileInfo);
            }
            documentInfo.addFile(documentFileInfo);
        }
    }

    /**
     * Retrieve boundingBox and featureCount from normalized file
     * 
     * @param context
     * @param fileModel
     * @param documentFileInfo
     */
    private void parseTable(Context context, TableModel fileModel, DocumentFileInfo documentFileInfo) {
        File csvFile = new File(context.getDataDirectory(), fileModel.getName() + ".csv");
        boolean computeUnion = false;
        if (fileModel.getName().contains("ZONE_URBA") || fileModel.getName().contains("SECTEUR_CC")) {
            computeUnion = true;
        }
        TableStats stats = getTableStatsFromNormalizedCSV(csvFile, computeUnion);
        if (stats != null) {
            documentFileInfo.setTotalFeatures(stats.getTotalFeatures());
            documentFileInfo.setBoundingBox(stats.getBoundingBox());
            if (stats.getUnion() != null) {
                WKTWriter2 writer = new WKTWriter2();
                String geometryWKT = writer.write(stats.getUnion());
                documentFileInfo.setUnionWKT(geometryWKT);
            }
        }
    }

    /**
     * Retrieve boundingBox and featureCount from normalized file
     * 
     * @param context
     * @param fileModel
     * @param documentFileInfo
     */
    private void parseTables(Context context, MultiTableModel fileModel, DocumentFileInfo documentFileInfo) {
        // stats for all tables
        TableStats globalStats = new TableStats();
        boolean computeUnion = false;

        for (TableModel tableModel : fileModel.getTableModels()) {
            File csvFile = new File(context.getDataDirectory(), tableModel.getName() + ".csv");
            TableStats tableStats = getTableStatsFromNormalizedCSV(csvFile, computeUnion);
            if (tableStats == null) {
                continue;
            }
            documentFileInfo.getTables().put(
                tableModel.getName(),
                tableStats
            );
            globalStats.setTotalFeatures(globalStats.getTotalFeatures() + tableStats.getTotalFeatures());
            globalStats.getBoundingBox().expandToInclude(tableStats.getBoundingBox());
        }

        documentFileInfo.setTotalFeatures(globalStats.getTotalFeatures());
        documentFileInfo.setBoundingBox(globalStats.getBoundingBox());
    }

    /**
     * Get {@link TableStats} from a normalized CSV file.
     * 
     * @param csvFile
     * @return
     */
    private TableStats getTableStatsFromNormalizedCSV(File csvFile, boolean computeUnion) {
        TableStats result = new TableStats();
        try {
            TableReader reader = TableReader.createTableReader(csvFile, StandardCharsets.UTF_8);

            int indexWktColumn = reader.findColumn("WKT");
            List<String> listWkt = new ArrayList<String>();
            while (reader.hasNext()) {
                String[] row = reader.next();
                // count features
                result.incrementTotalFeatures();
                // compute bounding box
                if (indexWktColumn >= 0) {
                    String wkt = row[indexWktColumn];
                    listWkt.add(wkt);
                    result.getBoundingBox().expandToInclude(EnvelopeUtils.getEnvelope(wkt));
                }
            }
            if (computeUnion) {
                result.setUnion(UnionUtils.getUnion(listWkt));
            }
        } catch (IOException e) {
            log.error(MARKER, "fail to compute stats for {}", csvFile);
            return null;
        }
        return result;
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
