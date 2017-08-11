package fr.ign.validator.model.type;

import java.io.File;

import fr.ign.validator.data.Attribute;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.validation.attribute.FilenameExistsValidator;

/**
 * 
 * Une référence à un fichier donnée par le nom du fichier
 * 
 * Remarque : Contrairement à Path, le chemin n'est pas complet
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
	 * Convertion dans le type java correspondant
	 * @param value
	 * @throws IllegalArgumentException si la conversion échoue
	 * @return
	 */
	public File bind( Object value ) {
		if ( value == null || value instanceof File ){
			return (File)value ;
		}
		//context.getRootDirectory()?
		return new File(value.toString()) ;
	}
	
	
	@Override
	public String format(File value) {
		if ( null == value ){
			return null ;
		}
		return value.toPath().toString() ;
	}
	
	@Override
	public Attribute<File> newAttribute(File object) {
		return new Attribute<File>(this,object);
	}
}
