package fr.ign.validator.model;

import java.util.List;

/**
 * Constraints on AttributeType
 *
 * @author MBorne
 */
public class AttributeConstraints {
    /**
     * Indicates if null values are forbidden
     * 
     * @since 4.0 previously "nullable" with opposite value
     * 
     * @see fr.ign.validator.validation.attribute.AttributeNullableValidator
     * @see fr.ign.validator.error.CoreErrorCodes#ATTRIBUTE_UNEXPECTED_NULL
     */
    private boolean required;
    /**
     * Indicates if the value is unique in the table
     * 
     * TODO move DGPR validation to validator-core
     * 
     * @since 4.0 previously "identifier" supported only by dgpr plugin
     * 
     * @see fr.ign.validator.dgpr.validation.database.IdentifierValidator
     * @see fr.ign.validator.dgpr.DgprErrorCodes#DGPR_IDENTIFIER_UNICITY
     */
    private boolean unique;
    /**
     * Regexp matching the attribute value TODO rename to pattern
     * 
     * @since 4.0 previously "regexp"
     * 
     * @see fr.ign.validator.validation.attribute.StringRegexpValidator
     * @see fr.ign.validator.error.CoreErrorCodes#ATTRIBUTE_INVALID_REGEXP
     */
    private String pattern;
    /**
     * Limit size of the attribute TODO rename to maxLength
     */
    private Integer size;
    /**
     * Restriction on a list of values TODO rename to enum at JSON level
     */
    private List<String> listOfValues;

    /**
     * Reference to another table attribute. Format TABLE_NAME.ATTRIBUTE_NAME
     * 
     * TODO support alternative format TABLE_NAME(ATTRIBUTE_NAME)
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

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public List<String> getListOfValues() {
        return listOfValues;
    }

    public void setListOfValues(List<String> listOfValues) {
        this.listOfValues = listOfValues;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}