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
import fr.ign.validator.cnig.idurba.IdurbaHelper;
import fr.ign.validator.cnig.idurba.IdurbaHelperFactory;
import fr.ign.validator.cnig.utils.EnveloppeUtils;
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
	public DocumentInfo parseDocument(Context context, Document document)
			throws MismatchedDimensionException, IOException, FactoryException, TransformException {
		File validationDirectory = context.getValidationDirectory();
		String documentName = document.getDocumentName();

		DocumentInfo documentInfo = parseDocument(context, validationDirectory, documentName);
		parseMetadataFileIdentifier(document, documentInfo);
		
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
		 * Finding shapefiles with extent
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
		 * Compute document extent
		 */
		documentInfo.setDocumentExtent(computeDocumentExtent(context, documentInfo.getDataLayers()));

		/*
		 * Finding PDF list
		 */
		{
			@SuppressWarnings("unchecked")
			Collection<File> files = FileUtils.listFiles(dataDirectory, new String[] { "pdf" }, false);
			for (File file : files) {
				documentInfo.addDataFile(new DataLayer(file.getName()));
			}
		}

		/*
		 * Extracting typeref (cadastral reference)
		 */
		documentInfo.setTyperef(parseTyperef(context, documentName, validationDirectory));
		
		return documentInfo;
	}
	

	/**
	 * Gets fileIdentifier from metadataFiles
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
	 * Get typeref value from DOC_URBA.csv file
	 * 
	 * @param context
	 * @return null if not found
	 */
	private String parseTyperef(Context context, String documentName, File validationDirectory) {
		IdurbaHelper helper = IdurbaHelperFactory.getInstance(context.getDocumentModel());
		if (null == helper) {
			log.info(MARKER, "TYPEREF ne sera pas extrait, le document n'est pas un DU");
			return null;
		}

		File documentDirectory = new File(validationDirectory, documentName);
		File dataDirectory = new File(documentDirectory, "DATA");
		File docUrbaFile = new File(dataDirectory, "DOC_URBA.csv");

		if (!docUrbaFile.exists()) {
			log.error(MARKER, "Impossible d'extraire TYPEREF, DOC_URBA non trouvée");
		}

		TyperefExtractor typerefExtractor = new TyperefExtractor(helper);
		String result = typerefExtractor.findTyperef(docUrbaFile, documentName);
		if (null == result) {
			context.report(
				CnigErrorCodes.CNIG_IDURBA_NOT_FOUND, 
				helper.getHelpExpected(documentName)
			);
		}
		return result;
	}

	/**
	 * TODO extract to tools and add unit tests
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
			layer.setBoundingBox(EnveloppeUtils.getBoundingBox(shpFile));
		}
		return layer;
	}
	
	
	/**
	 * Compute global extends from 
	 * 
	 * @param repertory
	 * @return
	 */
	private Envelope computeDocumentExtent( Context context, List <DataLayer > layerList) {
		Envelope result = new Envelope();
		for ( DataLayer dataLayer : layerList ) {
			result.expandToInclude(dataLayer.getBoundingBox());
		}
		if ( result.isNull() ){
			context.report(CnigErrorCodes.CNIG_NO_SPATIAL_DATA);
		}		
		return result;
	}



}
