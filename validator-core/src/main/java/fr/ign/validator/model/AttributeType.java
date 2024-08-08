package fr.ign.validator.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import fr.ign.validator.data.Attribute;
import fr.ign.validator.validation.Validator;
import fr.ign.validator.validation.attribute.AttributeRequiredValidator;
import fr.ign.validator.validation.attribute.CharactersValidator;

/**
 * Describes an attribute of a table (FeatureType)
 *
 * @author MBorne
 *
 * @param <T> the matching java type
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonInclude(value = Include.NON_NULL)
public abstract class AttributeType<T> implements Model, Cloneable {
    /**
     * Matching java class
     */
    private Class<T> clazz;

    /**
     * Attribute name
     */
    private String name;

    /**
     * Attribute description
     */
    private String description;

    /**
     * Constraints on the attribute
     */
    private AttributeConstraints constraints = new AttributeConstraints();

    /**
     * Validators on attributes
     */
    private List<Validator<Attribute<T>>> validators = new ArrayList<Validator<Attribute<T>>>();

    /**
     * Constructing a class and validators by default
     *
     * @param clazz
     */
    protected AttributeType(Class<T> clazz) {
        this.clazz = clazz;
        addValidator(new AttributeRequiredValidator<T>());
        addValidator(new CharactersValidator<T>());
    }

    /**
     * Returns type name
     *
     * @return
     */
    @JsonIgnore
    public abstract String getTypeName();

    /**
     * Indicates if attribute is a geometry
     *
     * @return
     */
    @JsonIgnore
    public boolean isGeometry() {
        return false;
    }

    /**
     * Create an AttributeType instance for a given type name
     *
     * @param type
     * @return
     */
    public static AttributeType<?> forName(String type) {
        return AttributeTypeFactory.getInstance().createAttributeTypeByName(type);
    }

    /**
     * Create an Attribute with a given value
     *
     * @param object
     * @return
     */
    public Attribute<T> newAttribute(Object value) {
        return new Attribute<T>(this, value);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AttributeConstraints getConstraints() {
        return constraints;
    }

    public void setConstraints(AttributeConstraints constraints) {
        this.constraints = constraints;
    }

    @Deprecated
    public boolean hasRegexp() {
        return null != constraints.getPattern();
    }

    @Deprecated
    @JsonIgnore
    public String getRegexp() {
        return constraints.getPattern();
    }

    @Deprecated
    public void setRegexp(String regexp) {
        this.constraints.setPattern(regexp);
    }

    @Deprecated
    @JsonIgnore
    public Integer getSize() {
        return constraints.getMaxLength();
    }

    @Deprecated
    public void setSize(Integer size) {
        this.constraints.setMaxLength(size);
    }

    @Deprecated
    @JsonIgnore
    public boolean isNullable() {
        return !constraints.isRequired();
    }

    @Deprecated
    public void setNullable(boolean nullable) {
        this.constraints.setRequired(!nullable);
    }

    @Deprecated
    @JsonIgnore
    public boolean hasListOfValues() {
        return constraints.getEnumValues() != null;
    }

    @Deprecated
    @JsonIgnore
    public List<String> getListOfValues() {
        return constraints.getEnumValues();
    }

    @Deprecated
    public void setListOfValues(List<String> listOfValues) {
        this.constraints.getEnumValues(listOfValues);
    }

    @Deprecated
    @JsonIgnore
    public boolean isIdentifier() {
        return constraints.isUnique();
    }

    @Deprecated
    public void setIdentifier(boolean identifier) {
        this.constraints.setUnique(identifier);
    }

    @Deprecated
    @JsonIgnore
    public String getReference() {
        return this.constraints.getReference();
    }

    @Deprecated
    public void setReference(String reference) {
        this.constraints.setReference(reference);
    }

    @Deprecated
    @JsonIgnore
    public boolean isReference() {
        return this.constraints.getReference() != null;
    }

    @Deprecated
    @JsonIgnore
    public String getTableReference() {
        if (this.constraints.getReference() == null) {
            return null;
        }
        if (!this.constraints.getReference().contains(".")) {
            return null;
        }
        return this.constraints.getReference().split("\\.")[0];
    }

    @Deprecated
    @JsonIgnore
    public String getAttributeReference() {
        if (this.constraints.getReference() == null) {
            return null;
        }
        if (!this.constraints.getReference().contains(".")) {
            return null;
        }
        return this.constraints.getReference().split("\\.")[1];
    }

    @JsonIgnore
    public List<Validator<Attribute<T>>> getValidators() {
        return this.validators;
    }

    public void addValidator(Validator<Attribute<T>> validator) {
        this.validators.add(validator);
    }

    /**
     * Converts a value in the matching java type. Validates the possibility of a
     * conversion of a value in the java type matching the ValueType
     *
     * @param value
     * @return
     */
    public abstract T bind(Object value) throws IllegalArgumentException;

    /**
     * Formats the value as a string parameter (e.g. YYYYMMDD for dates)
     *
     * Note : null stays null
     *
     * @param value
     * @return
     * @throws IllegalArgumentException if type is incorrect
     */
    public abstract String format(T value) throws IllegalArgumentException;

    /**
     * Formats object in parameter
     *
     * @param value
     * @return
     * @throws IllegalArgumentException if type is incorrect
     */
    public String formatObject(Object value) throws IllegalArgumentException {
        if (value == null) {
            return null;
        }
        if (this.clazz.isAssignableFrom(value.getClass())) {
            return format(this.clazz.cast(value));
        } else {
            throw new IllegalArgumentException(
                String.format(
                    "Invalid type '%1s' for value '%2s'",
                    name,
                    value
                )
            );
        }
    }

    @Override
    public String toString() {
        return name + " (" + getClass().getSimpleName() + ")";
    }

    @SuppressWarnings("unchecked")
    public Object clone() {
        AttributeType<T> attributeType = null;
        try {
            attributeType = (AttributeType<T>) super.clone();
            attributeType.constraints = new AttributeConstraints();
            attributeType.validators = new ArrayList<Validator<Attribute<T>>>(validators.size());
            attributeType.validators.addAll(validators);
        } catch (CloneNotSupportedException cnse) {
            throw new RuntimeException(cnse);
        }

        // returns the clone
        return attributeType;
    }

}
