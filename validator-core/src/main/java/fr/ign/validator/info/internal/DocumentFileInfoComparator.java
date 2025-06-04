package fr.ign.validator.info.internal;

import java.util.Comparator;

import fr.ign.validator.info.model.DocumentFileInfo;

/**
 *
 * Helper to sort files according to their names
 *
 * @author MBorne
 *
 */
public class DocumentFileInfoComparator implements Comparator<DocumentFileInfo> {

    @Override
    public int compare(DocumentFileInfo o1, DocumentFileInfo o2) {
        return o1.getPath().compareTo(o2.getPath());
    }

}
