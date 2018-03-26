package fr.ign.validator.error;

/**
 * Error codes defined in validator-core
 * @author MBorne
 *
 */
public class CoreErrorCodes {
	
	/*
	 * ErrorCode for error in validator
	 */
	public static final ErrorCode VALIDATOR_INFO      = ErrorCode.valueOf("VALIDATOR_INFO");
	public static final ErrorCode VALIDATOR_EXCEPTION = ErrorCode.valueOf("VALIDATOR_EXCEPTION") ;
	
	/*
 	 * ErrorCode for error in Document
 	 */	 
	public static final ErrorCode DIRECTORY_UNEXPECTED_NAME = ErrorCode.valueOf("DIRECTORY_UNEXPECTED_NAME") ;

	/*
 	 * ErrorCode for error in FileModel
 	 */	
	public static final ErrorCode FILE_MISSING_RECOMMANDED = ErrorCode.valueOf("FILE_MISSING_RECOMMANDED") ;
	public static final ErrorCode FILE_MISSING_RECOMMANDED_DIRECTORY = ErrorCode.valueOf("FILE_MISSING_RECOMMANDED_DIRECTORY") ;
	
	public static final ErrorCode FILE_MISSING_MANDATORY = ErrorCode.valueOf("FILE_MISSING_MANDATORY") ;
	public static final ErrorCode FILE_MISSING_MANDATORY_DIRECTORY = ErrorCode.valueOf("FILE_MISSING_MANDATORY_DIRECTORY") ;
	
	public static final ErrorCode FILE_UNEXPECTED = ErrorCode.valueOf("FILE_UNEXPECTED") ;
	
	public static final ErrorCode FILE_MISPLACED = ErrorCode.valueOf("FILE_MISPLACED") ;
	public static final ErrorCode FILE_EMPTY = ErrorCode.valueOf("FILE_EMPTY") ;

	
	/*
 	 * ErrorCode for error in Metadata Files 
 	 */	
	public static final ErrorCode METADATA_MULTIPLE_FILES = ErrorCode.valueOf("METADATA_MULTIPLE_FILES");
	public static final ErrorCode METADATA_INVALID_FILE    = ErrorCode.valueOf("METADATA_INVALID_FILE") ;
	public static final ErrorCode METADATA_IGNORED_FILE    = ErrorCode.valueOf("METADATA_IGNORED_FILE") ;
	
	/*
 	 * ErrorCode for error in Metadata Content
 	 */	
	public static final ErrorCode METADATA_FILEIDENTIFIER_NOT_FOUND   = ErrorCode.valueOf("METADATA_FILEIDENTIFIER_NOT_FOUND") ;
	public static final ErrorCode METADATA_IDENTIFIER_NOT_FOUND       = ErrorCode.valueOf("METADATA_IDENTIFIER_NOT_FOUND") ;
	public static final ErrorCode METADATA_TITLE_NOT_FOUND            = ErrorCode.valueOf("METADATA_TITLE_NOT_FOUND");
	public static final ErrorCode METADATA_ABSTRACT_NOT_FOUND         = ErrorCode.valueOf("METADATA_ABSTRACT_NOT_FOUND");
	
	public static final ErrorCode METADATA_TYPE_NOT_FOUND             = ErrorCode.valueOf("METADATA_TYPE_NOT_FOUND");	
	public static final ErrorCode METADATA_TYPE_INVALID               = ErrorCode.valueOf("METADATA_TYPE_INVALID");	

	public static final ErrorCode METADATA_LOCATORS_EMPTY             = ErrorCode.valueOf("METADATA_LOCATORS_EMPTY");
	public static final ErrorCode METADATA_LOCATOR_NAME_NOT_FOUND     = ErrorCode.valueOf("METADATA_LOCATOR_NAME_NOT_FOUND");
	public static final ErrorCode METADATA_LOCATOR_PROTOCOL_NOT_FOUND = ErrorCode.valueOf("METADATA_LOCATOR_PROTOCOL_NOT_FOUND");
	public static final ErrorCode METADATA_LOCATOR_URL_NOT_FOUND      = ErrorCode.valueOf("METADATA_LOCATOR_URL_NOT_FOUND");
	
	public static final ErrorCode METADATA_LANGUAGE_NOT_FOUND         = ErrorCode.valueOf("METADATA_LANGUAGE_NOT_FOUND");
	public static final ErrorCode METADATA_LANGUAGE_INVALID           = ErrorCode.valueOf("METADATA_LANGUAGE_INVALID");	

