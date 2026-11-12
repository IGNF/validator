package fr.ign.validator.code;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * Represents a list of values associated to translation. Values are loaded as a
 * JSON array from src/main/resources/codes/[NAME].json. Each item is an object
 * with a mandatory "code" property and an optional "description" property.
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
     * @param name
     * @return
     */
    public static CodeList getCodeList(String name) {
        String resource = "codes/" + name + ".json";
        try (InputStream is = CodeList.class.getClassLoader().getResourceAsStream(resource)) {
            if (is == null) {
                throw new IllegalArgumentException("Code list '" + name + "' not found (" + resource + ")");
            }
            JsonNode root = new ObjectMapper().readTree(is);
            if (!root.isArray()) {
                throw new IllegalArgumentException("Code list '" + name + "' must be a JSON array");
            }
            /* codes are sorted to get a stable order */
            Map<String, String> sorted = new TreeMap<>();
            for (JsonNode item : root) {
                JsonNode code = item.get("code");
                if (code == null || !code.isTextual()) {
                    throw new IllegalArgumentException("Code list '" + name + "' : missing 'code' property");
                }
                JsonNode description = item.get("description");
                sorted.put(code.asText(), description != null ? description.asText() : null);
            }
            return new CodeList(name, new LinkedHashMap<>(sorted));
        } catch (IOException e) {
            throw new IllegalArgumentException("Fail to read code list '" + name + "' : " + e.getMessage(), e);
        }
    }

}
