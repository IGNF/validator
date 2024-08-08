package fr.ign.validator.cnig.tools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import fr.ign.validator.tools.TableReader;

public class CSV {

    /**
     * Count rows in a CSV file.
     *
     * @param csvFile
     * @return
     * @throws IOException
     */
    public static int countRows(File csvFile) throws IOException {
        TableReader reader = TableReader.createTableReader(csvFile, StandardCharsets.UTF_8);
        int numRows = 0;
        while (reader.hasNext()) {
            numRows++;
            reader.next();
        }
        return numRows;
    }

}
