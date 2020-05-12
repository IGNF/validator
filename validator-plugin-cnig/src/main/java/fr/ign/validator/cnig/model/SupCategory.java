package fr.ign.validator.cnig.model;

import org.apache.commons.lang.StringUtils;

public class SupCategory {

    public static final String REGEXP = "[a-zA-Z0-9]+";

    /**
     * Indicates if given parameter is a valid SUP category
     * 
     * @param siren
     * @return
     */
    public static boolean isValid(String value) {
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        return value.matches(REGEXP);
    }

}
