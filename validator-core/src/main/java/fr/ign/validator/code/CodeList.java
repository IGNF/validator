package fr.ign.validator.code;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 
 * Represents a list of values associated to translation. Values are loaded as a
 * ResourceBundle from src/main/resources/metadata/codes/[NAME].properties
 * 
 * @author MBorne
 *
 */
public class CodeList {
    /**
     * The name of the code list
     */
    private String name;
    /**
     * The list of allowed codes associated to descriptions
     */
    private Map<String, String> codes = new HashMap<>();

    private CodeList(String name, Map<String, String> codes) {
        this.name = name;
        this.codes = codes;
    }

    /**
     * Get the name of the list
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Gets allowed values
     * 
     * @return
     */
    public Collection<String> getAllowedValues() {
        return codes.keySet();
    }

    /**
     * Gets translation for a given code
     * 
     * @param code
     * @return
     */
    public String getDescription(String code) {
        return codes.get(code);
    }

    /**
     * Gets a code list by a given name
     * 
     * TODO Read JSON file where objects contains at least a name
     * 
     * @param name
     * @return
     */
    public static CodeList getCodeList(String name) {
        /* load bundle file */
        ResourceBundle bundle = ResourceBundle.getBundle("codes/" + name);

        /* retreive code descriptions */
        Map<String, String> codes = new LinkedHashMap<>();
        List<String> keys = new ArrayList<>(bundle.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            codes.put(key, bundle.getString(key));
        }

        return new CodeList(name, codes);
    }

}
