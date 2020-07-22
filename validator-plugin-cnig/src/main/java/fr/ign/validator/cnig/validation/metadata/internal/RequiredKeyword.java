package fr.ign.validator.cnig.validation.metadata.internal;

/**
 * Helper class to describe required keywords
 */
public class RequiredKeyword {

    /**
     * Descriptive name to report error
     */
    public String name;
    /**
     * Thesaurus name regexp to find the given keyword
     */
    public String thesaurusName;
    /**
     * Expected value for the keyword according to document name
     */
    public String expectedValue;

    public RequiredKeyword(
        String name,
        String thesaurusName,
        String expectedValue) {
        this.name = name;
        this.thesaurusName = thesaurusName;
        this.expectedValue = expectedValue;
    }
}
