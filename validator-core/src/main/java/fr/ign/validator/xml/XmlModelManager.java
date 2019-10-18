package fr.ign.validator.xml;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.exception.InvalidModelException;
import fr.ign.validator.exception.ModelNotFoundException;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.file.TableModel;

/**
 * Bring helpers to load models from XML
 * 
 * @author MBorne
 */
public class XmlModelManager {
	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("XmlModelManager");

	private JAXBContext context;
	private Unmarshaller unmarshaller;

	public XmlModelManager() {
		try {
			this.context = JAXBContext.newInstance(FeatureType.class, DocumentModel.class);
			this.unmarshaller = context.createUnmarshaller();
		} catch (JAXBException e) {
			throw new RuntimeException("fail to load JAXB context", e);
		}
	}

	/**
	 * Read DocumentModel from XML file (files.xml)
	 * 
	 * @param documentModelPath
	 * @return
	 * @throws JAXBException
	 */
	public DocumentModel loadDocumentModel(File documentModelPath) {
		log.info(MARKER, "loadDocumentModel({}) ...", documentModelPath);
		throwIfFileNotFound(documentModelPath);
		/*
		 * loading documentModel
		 */
		try {
			DocumentModel documentModel = (DocumentModel) unmarshaller.unmarshal(documentModelPath);
			/*
			 * load feature types for TableModel
			 */
			File featureTypeDirectory = new File(documentModelPath.getParent(), "types");
			for (FileModel documentFile : documentModel.getFileModels()) {
				if (!(documentFile instanceof TableModel)) {
					continue;
				}
				File featureTypeFile = new File(featureTypeDirectory, documentFile.getName() + ".xml");
				FeatureType featureType = loadFeatureType(featureTypeFile);
				documentFile.setFeatureType(featureType);
			}
			return documentModel;
		} catch (JAXBException e) {
			String message = String.format("Fail to parse DocumentModel : %1s", documentModelPath);
			throw new InvalidModelException(message, e);
		}
	}

	/**
	 * Load FeatureType from XML file (types/[NAME].xml)
	 * 
	 * @param path
	 * @return
	 * @throws JAXBException
	 */
	public FeatureType loadFeatureType(File path) throws ModelNotFoundException, InvalidModelException {
		log.info(MARKER, "loadFeatureType({}) ...", path);
		throwIfFileNotFound(path);

		try {
			FeatureType featureType = (FeatureType) unmarshaller.unmarshal(path);
			/*
			 * check typeName = fileName
			 */
			if (!FilenameUtils.getBaseName(path.getPath()).equals(featureType.getTypeName())) {
				String message = String.format("typeName != fileName for %1s", path.getName());
				log.error(MARKER, message);
				throw new InvalidModelException(message);
			}
			return featureType;
		} catch (JAXBException e) {
			String message = String.format("Fail to parse FeatureType : %1s", path);
			throw new InvalidModelException(message, e);
		}
	}

	/**
	 * Assert file exists throwing ModelNotFoundException if is not found
	 * 
	 * @param path
	 */
	private void throwIfFileNotFound(File path) throws ModelNotFoundException {
		if (!path.exists()) {
			String message = String.format("Model %1s not found", path);
			throw new ModelNotFoundException(message);
		}
	}

}
