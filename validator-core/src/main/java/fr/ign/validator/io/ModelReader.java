package fr.ign.validator.io;

import java.io.File;
import java.net.URL;

import fr.ign.validator.exception.InvalidModelException;
import fr.ign.validator.exception.ModelNotFoundException;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;

/**
 * Interface to load DocumentModel and FeatureType from URL or File
 */
public interface ModelReader {

    /**
     * Get format (json or xml, lowercase)
     * @return
     */
    public String getFormat() ;
    
    
    /**
     * Read File as a DocumentModel (files.xml)
     * 
     * @param documentModelPath
     * @return
     */
    DocumentModel loadDocumentModel(File documentModelPath) throws ModelNotFoundException, InvalidModelException;

    /**
     * Read DocumentModel from URL
     * 
     * @param documentModelUrl
     */
    DocumentModel loadDocumentModel(URL documentModelUrl) throws ModelNotFoundException, InvalidModelException;

    /**
     * Load FeatureType from file
     * 
     * @param featureTypePath
     * @return
     * @throws ModelNotFoundException, InvalidModelException
     */
    FeatureType loadFeatureType(File featureTypePath) throws ModelNotFoundException, InvalidModelException;

    /**
     * Load FeatureType from URL
     * 
     * @param featureTypeUrl
     * @return
     * @throws ModelNotFoundException, InvalidModelException
     */
    FeatureType loadFeatureType(URL featureTypeUrl) throws ModelNotFoundException, InvalidModelException;

}