package fr.ign.validator.model.type;

import java.io.File;
import java.net.URI;

import com.fasterxml.jackson.annotation.JsonTypeName;

import fr.ign.validator.model.AttributeType;
import fr.ign.validator.validation.attribute.PathExistsValidator;
import fr.ign.validator.validation.attribute.MaxLengthValidator;
import fr.ign.validator.validation.attribute.MinLengthValidator;

/**
 *
 * Path to a file, relative to Document root
 *
 * @author MBorne
 */
@JsonTypeName(PathType.TYPE)
public class PathType extends AttributeType<File> {

    public static final String TYPE = "Path";

    public PathType() {
        super(File.class);
        addValidator(new MinLengthValidator<File>());
        addValidator(new MaxLengthValidator<File>());
        addValidator(new PathExistsValidator());
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
    public File bind(Object value) {
        if (value == null || value instanceof File) {
            return (File) value;
        }
        File result = new File(value.toString());
        URI.create(value.toString()); // throws IllegalArgumentException if the given string violates RFC 2396
        return result;
    }

    @Override
    public String format(File value) {
        if (null == value) {
            return null;
        }
        return value.toString();
    }

}
