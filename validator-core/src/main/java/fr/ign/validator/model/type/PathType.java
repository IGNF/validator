package fr.ign.validator.model.type;

import java.io.File;
import java.net.URI;

import fr.ign.validator.model.AttributeType;
import fr.ign.validator.validation.attribute.PathExistsValidator;

/**
 * 
 * Un chemin vers un fichier, relatif à la racine du document
 * 
 * @author MBorne
 */
public class PathType extends AttributeType<File> {
	
	public PathType() {
		super(File.class);
		addValidator(new PathExistsValidator());
	}
	
	@Override
	public String getTypeName() {
		return "Path" ;
	}
	
	/**
	 * Convertion dans le type java correspondant
	 * @param value
	 * @throws IllegalArgumentException si la conversion échoue
	 * @return
	 */
	public File bind( Object value ) {
		if ( value == null || value instanceof File ){
			return (File)value ;
		}
		File result = new File(value.toString()) ;
		URI.create(value.toString()); // throws IllegalArgumentException if the given string violates RFC 2396
		return result ;
	}
	
	
	@Override
	public String format(File value) {
		if ( null == value ){
			return null ;
		}
		return value.toString() ;
	}

}
