package fr.ign.validator.tools.ogr;

/**
 * 
 * ogr2ogr's version is not supported
 * 
 * @author MBorne
 *
 */
public class OgrBadVersionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public OgrBadVersionException(String message){
		super(message);
	}
	
}
