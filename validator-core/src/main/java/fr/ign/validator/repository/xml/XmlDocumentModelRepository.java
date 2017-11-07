package fr.ign.validator.repository.xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.repository.DocumentModelRepository;
import fr.ign.validator.xml.XmlModelManager;

/**
 * XML implementation with a configuration directory
 * @author MBorne
 *
 */
public class XmlDocumentModelRepository implements DocumentModelRepository {

	public static final Logger log = LogManager.getRootLogger() ;
	public static final Marker MARKER = MarkerManager.getMarker("XmlDocumentModelRepository") ;
	
	private File configDir ;
	
	public XmlDocumentModelRepository(File configDir){
		this.configDir = configDir;
	}
	
	@Override
	public List<String> listAll() {
		log.info(MARKER, "listAll()...");
		List<String> result = new ArrayList<>();
		for ( File candidate : configDir.listFiles() ){
			if ( ! candidate.isDirectory() ){
				continue;
			}
			File mapping = new File(candidate,"files.xml");
			if ( ! mapping.exists() ){
				continue;
			}
			result.add(candidate.getName());
		}
		return result;
	}

	@Override
	public List<DocumentModel> findAll() throws IOException {
		log.info(MARKER, "findAll()...");
		List<String> documentModelNames = listAll();
		List<DocumentModel> result = new ArrayList<>(documentModelNames.size());
		for (String documentModelName : documentModelNames) {
			result.add(findOneByName(documentModelName));
		}
		return result;
	}
	
	@Override
	public DocumentModel findOneByName(String documentModelName) throws IOException {
		log.info(MARKER, "findOneByName({})...",documentModelName);
		File documentModelDirectory = new File(configDir,documentModelName);
		if ( ! documentModelDirectory.exists() ){
			return null;
		}
		File documentModelPath = new File(documentModelDirectory,"files.xml");
		if ( ! documentModelPath.exists() ){
			return null;
		}
		try {
			XmlModelManager reader = new XmlModelManager();
			return reader.loadDocumentModel(documentModelPath);
		} catch (JAXBException e) {
			throw new IOException("invalid DocumentModel : "+documentModelDirectory,e);
		}
	}

}
