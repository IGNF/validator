package fr.ign.validator.model.constraint;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * A SQL condition that each row of a FeatureType must respect, with an optional
 * human readable message used in error reports.
 *
 * In JSON, a condition is either a plain SQL string, or an object :
 *
 * <pre>
 * "DATAPPRO IS NOT NULL OR ETAT NOT LIKE '03'"
 * {"sql": "DATAPPRO IS NOT NULL OR ETAT NOT LIKE '03'", "message": "DATAPPRO est obligatoire si ETAT vaut 03"}
 * </pre>
 */
public class ConditionConstraint {

    private String sql;

    private String message;

    public ConditionConstraint() {
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public ConditionConstraint(String sql) {
        this.sql = sql;
    }

    public ConditionConstraint(String sql, String message) {
        this.sql = sql;
        this.message = message;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * @return the custom message, may be null
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the text to show in error reports : the message if defined, the SQL
     *         expression otherwise
     */
    public String getLabel() {
        return message != null && !message.trim().isEmpty() ? message : sql;
    }

    /**
     * Serialized as a plain string when there is no message (compact form)
     */
    @JsonValue
    public Object toJson() {
        if (message == null) {
            return sql;
        }
        Map<String, String> result = new LinkedHashMap<>();
        result.put("sql", sql);
        result.put("message", message);
        return result;
    }

    @Override
    public String toString() {
        return sql;
    }

}
