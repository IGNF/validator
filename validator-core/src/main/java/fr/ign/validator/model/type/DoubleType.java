package fr.ign.validator.model.type;

import com.fasterxml.jackson.annotation.JsonTypeName;

import fr.ign.validator.model.AttributeType;

/**
 * 
 * Represents a double
 * 
 * @author MBorne
 *
 */
@JsonTypeName(DoubleType.TYPE)
public class DoubleType extends AttributeType<Double> {

    public static final String TYPE = "Double";

    public DoubleType() {
        super(Double.class);
    }

    @Override
    public String getTypeName() {
        return TYPE;
    }

    @Override
    public Double bind(Object object) {
        if (object == null || object instanceof Double) {
            return (Double) object;
        }
        String value = object.toString();
        return Double.parseDouble(value);
    }

    @Override
    public String format(Double value) throws IllegalArgumentException {
        if (null == value) {
            return null;
        }
        return value.toString();
    }

}
