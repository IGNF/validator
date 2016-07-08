package fr.ign.validator.model.type;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.ign.validator.model.Attribute;
import fr.ign.validator.model.AttributeType;

public class DateType extends AttributeType<Date> {

	public DateType() {
		super(Date.class);
	}
	
	@Override
	public String getTypeName() {
		return "Date" ;
	}
	
	@Override
	public Date bind(Object value) {
		if ( value == null || value instanceof Date ){
			return (Date)value ;
		}
		
		String[] formats = {"yyyyMMdd", "yyyy", "dd/MM/yyyy"} ;
		
		String stringDate = value.toString() ;
		
		if ( stringDate.isEmpty() ){
			return null ;
		}
		
		for (String format : formats) {
			Date result = tryParse(stringDate, format) ;
			if ( result != null ){
				return result ;
			}
		}
		
		throw new IllegalArgumentException("Format de date invalide : '"+stringDate+"'");
	}

	/**
	 * Tente une conversion 
	 * @param dateValue
	 * @param dateFormat
	 * @return 
	 */
	private Date tryParse( String dateValue, String dateFormat ) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat( dateFormat ) ;
		try {
			return simpleDateFormat.parse( dateValue ) ;
		} catch (ParseException e) {
			return null ;
		}
	}
	
	
	@Override
	public String format(Date value) {
		if ( null == value ){
			return null ;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd") ;
		return format.format(value) ;
	}
	
	@Override
	public Attribute<Date> newAttribute(Date object) {
		return new Attribute<Date>(this,object);
	}
}
