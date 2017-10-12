package fr.ign.validator.cnig.info;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;

import com.vividsolutions.jts.geom.Envelope;

import fr.ign.validator.Context;
import fr.ign.validator.cnig.error.CnigErrorCodes;
import fr.ign.validator.cnig.utils.EnveloppeUtils;
import fr.ign.validator.cnig.utils.IdurbaUtils;
import fr.ign.validator.cnig.utils.TyperefExtractor;
import fr.ign.validator.data.Document;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.exception.InvalidMetadataException;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.gmd.MetadataISO19115;
import fr.ign.validator.model.file.MetadataModel;
import fr.ign.validator.tools.CompanionFileUtils;

/**
 * 
 * Extraction des informations à partir du répertoire de validation d'un
 * document
 * 
 * @author CBouche
 * @author MBorne
 *
 */
public class DocumentInfoExtractor {
	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("DocumentInfoExtractor");

	/**
	 * Récupération des informations d'un dossier
	 * 
	 * @param documentName
	 * @param validationDirectory
	 * @return
	 * @throws MismatchedDimensionException
	 * @throws IOException
	 * @throws FactoryException
	 * @throws TransformException
	 */
	public DocumentInfo parseDocument(Context context, Document document)
			throws MismatchedDimensionException, IOException, FactoryException, TransformException {
		File validationDirectory = context.getValidationDirectory();
		String documentName = document.getDocumentName();

		DocumentInfo documentInfo = parseDocument(context, validationDirectory, documentName);
		parseMetadataFileIdentifier(document, documentInfo);
		documentInfo.setGeometry(DocumentGeometryExtractor.extractGeometry(context, document));
		
		documentInfo.sortFiles();
		
		return documentInfo;
	}

	/**
	 * @param directoryName
	 * @param file
	 * @throws IOException
	 * @throws FactoryException
	 * @throws TransformException
	 * @throws MismatchedDimensionException
	 */
	private DocumentInfo parseDocument(Context context, File validationDirectory, String documentName)
			throws IOException, FactoryException, MismatchedDimensionException, TransformException {
		DocumentInfo documentInfo = new DocumentInfo(documentName);
		documentInfo.setStandard(context.getDocumentModelName());
		/*
		 * Récupération des shapefiles avec leurs emprises
		 */
		File documentDirectory = new File(validationDirectory, documentName);
		File dataDirectory = new File(documentDirectory, "DATA");
		{
			@SuppressWarnings("unchecked")
			Collection<File> dbfFiles = FileUtils.listFiles(dataDirectory, new String[] { "dbf" }, false);
			for (File dbfFile : dbfFiles) {
				documentInfo.addDataLayer(createDataLayer(dbfFile));
			}
		}

		/*
		 * Récupération de la liste des PDF
		 */
		{
			@SuppressWarnings("unchecked")
			Collection<File> files = FileUtils.listFiles(dataDirectory, new String[] { "pdf" }, false);
			for (File file : files) {
				documentInfo.addDataFile(new DataLayer(file.getName()));
			}
		}

		/*
		 * Extraction du typeref (référence cadastrale)
		 */
		documentInfo.setTyperef(parseTyperef(context, documentName, validationDirectory));

		return documentInfo;
	}
	

	/**
	 * Récupération du fileIdentifier dans la fiche de métadonnée
	 * 
	 * @param document
	 * @return
	 */
	private void parseMetadataFileIdentifier(Document document, DocumentInfo documentDirectory) {
		List<DocumentFile> metadataFiles = document.getDocumentFiles(MetadataModel.class);
		if (metadataFiles.isEmpty()) {
			return;
		}
		if (metadataFiles.size() > 1) {
			log.warn(MARKER, "Il y a {} fiche de métadonnée, utilisation de la première", metadataFiles.size());
		}

		File metadataPath = metadataFiles.get(0).getPath();
		try {
			Metadata reader = MetadataISO19115.readFile(metadataPath);
			documentDirectory.setMetadataFileIdentifier(reader.getFileIdentifier());
			documentDirectory.setMetadataMdIdentifier(reader.getIdentifier());
		} catch (InvalidMetadataException e) {
			log.warn(MARKER, "Erreur dans la lecture de la fiche de métadonnée");
		}
	}

	/**
	 * Récupération de la valeur du champ TYPEREF dans le fichier DOC_URBA.csv
	 * 
	 * @param context
	 * @return null si non trouvé
	 */
	private String parseTyperef(Context context, String documentName, File validationDirectory) {
		String regexpIDURBA = IdurbaUtils.getRegexp(documentName);
		if (null == regexpIDURBA) {
			log.info(MARKER, "TYPEREF ne sera pas extrait, le document n'est pas un DU");
			return null;
		}

		File documentDirectory = new File(validationDirectory, documentName);
		File dataDirectory = new File(documentDirectory, "DATA");
		File docUrbaFile = new File(dataDirectory, "DOC_URBA.csv");

		if (!docUrbaFile.exists()) {
			log.error(MARKER, "Impossible d'extraire TYPEREF, DOC_URBA non trouvée");
		}

		TyperefExtractor typerefExtractor = new TyperefExtractor();
		String result = typerefExtractor.findTyperef(docUrbaFile, documentName);
		if (null == result) {
			context.report(CnigErrorCodes.CNIG_IDURBA_NOT_FOUND, IdurbaUtils.getRegexp(documentName));
		}
		return result;
	}

	/**
	 * TODO Sortir dans tools et ajouter des tests unitaires
	 * 
	 * @param dbfFile
	 * @return
	 * @throws IOException
	 * @throws FactoryException
	 * @throws MismatchedDimensionException
	 * @throws TransformException
	 */
	private DataLayer createDataLayer(File dbfFile)
			throws IOException, FactoryException, MismatchedDimensionException, TransformException {
		log.debug(MARKER, "récupération de la BBOX du fichier {}", dbfFile);
		
		String layerName = FilenameUtils.getBaseName(dbfFile.getName());
		DataLayer layer = new DataLayer(layerName);
		File shpFile = CompanionFileUtils.getCompanionFile(dbfFile, "shp");
		if ( shpFile.exists() ){
			Envelope bbox = EnveloppeUtils.getBoundingBox(shpFile);
			layer.setLayerBbox(EnveloppeUtils.format(bbox));
		}
		return layer;
	}

}
