package fr.ign.validator.data.file;

import java.io.File;
import java.io.IOException;

import fr.ign.validator.Context;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.data.Table;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.model.file.EmbeddedTableModel;
import fr.ign.validator.model.file.MultiTableModel;
import fr.ign.validator.tools.MultiTableReader;

public class MultiTableFile extends DocumentFile {

    private MultiTableModel fileModel;

    public MultiTableFile(MultiTableModel fileModel, File path) {
        super(path);
        this.fileModel = fileModel;
    }

    @Override
    public MultiTableModel getFileModel() {
        return fileModel;
    }

    @Override
    protected void validateContent(Context context) {
        try {
            log.info(MARKER, "Validate '{}' according to {}...", getPath(), fileModel);
            MultiTableReader reader = MultiTableReader.createMultiTableReader(getPath());
            for (String tableName : reader.getTableNames()) {
                String relativePath = context.relativize(getPath()) + "#" + tableName;
                EmbeddedTableModel tableModel = fileModel.getTableModelByName(tableName);
                if (tableModel == null || tableModel.getFeatureType() == null) {
                    // TODO report MULTITABLE_MISSING_MODEL
                    log.warn(
                        MARKER, "Validate '{}' according to {} : no FeatureType found for '{}'!",
                        getPath(),
                        fileModel,
                        tableName
                    );
                    continue;
                }
                Table table = new Table(
                    tableModel.getFeatureType(),
                    reader.getTableReader(tableName),
                    relativePath
                );
                table.validate(context);
            }

        } catch (IOException e) {
            log.error(MARKER, "Fail to read file '{}'!", getPath());
            context.report(
                context.createError(CoreErrorCodes.FILE_NOT_OPENED)
                    .setMessageParam("FILEPATH", context.relativize(getPath()))
            );
        }
    }

}
