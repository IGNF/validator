package fr.ign.validator.model.type;

import com.fasterxml.jackson.annotation.JsonTypeName;

import fr.ign.validator.model.AttributeType;

/**
 * Represents a boolean
 *
 * @author MBorne
 *
 */
@JsonTypeName(BooleanType.TYPE)
public class BooleanType extends AttributeType<Boolean> {

    public static final String TYPE = "Boolean";

    public BooleanType() {
        super(Boolean.class);
    }

    @Override
    public String getTypeName() {
        return TYPE;
    }

    @Override
    public Boolean bind(Object value) {
        if (value == null || value instanceof Boolean) {
            return (Boolean) value;
        }

        if (value.equals("1") || value.equals("T") || value.equals("t") || value.equals("Y") || value.equals("y")) {
            return true;
        }

        if (value.equals("0") || value.equals("F") || value.equals("f") || value.equals("N") || value.equals("n")) {
            return false;
        }

        throw new IllegalArgumentException(
            "Format de booléen invalide : " + value
        );
    }

    @Override
    public String format(Boolean value) {
        if (null == value) {
            return null;
        }
        return value.booleanValue() ? "1" : "0";
    }

}
