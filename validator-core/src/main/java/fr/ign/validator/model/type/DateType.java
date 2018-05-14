package fr.ign.validator.model.type;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.ign.validator.model.AttributeType;

/**
 * Represents a date
 *
 */
public class DateType extends AttributeType<Date> {
	
	private static final String FORMAT_A = "yyyyMMdd";
	private static final String FORMAT_B = "dd/MM/yyyy";
	private static final String FORMAT_C = "yyyy-MM-dd";
	private static final String FORMAT_D = "yyyy/MM/dd";
	/**
	 * @deprecated
	 */
	private static final String FORMAT_E = "yyyy";

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
		
		String[] formats = {FORMAT_A, FORMAT_B, FORMAT_C, FORMAT_D, FORMAT_E} ;
		
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
		/*
		 * Relative to depreciation of FORMAT_E. 
		 * Avoid conversion for 20151717 to 01/01/20151717
		 */
		if ( dateFormat.equals(FORMAT_E) && dateValue.length() != 4 ){
			return null;
		}
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat( dateFormat ) ;
		simpleDateFormat.setLenient(false);
		try {
			Date result = simpleDateFormat.parse( dateValue ) ;
			// ensure strict date parsing
			String reformatedDate = simpleDateFormat.format(result);
			if ( reformatedDate.equals(dateValue) ){
				return result;
			}else{
				return null;
			}			
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

}
