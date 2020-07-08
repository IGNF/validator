package fr.ign.validator.data.file;

import java.io.File;
import java.io.IOException;

import fr.ign.validator.Context;
import fr.ign.validator.data.DocumentFile;
import fr.ign.validator.data.Header;
import fr.ign.validator.data.Row;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.mapping.FeatureTypeMapper;
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
        /*
         * csv file validation
         */
        try {
            TableReader reader = TableReader.createTableReader(matchingFile, context.getEncoding());
            if (!reader.isCharsetValid()) {
                log.error(MARKER, "Invalid charset '{}' for '{}'", context.getEncoding(), matchingFile);
                context.report(
                    context.createError(CoreErrorCodes.TABLE_UNEXPECTED_ENCODING)
                        .setMessageParam("ENCODING", context.getEncoding().toString())
                );
            }

            /*
             * header validation
             */
            String[] columns = reader.getHeader();
            FeatureTypeMapper mapping = new FeatureTypeMapper(columns, tableModel.getFeatureType());
            Header header = new Header(matchingFile, mapping);
            header.validate(context);

            /*
             * feature validation
             */
            int count = 0;
            while (reader.hasNext()) {
                count++;

                Row row = new Row(count, reader.next(), mapping);
                row.validate(context);
                if ( count % 10000 == 0 ) {
                    log.debug(MARKER, "'{}' : {} rows processed...", matchingFile, count);
                }
            }

            /*
             * check for empty file
             */
            if (count == 0) {
                context.report(
                    context.createError(CoreErrorCodes.FILE_EMPTY)
                        .setMessageParam("FILEPATH", context.relativize(matchingFile))
                );
            }

            log.info(MARKER, "'{}' : {} row(s) processed", matchingFile, count);
        } catch (IOException e) {
            log.error(MARKER, "Fail to read file '{}'!", matchingFile);
            context.report(
                context.createError(CoreErrorCodes.FILE_NOT_OPENED)
                    .setMessageParam("FILEPATH", context.relativize(matchingFile))
            );
            return;
        }
    }

}
