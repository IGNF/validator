package fr.ign.validator.cnig.utils;

/**
 * 
 * DocumentName helpers
 * 
 * @author MBorne
 *
 */
public class DocumentNameHelper {

	public static final String REGEXP_YYYYMMDD = "[0-9]{8}" ; 

	public static final String REGEXP_DU_TERRITOIRE = "("+InseeUtils.REGEXP_COMMUNE+"|"+SirenUtils.REGEXP_SIREN+")" ; 
	public static final String REGEXP_DU_TYPE       = "(PLU|PLUI|POS|CC|PSMV)" ;
	public static final String REGEXP_DU_CODE       = "[A-Z]";

	public static final String REGEXP_DU = REGEXP_DU_TERRITOIRE + "_" + REGEXP_DU_TYPE + "_" + DocumentNameHelper.REGEXP_YYYYMMDD+ "(_"+REGEXP_DU_CODE+")?" ;

}
