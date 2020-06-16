package fr.ign.validator.model;

import java.util.List;

public class AttributeConstraints {
    /**
     * Regexp matching the attribute value
     * TODO rename to pattern
     */
    private String regexp;
    /**
     * Limit size of the attribute
     * TODO rename to maxLength
     */
    private Integer size;
    /**
     * Indicates if the value is nullable
     * TODO rename to required inverting values
     */
    private boolean nullable;

    /**
     * Indicates if the value represent the feature id
     * TODO rename to unique
     */
    private boolean identifier;
    
    /**
     * Restriction on a list of values
     * TODO rename to enum at JSON level
     */
    private List<String> listOfValues;

    /**
     * Reference to another table attribute.
     * Format TABLE_NAME.ATTRIBUTE_NAME
     * 
     * TODO support alternative format TABLE_NAME(ATTRIBUTE_NAME)
     */
    private String reference;

    public AttributeConstraints() {
        this.nullable = false;
        this.identifier = false;
    }

    public String getRegexp() {
        return regexp;
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public List<String> getListOfValues() {
        return listOfValues;
    }

    public void setListOfValues(List<String> listOfValues) {
        this.listOfValues = listOfValues;
    }

    public boolean isIdentifier() {
        return identifier;
    }

    public void setIdentifier(boolean identifier) {
        this.identifier = identifier;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}