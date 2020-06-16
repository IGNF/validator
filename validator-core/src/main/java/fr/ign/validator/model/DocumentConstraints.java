package fr.ign.validator.model;

/**
 * Constraints on DocumentModel
 *
 * @author MBorne
 */
public class DocumentConstraints {
    /**
     * Constraint - The name of the document folder as a regexp (e.g.
     * "(2A|2B|[0-9]{2})[0-9]{3}_PLU_[0-9]{8}")
     * 
     * @since 4.0 previously regexp
     */
    private String folderName;
    /**
     * Constraint - Excepted value in metadata specifications
     * 
     * @since 4.0
     */
    private String metadataSpecification;

    public DocumentConstraints() {
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getMetadataSpecification() {
        return metadataSpecification;
    }

    public void setMetadataSpecification(String metadataSpecification) {
        this.metadataSpecification = metadataSpecification;
    }
}