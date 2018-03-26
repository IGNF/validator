package fr.ign.validator.cnig.standard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.cnig.utils.SpecificationUtils;
import fr.ign.validator.exception.InvalidMetadataException;
import fr.ign.validator.metadata.Metadata;
import fr.ign.validator.metadata.Specification;
import fr.ign.validator.metadata.gmd.MetadataISO19115;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.repository.DocumentModelRepository;

/**
 * 
 * Find DocumentModel according for CNIG documents
 * 
 * TODO improve coverage / split class (findMetadataSpecification => MetadataLocator)
 * 
 * @author MBorne
 *
 */
public class DocumentModelFinder {
	
	public static final Logger log = LogManager.getRootLogger();
	public static final Marker MARKER = MarkerManager.getMarker("DocumentModelFinder");

	private DocumentModelRepository repository ;
	
	public DocumentModelFinder(DocumentModelRepository repository){
		this.repository = repository;
	}
	
	/**
	 * Find DocumentModel according to DocumentType
	 * @param documentName
	 * @return
	 * @throws IOException 
	 */
	public List<DocumentModel> findByDocumentName(String documentName) throws IOException{
		log.info(MARKER, "findByDocumentName({})...", documentName);
		List<DocumentModel> result = new ArrayList<>();
		List<DocumentModel> candidates = repository.findAll();
		
		for (DocumentModel candidate : candidates) {
			if ( StringUtils.isEmpty(candidate.getRegexp()) ){
				continue;
			}
			if ( ! documentName.matches(candidate.getRegexp() ) ){
				continue;
			}
			log.info(MARKER, "findByDocumentName({}) : found candidate {}", documentName, candidate.getName());
			result.add(candidate);
		}
		return result;
	}

	/**
	 * Find document model according to documentName and metadata file
	 * @return
	 * @throws IOException 
	 */
	public DocumentModel findByDocumentPath(File documentPath) throws IOException{
		log.info(MARKER, "findByDocumentPath({})...", documentPath.getAbsolutePath());
		String documentName = documentPath.getName();
		List<DocumentModel> candidates = findByDocumentName(documentName);
		if ( candidates.isEmpty() ){
			log.info(MARKER, "findByDocumentPath({}) : no model found",documentPath);
			return null;
		}
		// find standard version from metadata
		Specification specification = findMetadataSpecification(documentPath);
		if ( specification == null ){
			log.info(MARKER, "findByDocumentPath({}) : no specification found, returning first result", documentPath);
			return candidates.get(0);
		}
		String version = SpecificationUtils.parseCnigSpecification(specification).version ;
		log.info(MARKER, "findByDocumentPath({}) : searching DocumentModel for version {}...", documentPath, version);
		for (DocumentModel candidate : candidates) {
			log.info(MARKER, "findByDocumentPath({}) : candidate : {}", candidate.getName());
			if ( candidate.getName().endsWith(version) ){
				return candidate;
			}
		}
		log.info(MARKER, "findByDocumentPath({}) : no document model found for version {}", documentPath, version);
		return candidates.get(0);
	}

	/**
	 * Find specification from metadata (null if no CNIG specification found)
	 * @param documentPath
	 * @return
	 */
	private Specification findMetadataSpecification(File documentPath){
		@SuppressWarnings("unchecked")
		Collection<File> candidateFiles = FileUtils.listFiles(documentPath, new String[]{"xml","XML"}, true);
		for (File candidateFile : candidateFiles) {
			try {
				Metadata metadata = MetadataISO19115.readFile(candidateFile);
				Specification specification = SpecificationUtils.findCnigSpecification(metadata);
				return specification;
			}catch (InvalidMetadataException e){
				// ignore file (pure XML, not a metadata)
			}
		}
		return null;
	}

}
