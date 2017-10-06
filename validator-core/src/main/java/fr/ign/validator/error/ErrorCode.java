package fr.ign.validator.error;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Définit les constantes des messages d'erreurs et assure une tracabilité des identifiants créés
 * 
 * @author CBouche
 * @author MBorne
 */
public class ErrorCode {
	
	private static ConcurrentMap<String, ErrorCode> errorCodeMap = new ConcurrentHashMap<String, ErrorCode>();
	
	/*
	 * Code d'erreur : reservé au validateur
	 */
	public static final ErrorCode VALIDATOR_EXCEPTION = ErrorCode.valueOf("VALIDATOR_EXCEPTION") ;

	/*
 	 * Niveau DOCUMENT
 	 */	 
	public static final ErrorCode DIRECTORY_UNEXPECTED_NAME = ErrorCode.valueOf("DIRECTORY_UNEXPECTED_NAME") ;

	/*
	 * Niveau FileModel
	 */
	public static final ErrorCode FILE_MISSING_RECOMMANDED = ErrorCode.valueOf("FILE_MISSING_RECOMMANDED") ;
	public static final ErrorCode FILE_MISSING_RECOMMANDED_DIRECTORY = ErrorCode.valueOf("FILE_MISSING_RECOMMANDED_DIRECTORY") ;
	
	public static final ErrorCode FILE_MISSING_MANDATORY = ErrorCode.valueOf("FILE_MISSING_MANDATORY") ;
	public static final ErrorCode FILE_MISSING_MANDATORY_DIRECTORY = ErrorCode.valueOf("FILE_MISSING_MANDATORY_DIRECTORY") ;
	
	public static final ErrorCode FILE_UNEXPECTED = ErrorCode.valueOf("FILE_UNEXPECTED") ;
	
	public static final ErrorCode FILE_MISPLACED = ErrorCode.valueOf("FILE_MISPLACED") ;
	public static final ErrorCode FILE_EMPTY = ErrorCode.valueOf("FILE_EMPTY") ;

	
	// FileModel / Metadata
	
	public static final ErrorCode METADATA_MULTIPLE_FILES = ErrorCode.valueOf("METADATA_MULTIPLE_FILES");
	public static final ErrorCode METADATA_INVALID_FILE    = ErrorCode.valueOf("METADATA_INVALID_FILE") ;
	public static final ErrorCode METADATA_IGNORED_FILE    = ErrorCode.valueOf("METADATA_IGNORED_FILE") ;
	
	public static final ErrorCode METADATA_FILEIDENTIFIER_NOT_FOUND = ErrorCode.valueOf("METADATA_FILEIDENTIFIER_NOT_FOUND") ;
	public static final ErrorCode METADATA_MD_IDENTIFIER_NOT_FOUND = ErrorCode.valueOf("METADATA_MD_IDENTIFIER_NOT_FOUND") ;
	

	// FileModel / Table
	public static final ErrorCode TABLE_MISSING_ATTRIBUTE = ErrorCode.valueOf("TABLE_MISSING_ATTRIBUTE") ;
	public static final ErrorCode TABLE_MISSING_NULLABLE_ATTRIBUTE = ErrorCode.valueOf("TABLE_MISSING_NULLABLE_ATTRIBUTE") ;
	public static final ErrorCode TABLE_UNEXPECTED_ATTRIBUTE = ErrorCode.valueOf("TABLE_UNEXPECTED_ATTRIBUTE") ;
	public static final ErrorCode TABLE_UNEXPECTED_ENCODING = ErrorCode.valueOf("TABLE_UNEXPECTED_ENCODING");
	public static final ErrorCode TABLE_MISSING_GEOMETRY = ErrorCode.valueOf("TABLE_MISSING_GEOMETRY");
	
	/*
	 * Code d'erreurs : niveau FEATURE / Attribute
	 */
	
