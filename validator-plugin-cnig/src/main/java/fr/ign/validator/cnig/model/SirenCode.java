package fr.ign.validator.cnig.model;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * Validates SIREN codes (on 9 digits)
 * 
 * @see http://www.insee.fr/fr/methodes/default.asp?page=definitions/numero-siren.htm
 * 
 * @author MBorne
 *
 */
public class SirenCode {

    public static final String REGEXP = "[0-9]{9}";

    /**
     * Indicates if given parameter is a valid SIREN code
     * 
     * @param value
     * @return
     */
    public static boolean isValid(String value) {
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        return value.matches(REGEXP);
    }

}
