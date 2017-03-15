package fr.ign.validator.model.type;

import fr.ign.validator.model.Attribute;
import fr.ign.validator.model.AttributeType;

/**
 * 
 * Représente un double
 * 
 * @author MBorne
 *
 */
public class DoubleType extends AttributeType<Double> {
	
	public DoubleType() {
		super(Double.class);
	}

	@Override
	public String getTypeName() {
		return "Double" ;
	}

	@Override
	public Double bind(Object object) {
		if ( object == null || object instanceof Double ){
			return (Double)object ;
		}
		String value = object.toString();
		return Double.parseDouble(value);
	}

	@Override
	public String format(Double value) throws IllegalArgumentException {
		if ( null == value ){
			return null ;
		}
		return value.toString() ;
	}

	@Override
	public Attribute<Double> newAttribute(Double object) {
		return new Attribute<Double>(this,object);
	}

}
