package fr.ign.validator.cnig.model;

/**
 * 
 * DocumentName helpers
 * 
 * @author MBorne
 *
 */
public class DocumentName {

	public static final String REGEXP_YYYYMMDD = "[0-9]{8}" ; 

	public static final String REGEXP_DU_TERRITOIRE = "("+MunicipalityCode.REGEXP+"|"+SirenCode.REGEXP+")" ; 
	public static final String REGEXP_DU_TYPE       = "(PLU|PLUi|POS|CC|PSMV)" ;
	public static final String REGEXP_DU_CODE       = "[A-Z]";

	public static final String REGEXP_DU = REGEXP_DU_TERRITOIRE + "_" + REGEXP_DU_TYPE + "_" + DocumentName.REGEXP_YYYYMMDD+ "(_"+REGEXP_DU_CODE+")?" ;
	
}
