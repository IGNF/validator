package fr.ign.validator.model.constraint;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import fr.ign.validator.exception.InvalidModelException;

/**
 * Foreign Key Constraint
 * 
 * ALTER TABLE featureType ADD CONSTRAINT featureTypeConstraint FOREIGN
 * KEY(key1, key2) REFERENCES staticTable(reference1, reference2)
 *
 * @author cbouche
 *
 */
public class ForeignKeyConstraint {

    /**
     * Names of columns in the FeatureType
     */
    private List<String> sourceColumnNames = new ArrayList<String>();

    /**
     * Name of the static table showing valid compound values
     */
    private String targetTableName;

    /**
     * Names of columns in the static table
     */
    private List<String> targetColumnNames = new ArrayList<String>();

    public ForeignKeyConstraint() {
    }

    public String getTargetTableName() {
        return targetTableName;
    }

    public void setTargetTableName(String targetTableName) {
        this.targetTableName = targetTableName;
    }

    public List<String> getSourceColumnNames() {
        return sourceColumnNames;
    }

    public void setSourceColumnNames(List<String> sourceColumnNames) {
        this.sourceColumnNames = sourceColumnNames;
    }

    public List<String> getTargetColumnNames() {
        return targetColumnNames;
    }

    public void setTargetColumnNames(List<String> targetColumnNames) {
        this.targetColumnNames = targetColumnNames;
    }

    @Override
    public String toString() {
        return String.format(
            "(%s) REFERENCES %s(%s)",
            String.join(",", sourceColumnNames),
            targetTableName,
            String.join(",", targetColumnNames)
        );
    }

    public static ForeignKeyConstraint parseForeignKey(String foreignKeyString) {
        // TODO replace regexp to protect from SQL injection
        // String regex = "\\([a-zA-Z0-9_]+(, *[a-zA-Z0-9_]+)*\\) +REFERENCES
        // +[a-zA-Z0-9_]+ *\\([a-zA-Z0-9_]+(, *[a-zA-Z0-9_]+)*\\)";
        // remove whitespaces
        foreignKeyString = foreignKeyString.replaceAll("\\(\\s+", "\\(")
            .replaceAll("\\)\\s+", "\\)")
            .replaceAll(",\\s+", ",")
            .replaceAll("\\s+\\(", "\\(")
            .replaceAll("\\s+\\)", "\\)")
            .replaceAll("\\s+,", ",");
        String regex = "\\([a-zA-Z0-9_]+(,[a-zA-Z0-9_]+)*\\)REFERENCES *[a-zA-Z0-9_]+\\([a-zA-Z0-9_]+(,[a-zA-Z0-9_]+)*\\)";
        if (!Pattern.matches(regex, foreignKeyString)) {
            throw new InvalidModelException(
                String.format(
                    "ForeignKeyConstraint - unable to parse key %s",
                    foreignKeyString
                )
            );
        }

        String[] stringPart = foreignKeyString.split("REFERENCES");
        String[] sourceColumns = stringPart[0].substring(1, stringPart[0].length() - 1).split(",");
        String[] targetPart = stringPart[1].split("\\(");
        String[] targetColumns = targetPart[1].substring(0, targetPart[1].length() - 1).split(",");

        if (sourceColumns.length != targetColumns.length) {
            throw new InvalidModelException(
                String.format(
                    "ForeignKeyConstraint - unable to parse key %s - columns length must match",
                    foreignKeyString
                )
            );
        }

        ForeignKeyConstraint constraint = new ForeignKeyConstraint();
        constraint.setTargetTableName(targetPart[0].trim());

        for (String column : sourceColumns) {
            constraint.getSourceColumnNames().add(column.trim());
        }

        for (String column : targetColumns) {
            constraint.getTargetColumnNames().add(column.trim());
        }

        return constraint;
    }

}
