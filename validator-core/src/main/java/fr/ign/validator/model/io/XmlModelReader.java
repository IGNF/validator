package fr.ign.validator.model.io;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

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
public class XmlModelReader {
	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("XmlModelManager");

	private JAXBContext context;
	private Unmarshaller unmarshaller;

	public XmlModelReader() {
		try {
			this.context = JAXBContext.newInstance(FeatureType.class, DocumentModel.class);
			this.unmarshaller = context.createUnmarshaller();
		} catch (JAXBException e) {
			throw new RuntimeException("fail to load JAXB context", e);
		}
	}
	
	/**
	 * Read DocumentModel from URL
	 * @param documentModelUrl
	 * @return
	 */
	public DocumentModel loadDocumentModel(URL documentModelUrl) {
	    log.info(MARKER, "loadDocumentModel({}) ...", documentModelUrl);

        /*
         * loading documentModel
         */
        try {
            DocumentModel documentModel = (DocumentModel) unmarshaller.unmarshal(documentModelUrl);
            /*
             * load feature types for TableModel
             */
            for (FileModel documentFile : documentModel.getFileModels()) {
                if (!(documentFile instanceof TableModel)) {
                    continue;
                }
                URL featureTypeUrl = resolveFeatureTypeUrl(documentModelUrl, documentModel, documentFile);
                FeatureType featureType = loadFeatureType(featureTypeUrl);
                documentFile.setFeatureType(featureType);
            }
            return documentModel;
        } catch (JAXBException e) {
            String message = String.format("Fail to parse DocumentModel : %1s", documentModelUrl);
            throw new InvalidModelException(message, e);
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
	private URL resolveFeatureTypeUrl(URL documentModelUrl,DocumentModel documentModel,FileModel documentFile) throws MalformedURLException {
	    String parentUrl = getParentURL(documentModelUrl);
	    if ( documentModelUrl.getProtocol().equals("file") ) {
	        /* config export convention */
	        // validator-config-cnig/config/cnig_PLU_2017/files.xml
	        // validator-config-cnig/config/cnig_PLU_2017/types/ZONE_URBA.xml
	        return new URL(parentUrl+"/types/"+documentFile.getName() + ".xml");
	    }else {
	        /* URL convention */
            // https://www.geoportail-urbanisme.gouv.fr/standard/cnig_PLU_2017.xml
	        // https://www.geoportail-urbanisme.gouv.fr/standard/cnig_PLU_2017/types/ZONE_URBA.xml
	        return new URL(parentUrl+"/"+documentModel.getName()+"/types/"+documentFile.getName() + ".xml");
	    }
	}

	/**
	 * @param url
	 * @return
	 */
	private String getParentURL(URL url) {
	    String path = url.toString();
	    int lastSlashPos = path.lastIndexOf('/');
	    if (lastSlashPos >= 0)
	    {
	        return path.substring(0, lastSlashPos); //strip off the slash
	    }else {
	        String message = String.format("Fail to get parent URL for : %1s", url);
	        throw new RuntimeException(message);
	    }
	}

	/**
	 * Read File as a DocumentModel (files.xml)
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
    public FeatureType loadFeatureType(URL url) throws ModelNotFoundException, InvalidModelException {
        log.info(MARKER, "loadFeatureType({}) ...", url);

        try {
            return (FeatureType) unmarshaller.unmarshal(url);
        } catch (JAXBException e) {
            String message = String.format("Fail to parse FeatureType : %1s", url);
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
			// TODO change here
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
