package fr.ign.validator.cnig.standard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
 * Find DocumentModel for CNIG documents according to document name and metadata specification
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
     * 
     * Note that candidates are sorted by descendant name so that candidate[0] is the last version
     * 
	 * @param documentName
	 * @return
	 * @throws IOException 
	 */
	public List<DocumentModel> findCandidatesByDocumentName(String documentName) throws IOException{
		log.info(MARKER, "findByDocumentName({})...", documentName);
		List<DocumentModel> result = new ArrayList<>();
		List<DocumentModel> candidates = repository.findAll();
		
		for (DocumentModel candidate : candidates) {
			if ( StringUtils.isEmpty(candidate.getRegexp()) ){
				continue;
			}
			if ( ! documentName.matches("(?i)"+candidate.getRegexp() ) ){
				continue;
			}
			log.info(MARKER, "findByDocumentName({}) : found candidate {}", documentName, candidate.getName());
			result.add(candidate);
		}
		
		/*
		 * Sort by name
		 */
		Collections.sort(result, new Comparator<DocumentModel>() {
			@Override
			public int compare(DocumentModel a, DocumentModel b) {
				return b.getName().compareTo(a.getName());
			}
		});

		return result;
	}

	/**
	 * Find document model according to documentName and metadata file
	 * @return
	 * @throws IOException 
	 */
	public DocumentModel findByDocumentPath(File documentPath) throws IOException{
		log.info(MARKER, "findByDocumentPath({})...", documentPath.getAbsolutePath());
		
		/* Find candidates by documentName */
		String documentName = documentPath.getName();
		List<DocumentModel> candidates = findCandidatesByDocumentName(documentName);
		if ( candidates.isEmpty() ){
			log.info(MARKER, "findByDocumentPath({}) : no candidate found according to {}",documentPath,documentName);
			return null;
		}
		
		/* Find metadata specification (ex : "CNIG PLU 2013") */
		Specification specification = findMetadataSpecification(documentPath);
		if ( specification == null ){
			log.info(MARKER, "findByDocumentPath({}) : no specification found, returning last standard", documentPath);
			return candidates.get(0);
		}

		/* Find version according to specification (ex : "2013") */
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
				/* not a metadata file */
			}
		}
		return null;
	}

}
