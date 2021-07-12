package fr.ign.validator.data.file;

import java.io.File;
import java.io.IOException;

import fr.ign.validator.Context;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.data.Table;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.model.file.TableModel;
import fr.ign.validator.tools.TableReader;

public class TableFile extends DocumentFile {

    private TableModel fileModel;

    public TableFile(TableModel fileModel, File path) {
        super(path);
        this.fileModel = fileModel;
    }

    @Override
    public TableModel getFileModel() {
        return fileModel;
    }

    @Override
    protected void validateContent(Context context) {
        validateTable(context, fileModel, getPath());
    }

    /**
     * File validation
     * 
     * @param context
     * @param matchingFile
     */
    protected void validateTable(Context context, TableModel tableModel, File matchingFile) {
        try {
            log.info(MARKER, "Validate '{}' according to {}...", matchingFile, tableModel);
            TableReader reader = TableReader.createTableReader(matchingFile, context.getEncoding());
            Table table = new Table(
                tableModel.getFeatureType(),
                reader,
                context.relativize(matchingFile)
            );
            table.validate(context);
        } catch (IOException e) {
            log.error(MARKER, "Fail to read file '{}'!", matchingFile);
            context.report(
                context.createError(CoreErrorCodes.FILE_NOT_OPENED)
                    .setMessageParam("FILEPATH", context.relativize(matchingFile))
            );
        }
    }

}