	public static final ErrorCode ATTRIBUTE_INVALID_FORMAT = ErrorCode.valueOf("ATTRIBUTE_INVALID_FORMAT") ;
	public static final ErrorCode ATTRIBUTE_SIZE_EXCEEDED = ErrorCode.valueOf("ATTRIBUTE_SIZE_EXCEEDED") ;
	public static final ErrorCode ATTRIBUTE_INVALID_REGEXP = ErrorCode.valueOf("ATTRIBUTE_INVALID_REGEXP") ;
	public static final ErrorCode ATTRIBUTE_UNEXPECTED_VALUE = ErrorCode.valueOf("ATTRIBUTE_UNEXPECTED_VALUE") ;
	public static final ErrorCode ATTRIBUTE_UNEXPECTED_NULL = ErrorCode.valueOf("ATTRIBUTE_UNEXPECTED_NULL") ; 
	public static final ErrorCode ATTRIBUTE_CHARACTERS_ILLEGAL    = ErrorCode.valueOf("ATTRIBUTE_CHARACTERS_ILLEGAL") ;

	public static final ErrorCode ATTRIBUTE_GEOMETRY_INVALID = ErrorCode.valueOf("ATTRIBUTE_GEOMETRY_INVALID") ;
	public static final ErrorCode ATTRIBUTE_GEOMETRY_INVALID_DATA_EXTENT = ErrorCode.valueOf("ATTRIBUTE_GEOMETRY_INVALID_DATA_EXTENT") ;
	
	public static final ErrorCode ATTRIBUTE_URL_NOT_FOUND = ErrorCode.valueOf("ATTRIBUTE_URL_NOT_FOUND") ;
	public static final ErrorCode ATTRIBUTE_PATH_NOT_FOUND = ErrorCode.valueOf("ATTRIBUTE_PATH_NOT_FOUND") ;
	public static final ErrorCode ATTRIBUTE_FILE_NOT_FOUND = ErrorCode.valueOf("ATTRIBUTE_FILE_NOT_FOUND") ;

	public static final ErrorCode FILE_NOT_OPENED = ErrorCode.valueOf("FILE_NOT_OPENED") ;
	
	/*
	 * Spéficique CNIG (à migrer)
	 */
	public static final ErrorCode INSEE_MALFORMED = ErrorCode.valueOf("INSEE_MALFORMED") ;
	public static final ErrorCode SIREN_MALFORMED = ErrorCode.valueOf("SIREN_MALFORMED") ;
	
	public static final ErrorCode UNEXPECTED_DATAPPRO = ErrorCode.valueOf("UNEXPECTED_DATAPPRO") ;
	public static final ErrorCode UNEXPECTED_DATVALID = ErrorCode.valueOf("UNEXPECTED_DATVALID") ; 
	
	public static final ErrorCode UNEXPECTED_SUP_NAME = ErrorCode.valueOf("UNEXPECTED_SUP_NAME") ;
	public static final ErrorCode UNEXPECTED_SUP_GENERATEUR = ErrorCode.valueOf("UNEXPECTED_SUP_GENERATEUR") ;
	public static final ErrorCode UNEXPECTED_SUP_ASSIETTE = ErrorCode.valueOf("UNEXPECTED_SUP_ASSIETTE") ;

	public static final ErrorCode CNIG_DOCUMENT_NO_PDF = ErrorCode.valueOf("CNIG_DOCUMENT_NO_PDF") ;
	public static final ErrorCode CNIG_IDURBA_MALFORMED = ErrorCode.valueOf("CNIG_IDURBA_MALFORMED") ;
	
	public static final ErrorCode CNIG_IDURBA_NOT_FOUND = ErrorCode.valueOf("CNIG_IDURBA_NOT_FOUND");
	
	private final String name;
	
	/**
     * @param name
     */
    private ErrorCode(final String name) {
        this.name = name;
    }

    /**
     * Récupération d'un code d'erreur correspondant à une chaîne de caractère
     * @param name
     * @return
     */
    public static ErrorCode valueOf(String name){
		errorCodeMap.putIfAbsent(name, new ErrorCode(name)) ;
		return errorCodeMap.get(name);
	}

    
    
    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return name;
    }
}