	public static final ErrorCode METADATA_EXTENTS_EMPTY              = ErrorCode.valueOf("METADATA_EXTENTS_EMPTY");
	public static final ErrorCode METADATA_EXTENT_INVALID             = ErrorCode.valueOf("METADATA_EXTENT_INVALID");

	public static final ErrorCode METADATA_TOPICCATEGORY_NOT_FOUND    = ErrorCode.valueOf("METADATA_TOPICCATEGORY_NOT_FOUND");
	public static final ErrorCode METADATA_TOPICCATEGORY_INVALID      = ErrorCode.valueOf("METADATA_TOPICCATEGORY_INVALID");

	public static final ErrorCode METADATA_DATES_NOT_FOUND            = ErrorCode.valueOf("METADATA_DATES_NOT_FOUND");
	public static final ErrorCode METADATA_DATEOFPUBLICATION_INVALID  = ErrorCode.valueOf("METADATA_DATEOFPUBLICATION_INVALID");
	public static final ErrorCode METADATA_DATEOFLASTREVISION_INVALID = ErrorCode.valueOf("METADATA_DATEOFLASTREVISION_INVALID");	
	public static final ErrorCode METADATA_DATEOFCREATION_INVALID     = ErrorCode.valueOf("METADATA_DATEOFCREATION_INVALID");
	
	public static final ErrorCode METADATA_CHARACTERSET_NOT_FOUND    = ErrorCode.valueOf("METADATA_CHARACTERSET_NOT_FOUND");
	public static final ErrorCode METADATA_CHARACTERSET_INVALID      = ErrorCode.valueOf("METADATA_CHARACTERSET_INVALID");

	public static final ErrorCode METADATA_DISTRIBUTIONFORMATS_EMPTY         = ErrorCode.valueOf("METADATA_DISTRIBUTIONFORMATS_EMPTY");
	public static final ErrorCode METADATA_DISTRIBUTIONFORMAT_NAME_NOT_FOUND = ErrorCode.valueOf("METADATA_DISTRIBUTIONFORMAT_NAME_NOT_FOUND");
	
	public static final ErrorCode METADATA_SPATIALREPRESENTATIONTYPE_NOT_FOUND = ErrorCode.valueOf("METADATA_SPATIALREPRESENTATIONTYPE_NOT_FOUND");
	public static final ErrorCode METADATA_SPATIALREPRESENTATIONTYPE_INVALID   = ErrorCode.valueOf("METADATA_SPATIALREPRESENTATIONTYPE_INVALID");
	
	public static final ErrorCode METADATA_LINEAGE_NOT_FOUND = ErrorCode.valueOf("METADATA_LINEAGE_NOT_FOUND");

	public static final ErrorCode METADATA_SPATIALRESOLUTIONS_EMPTY              = ErrorCode.valueOf("METADATA_SPATIALRESOLUTIONS_EMPTY");
	public static final ErrorCode METADATA_SPATIALRESOLUTION_INVALID_DENOMINATOR = ErrorCode.valueOf("METADATA_SPATIALRESOLUTION_INVALID_DENOMINATOR");
	public static final ErrorCode METADATA_SPATIALRESOLUTION_INVALID_DISTANCE    = ErrorCode.valueOf("METADATA_SPATIALRESOLUTION_INVALID_DISTANCE");	


	public static final ErrorCode METADATA_SPECIFICATIONS_EMPTY          = ErrorCode.valueOf("METADATA_SPECIFICATIONS_EMPTY");
	public static final ErrorCode METADATA_SPECIFICATION_TITLE_NOT_FOUND = ErrorCode.valueOf("METADATA_SPECIFICATION_TITLE_NOT_FOUND");
	public static final ErrorCode METADATA_SPECIFICATION_DATE_NOT_FOUND  = ErrorCode.valueOf("METADATA_SPECIFICATION_DATE_NOT_FOUND");
	public static final ErrorCode METADATA_SPECIFICATION_DATE_INVALID    = ErrorCode.valueOf("METADATA_SPECIFICATION_DATE_INVALID");	
	public static final ErrorCode METADATA_SPECIFICATION_DEGREE_INVALID  = ErrorCode.valueOf("METADATA_SPECIFICATION_DEGREE_INVALID");

