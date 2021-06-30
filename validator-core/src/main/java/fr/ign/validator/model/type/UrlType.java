package fr.ign.validator.model.type;

import java.net.MalformedURLException;
import java.net.URL;

import com.fasterxml.jackson.annotation.JsonTypeName;

import fr.ign.validator.model.AttributeType;
import fr.ign.validator.validation.attribute.MaxLengthValidator;
import fr.ign.validator.validation.attribute.MinLengthValidator;

/**
 * Location of a file
 * 
 * @author MBorne
 */
@JsonTypeName(UrlType.TYPE)
public class UrlType extends AttributeType<URL> {

    public static final String TYPE = "Url";

    public UrlType() {
        super(URL.class);
        addValidator(new MinLengthValidator<URL>());
        addValidator(new MaxLengthValidator<URL>());
    }

    @Override
    public String getTypeName() {
        return TYPE;
    }

    /**
     * Conversion in the matching java type
     * 
     * @param value
     * @throws IllegalArgumentException if conversion fails
     * @return
     */
    public URL bind(Object value) {
        if (value == null || value instanceof URL) {
            return (URL) value;
        }

        try {
            URL url = new URL(value.toString());
            return url;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Format d'URL invalide : " + value);
        }
    }

    @Override
    public String format(URL obj) {
        if (null == obj) {
            return null;
        }
        return obj.toString();
    }

}
