package fr.ign.validator.model;

public class DocumentConstraints {
    /**
     * Constraint - The name of the document folder as a regexp (e.g.
     * "(2A|2B|[0-9]{2})[0-9]{3}_PLU_[0-9]{8}")
     * 
     * @since 4.0 previously regexp
     */
    public String folderName;
    /**
     * Constraint - Excepted value in metadata specifications
     * 
     * @since 4.0
     */
    public String metadataSpecification;

    public DocumentConstraints() {
    }
}