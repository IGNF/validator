package fr.ign.validator.cnig.process;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.ValidatorListener;
import fr.ign.validator.cnig.sup.AdditionalColumnsBuilder;
import fr.ign.validator.cnig.sup.DatabaseSUP;
import fr.ign.validator.cnig.sup.DatabaseSUPFactory;
import fr.ign.validator.data.Document;

/**
 * Post-process relations between SUP tables to adds columns to "GENERATEUR" and
 * "ASSIETE" tables in output data.
 * 
 * @author MBorne
 */
public class SupRelationsPostProcess implements ValidatorListener {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("SupRelationsPostProcess");

    @Override
    public void beforeMatching(Context context, Document document) throws Exception {

    }

    @Override
    public void beforeValidate(Context context, Document document) throws Exception {

    }

    @Override
    public void afterValidate(Context context, Document document) throws Exception {
        File tempDirectory = getTempDirectory(context);

        /*
         * Create DatabaseSUP instance to explore relations.
         */
        DatabaseSUPFactory databaseFactory = new DatabaseSUPFactory(tempDirectory);
        DatabaseSUP database = databaseFactory.createFromDataDirectory(context.getDataDirectory());
        if (database == null) {
            log.warn(MARKER, "skipped due to failure in DatabaseSUP creation");
            return;
        }

        /*
         * Add columns to GENERATEUR and ASSIETTE in DATA directory
         * ('fichier','nomsuplitt',...)
         */
        AdditionalColumnsBuilder builder = new AdditionalColumnsBuilder(
            database,
            tempDirectory
        );
        builder.addColumnsToGenerateurAndAssietteFiles(context.getDataDirectory());
    }

    /**
     * Get temp directory
     */
    private File getTempDirectory(Context context) {
        return new File(context.getDataDirectory(), "tmp");
    }

}
