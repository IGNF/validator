package fr.ign.validator.data;

import fr.ign.validator.Context;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.string.transform.IsoControlEscaper;
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
	
	enum BindingStatus {
		SUCCESS,
		FAILURE
	};
	
	/**
	 * Le modèle décrivant l'attribut
	 */
	private AttributeType<T> type ;
	
	/**
	 * La valeur à valider
	 */
	private Object value ;

	/**
	 * Résultat de la conversion de la valeur dans le type adéquat
	 */
	private BindingStatus bindingStatus ;
	
	/**
	 * La valeur de l'attribut convertie dans le type correspondant
	 */
	private T bindedValue ;


	
	/**
	 * Construction d'un attribut avec un type et une valeur
	 * @param type
	 * @param value
	 */
	public Attribute(AttributeType<T> type, Object value){
		this.type  = type ;
		this.value = value ;
		try {
			this.bindedValue = type.bind(value);
			this.bindingStatus = BindingStatus.SUCCESS;			
		}catch (IllegalArgumentException e){
			this.bindingStatus = BindingStatus.FAILURE;
		}
	}
	
	/**
	 * @return the type
	 */
	public AttributeType<T> getType() {
		return type;
	}
	
	/**
	 * Get original value
	 * @return
	 */
	public Object getValue(){
		return value;
	}
	
	/**
	 * @return the binded value
	 */
	public T getBindedValue() {
		return bindedValue;
	}


	@Override
	public void validate(Context context) {
		context.beginData(this);

		if ( bindingStatus.equals(BindingStatus.SUCCESS) ){
			for (Validator<Attribute<T>> validator : getType().getValidators()) {
				validator.validate(context, this);
			}
		}else{
			IsoControlEscaper transform = new IsoControlEscaper(false);
			context.report(
				ErrorCode.ATTRIBUTE_INVALID_FORMAT, 
				transform.transform(value.toString()),
				type.getTypeName()
			);
		}
		context.endData(this);
	}

}
