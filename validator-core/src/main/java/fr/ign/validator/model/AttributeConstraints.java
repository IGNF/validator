package fr.ign.validator.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Constraints on AttributeType
 *
 * @author MBorne
 */
@JsonInclude(value = Include.NON_NULL)
public class AttributeConstraints {

    /**
     * Indicates if null values are forbidden
     *
     * @since 4.0 previously "nullable" with opposite value
     *
     * @see fr.ign.validator.validation.attribute.AttributeRequiredValidator
     * @see fr.ign.validator.error.CoreErrorCodes#ATTRIBUTE_UNEXPECTED_NULL
     */
    private boolean required;

    /**
     * Indicates if the value is unique in the table
     *
     * @since 4.0 previously "identifier" supported only by dgpr plugin
     *
     * @see fr.ign.validator.validation.database.AttributeUniqueValidator
     * @see fr.ign.validator.error.CoreErrorCodes#ATTRIBUTE_NOT_UNIQUE
     */
    private boolean unique;

    /**
     * Regexp matching the attribute value
     *
     * @since 4.0 previously "regexp"
     *
     * @see fr.ign.validator.validation.attribute.StringPatternValidator
     * @see fr.ign.validator.error.CoreErrorCodes#ATTRIBUTE_INVALID_REGEXP
     */
    private String pattern;

    /**
     * Minimum length of the value
     *
     * @see fr.ign.validator.validation.attribute.MinLengthValidator
     * @see fr.ign.validator.error.CoreErrorCodes#ATTRIBUTE_SIZE_TOO_SHORT
     *
     * @since 4.1
     */
    private Integer minLength;

    /**
     * Maximum length of the value
     *
     * @since 4.0 previously "size"
     *
     * @see fr.ign.validator.validation.attribute.MaxLengthValidator
     * @see fr.ign.validator.error.CoreErrorCodes#ATTRIBUTE_SIZE_EXCEEDED
     */
    private Integer maxLength;

    /**
     * Restriction on a list of values
     *
     * @since 4.0 previously "listOfValues"
     *
     * @see fr.ign.validator.validation.attribute.StringEnumValuesValidator
     * @see fr.ign.validator.error.CoreErrorCodes#ATTRIBUTE_UNEXPECTED_VALUE
     */
    private List<String> enumValues;

    /**
     * Reference to another table attribute. Format TABLE_NAME.ATTRIBUTE_NAME
     */
    private String reference;

    public AttributeConstraints() {
        this.required = true;
        this.unique = false;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    /**
     * Tell if enum restriction is defined
     *
     * @return
     */
    public boolean hasEnumValues() {
        return enumValues != null && !enumValues.isEmpty();
    }

    @JsonProperty("enum")
    public List<String> getEnumValues() {
        return enumValues;
    }

    public void getEnumValues(List<String> enumValues) {
        this.enumValues = enumValues;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

}