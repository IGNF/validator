package fr.ign.validator.data;

import fr.ign.validator.Context;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.validation.Validatable;
import fr.ign.validator.validation.Validator;

/**
 * 
 * Représente un attribut d'une Feature, i.e. une valeur associée à un type
 * 
 * @author MBorne
 *
 */
public class Attribute<T> implements Validatable {
	/**
	 * Le modèle décrivant l'attribut
	 */
	private AttributeType<T> type ;
	/**
	 * La valeur de l'attribut
	 */
	private T value ;
	
	/**
	 * Construction d'un attribut avec un type et une valeur
	 * @param type
	 * @param value
	 */
	public Attribute(AttributeType<T> type, T value){
		this.type  = type ;
		this.value = value ;
	}
	
	/**
	 * @return the type
	 */
	public AttributeType<T> getType() {
		return type;
	}

	/**
	 * @return the value
	 */
	public T getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(Object value) {
		this.value = type.bind(value) ;
	}


	@Override
	public void validate(Context context) {
		context.beginData(this);
		for (Validator<Attribute<T>> validator : getType().getValidators()) {
			validator.validate(context, this);
		}
		context.endData(this);
	}

}
