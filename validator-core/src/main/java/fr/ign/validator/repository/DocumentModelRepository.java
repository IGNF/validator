package fr.ign.validator.repository;

import java.io.IOException;
import java.util.List;

import fr.ign.validator.model.DocumentModel;

/**
 * DocumentModel repository
 * @author MBorne
 *
 */
public interface DocumentModelRepository {

	/**
	 * List all document model names
	 * @return
	 */
	public List<String> listAll();
	
	/**
	 * Find all DocumentModel
	 * @return
	 */
	public List<DocumentModel> findAll() throws IOException ;
	
	/**
	 * Find document model by name
	 * @param documentModelName
	 * @return
	 */
	public DocumentModel findOneByName(String documentModelName) throws IOException ;

}