	public static final ErrorCode METADATA_CONTACT_NOT_FOUND = ErrorCode.valueOf("METADATA_CONTACT_NOT_FOUND");	
	
	public static final ErrorCode METADATA_METADATACONTACT_NOT_FOUND = ErrorCode.valueOf("METADATA_METADATACONTACT_NOT_FOUND");		

	public static final ErrorCode METADATA_METADATADATE_NOT_FOUND  = ErrorCode.valueOf("METADATA_METADATADATE_NOT_FOUND");
	public static final ErrorCode METADATA_METADATADATE_INVALID    = ErrorCode.valueOf("METADATA_METADATADATE_INVALID");	

	public static final ErrorCode METADATA_METADATALANGUAGE_NOT_FOUND = ErrorCode.valueOf("METADATA_METADATALANGUAGE_NOT_FOUND");
	public static final ErrorCode METADATA_METADATALANGUAGE_INVALID   = ErrorCode.valueOf("METADATA_METADATALANGUAGE_INVALID");

	/*
 	 * ErrorCode for error in Table (FileModel)
 	 */	
	public static final ErrorCode TABLE_MISSING_ATTRIBUTE = ErrorCode.valueOf("TABLE_MISSING_ATTRIBUTE") ;
	public static final ErrorCode TABLE_MISSING_NULLABLE_ATTRIBUTE = ErrorCode.valueOf("TABLE_MISSING_NULLABLE_ATTRIBUTE") ;
	public static final ErrorCode TABLE_UNEXPECTED_ATTRIBUTE = ErrorCode.valueOf("TABLE_UNEXPECTED_ATTRIBUTE") ;
	public static final ErrorCode TABLE_UNEXPECTED_ENCODING = ErrorCode.valueOf("TABLE_UNEXPECTED_ENCODING");
	public static final ErrorCode TABLE_MISSING_GEOMETRY = ErrorCode.valueOf("TABLE_MISSING_GEOMETRY");
	
	/*
 	 * ErrorCode for error in Features and Attributes
 	 */	
	public static final ErrorCode ATTRIBUTE_INVALID_FORMAT = ErrorCode.valueOf("ATTRIBUTE_INVALID_FORMAT") ;
	public static final ErrorCode ATTRIBUTE_SIZE_EXCEEDED = ErrorCode.valueOf("ATTRIBUTE_SIZE_EXCEEDED") ;
	public static final ErrorCode ATTRIBUTE_INVALID_REGEXP = ErrorCode.valueOf("ATTRIBUTE_INVALID_REGEXP") ;
	public static final ErrorCode ATTRIBUTE_UNEXPECTED_VALUE = ErrorCode.valueOf("ATTRIBUTE_UNEXPECTED_VALUE") ;
	public static final ErrorCode ATTRIBUTE_UNEXPECTED_NULL = ErrorCode.valueOf("ATTRIBUTE_UNEXPECTED_NULL") ; 

	public static final ErrorCode ATTRIBUTE_CHARACTERS_REPLACED   = ErrorCode.valueOf("ATTRIBUTE_CHARACTERS_REPLACED");
	public static final ErrorCode ATTRIBUTE_CHARACTERS_ILLEGAL    = ErrorCode.valueOf("ATTRIBUTE_CHARACTERS_ILLEGAL") ;

	public static final ErrorCode ATTRIBUTE_GEOMETRY_INVALID = ErrorCode.valueOf("ATTRIBUTE_GEOMETRY_INVALID") ;
	public static final ErrorCode ATTRIBUTE_GEOMETRY_INVALID_DATA_EXTENT = ErrorCode.valueOf("ATTRIBUTE_GEOMETRY_INVALID_DATA_EXTENT") ;
	
	public static final ErrorCode ATTRIBUTE_URL_NOT_FOUND = ErrorCode.valueOf("ATTRIBUTE_URL_NOT_FOUND") ;
	public static final ErrorCode ATTRIBUTE_PATH_NOT_FOUND = ErrorCode.valueOf("ATTRIBUTE_PATH_NOT_FOUND") ;
	public static final ErrorCode ATTRIBUTE_FILE_NOT_FOUND = ErrorCode.valueOf("ATTRIBUTE_FILE_NOT_FOUND") ;

	public static final ErrorCode FILE_NOT_OPENED = ErrorCode.valueOf("FILE_NOT_OPENED") ;
	

}
