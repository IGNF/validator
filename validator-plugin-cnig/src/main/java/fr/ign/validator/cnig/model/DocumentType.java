package fr.ign.validator.cnig.model;

public enum DocumentType {
    PLU,
    PLUi,
    POS,
    CC,
    PSMV,

    SUP,

    SCoT;

    /**
     * Find DocumentType for a given string (case insensitive)
     */
    public static DocumentType find(String code) {
        for (DocumentType candidate : values()) {
            if (candidate.toString().equalsIgnoreCase(code)) {
                return candidate;
            }
        }
        return null;
    }
}
