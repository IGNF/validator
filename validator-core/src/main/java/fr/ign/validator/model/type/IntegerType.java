package fr.ign.validator.model.type;

import fr.ign.validator.model.AttributeType;

public class IntegerType extends AttributeType<Integer> {
	
	public IntegerType() {
		super(Integer.class);
	}

	@Override
	public String getTypeName() {
		return "Integer" ;
	}
	
	@Override
	public Integer bind(Object object) {
		if ( object == null || object instanceof Integer ){
			return (Integer)object ;
		}
		String value = object.toString();
		Integer result = tryParseInteger(value);
		if ( result == null ){
			result = tryParseDoubleAndRound(value);
		}
		if ( result == null ){
			throw new IllegalArgumentException("Format d'entier invalide : "+value);
		}
		return result ;
	}
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	private Integer tryParseDoubleAndRound(String value) {
		try {
			Double dValue = Double.parseDouble(value) ;
			double rValue = Math.round(dValue.doubleValue()) ;
			
			if ( Math.abs(rValue - dValue) >= 1.0e-7 ){
				return null ;
			}
			
			return (int) rValue ;
		}catch (NumberFormatException e) {
			return null ;
		}
	}

	/**
	 * Tries to pars an integer (e.g. 4)
	 * 
	 * @param value
	 * @return
	 */
	private Integer tryParseInteger(String value){
		try {
			Integer result = Integer.parseInt( value.toString() ) ;
			return result ;
		}catch (NumberFormatException e) {
			return null ;
		}
	}
	
	

	@Override
	public String format(Integer value) {
		if ( null == value ){
			return null ;
		}
		return value.toString() ;
	}

}
