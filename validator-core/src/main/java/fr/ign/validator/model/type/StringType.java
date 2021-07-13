package fr.ign.validator.model.type;

import com.fasterxml.jackson.annotation.JsonTypeName;

import fr.ign.validator.model.AttributeType;
import fr.ign.validator.validation.attribute.MaxLengthValidator;
import fr.ign.validator.validation.attribute.MinLengthValidator;
import fr.ign.validator.validation.attribute.StringEnumValuesValidator;
import fr.ign.validator.validation.attribute.StringPatternValidator;

/**
 * Represents a character string
 * 
 * @author MBorne
 *
 */
@JsonTypeName(StringType.TYPE)
public class StringType extends AttributeType<String> {

    public static final String TYPE = "String";

    public StringType() {
        super(String.class);
        addValidator(new MinLengthValidator<String>());
        addValidator(new MaxLengthValidator<String>());
        addValidator(new StringPatternValidator());
        addValidator(new StringEnumValuesValidator());
    }

    @Override
    public String getTypeName() {
        return TYPE;
    }

    @Override
    public String bind(Object value) {
        if (value == null || value instanceof String) {
            return (String) value;
        }
        return value.toString();
    }

    @Override
    public String format(String value) {
        return value;
    }

}
