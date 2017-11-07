package fr.ign.validator.process;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.data.Document;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.metadata.gmd.MetadataISO19115;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.MetadataModel;

/**
 * 
 * Remove XML files which doesn't corresponds to ISO19115 metadata files
 * 
 * @see MetadataISO19115.isMetadataFile
 * 
 * @author MBorne
 *
 */
public class FilterMetadataPreProcess implements ValidatorListener {

	public static final Logger log = LogManager.getRootLogger();
	private static final Marker MARKER = MarkerManager.getMarker("FilterMetadataPreProcess");

	@Override
	public void beforeMatching(Context context, Document document) throws Exception {

	}

	@Override
	public void beforeValidate(Context context, Document document) throws Exception {
		log.info(MARKER, "Filtrage des fichiers xml qui ne sont pas des métadonnées...");

		for (FileModel fileModel : document.getDocumentModel().getFileModels()) {
			if (!(fileModel instanceof MetadataModel)) {
				continue;
			}

			List<DocumentFile> documentFiles = document.getDocumentFilesByModel(fileModel);

			if (documentFiles.isEmpty()) {
				continue;
			}

			for (DocumentFile documentFile : documentFiles) {
				File xmlFile = documentFile.getPath();
				if (!MetadataISO19115.isMetadataFile(xmlFile)) {
					context.report(CoreErrorCodes.METADATA_IGNORED_FILE, context.relativize(xmlFile));
					log.info(MARKER, "Suppression du fichier XML {}...", documentFile);
					document.removeDocumentFile(documentFile);
				}
			}
		}
	}

	@Override
	public void afterValidate(Context context, Document document) throws Exception {

	}

}
