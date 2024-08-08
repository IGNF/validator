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
import fr.ign.validator.tools.TableReaderOptions;

/**
 * A table storing a set of tables.
 *
 * @author MBorne
 *
 */
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

    /**
     * Get reader with the correct options.
     *
     * @return
     * @throws IOException
     */
    public MultiTableReader getReader() throws IOException {
        TableReaderOptions options = new TableReaderOptions();
        // in order to use GMLAS driver from GDAL
        if (fileModel.getXsdSchema() != null) {
            options.setXsdSchema(fileModel.getXsdSchema());
        }
        return MultiTableReader.createMultiTableReader(getPath(), options);
    }

    @Override
    protected void validateContent(Context context) {
        try {
            log.info(MARKER, "Validate '{}' according to {}...", getPath(), fileModel);

            MultiTableReader reader = getReader();
            for (String tableName : reader.getTableNames()) {
                String relativePath = context.relativize(getPath()) + "#" + tableName;
                EmbeddedTableModel tableModel = fileModel.getTableModelByName(tableName);
                if (tableModel == null || tableModel.getFeatureType() == null) {
                    log.warn(
                        MARKER, "Validate '{}' according to {} : no FeatureType found for '{}'!",
                        getPath(),
                        fileModel,
                        tableName
                    );
                    context.report(
                        context.createError(CoreErrorCodes.MULTITABLE_UNEXPECTED)
                            .setMessageParam("TABLENAME", tableName)
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
