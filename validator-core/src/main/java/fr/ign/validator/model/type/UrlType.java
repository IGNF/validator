package fr.ign.validator.model.type;

import java.net.MalformedURLException;
import java.net.URL;

import fr.ign.validator.model.Attribute;
import fr.ign.validator.model.AttributeType;

/**
 * Un chemin vers un fichier
 * @author MBorne
 */
public class UrlType extends AttributeType<URL> {
	
	public UrlType() {
		super(URL.class);
	}
	
	@Override
	public String getTypeName() {
		return "Url" ;
	}
	
	/**
	 * Convertion dans le type java correspondant
	 * @param value
	 * @throws IllegalArgumentException si la conversion échoue
	 * @return
	 */
	public URL bind( Object value ) {
		if ( value == null || value instanceof URL ){
			return (URL)value ;
		}
		
		try {
			URL url = new URL(value.toString());
			return url ;
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("Format d'URL invalide : "+value);
		}
	}
	
	@Override
	public String format(URL obj) {
		if ( null == obj ){
			return null ;
		}
		return obj.toString() ;
	}

	@Override
	public Attribute<URL> newAttribute(URL object) {
		return new Attribute<URL>(this,object);
	}
}
