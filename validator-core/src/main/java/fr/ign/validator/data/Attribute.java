package fr.ign.validator.data;

import fr.ign.validator.Context;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.model.AttributeType;
import fr.ign.validator.string.transform.IsoControlEscaper;
import fr.ign.validator.validation.Validatable;
import fr.ign.validator.validation.Validator;
import fr.ign.validator.validation.attribute.GeometryErrorCode;
import fr.ign.validator.validation.attribute.GeometryIsValidValidator;

/**
 * Represents an attribute of a Feature (value associated to a type)
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
     * Model describing the attribute
     */
    private AttributeType<T> type;

    /**
     * value to validate
     */
    private Object value;

    /**
     * Result of the conversion (of in the value in the correct type)
     */
    private BindingStatus bindingStatus;

    /**
     * The value of the attribute converted in the corresponding type
     */
    private T bindedValue;

    /**
     * Construction of an attribute with type and value
     * 
     * @param type
     * @param value
     */
    public Attribute(AttributeType<T> type, Object value) {
        this.type = type;
        this.value = value;
        try {
            this.bindedValue = type.bind(value);
            this.bindingStatus = BindingStatus.SUCCESS;
        } catch (IllegalArgumentException e) {
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
     * 
     * @return
     */
    public Object getValue() {
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

        if (bindingStatus.equals(BindingStatus.SUCCESS)) {
            for (Validator<Attribute<T>> validator : getType().getValidators()) {
                validator.validate(context, this);
            }
        } else {
            IsoControlEscaper transform = new IsoControlEscaper(false);
            if (type.isGeometry()) {
                context.report(
                    context.createError(CoreErrorCodes.ATTRIBUTE_GEOMETRY_INVALID)
                        .setMessageParam("TYPE_ERROR", GeometryErrorCode.INVALID_WKT.getMessage())
                );
            } else {
                context.report(
                    context.createError(CoreErrorCodes.ATTRIBUTE_INVALID_FORMAT)
                        .setMessageParam("VALUE", transform.transform(value.toString()))
                        .setMessageParam("EXPECTED_TYPE", type.getTypeName())
                );
            }
        }
        context.endData(this);
    }

}
