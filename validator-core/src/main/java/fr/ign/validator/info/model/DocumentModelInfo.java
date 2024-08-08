package fr.ign.validator.info.model;

import fr.ign.validator.model.DocumentModel;

/**
 * Expose partial informations about the DocumentModel
 *
 * @author MBorne
 *
 */
public class DocumentModelInfo {
    /**
     * The name of the document model
     */
    private String name;

    public DocumentModelInfo(DocumentModel documentModel) {
        this.name = documentModel.getName();
    }

    public String getName() {
        return name;
    }

}
