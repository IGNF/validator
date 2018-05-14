package fr.ign.validator.model.type;

import java.io.File;
import java.net.URI;

import fr.ign.validator.model.AttributeType;
import fr.ign.validator.validation.attribute.FilenameExistsValidator;

/**
 * 
 * Reference to a file given by filename
 * 
 * Note : unlike Path, the filename is not the full filepath
 * 
 * @author MBorne
 */
public class FilenameType extends AttributeType<File> {
	
	public FilenameType() {
		super(File.class);
		addValidator(new FilenameExistsValidator());
	}
	
	@Override
	public String getTypeName() {
		return "Filename" ;
	}
	
	/**
	 * Conversion in the matching java type
	 * 
	 * @param value
	 * @throws IllegalArgumentException if conversion fails
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
