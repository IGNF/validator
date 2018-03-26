package fr.ign.validator.model.type;

import java.net.MalformedURLException;
import java.net.URL;

import fr.ign.validator.model.AttributeType;

/**
 * Location of a file
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
	 * Conversion in the matching java type
	 * 
	 * @param value
	 * @throws IllegalArgumentException if conversion fails
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

}
