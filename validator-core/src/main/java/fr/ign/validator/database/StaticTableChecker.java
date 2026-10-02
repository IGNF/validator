package fr.ign.validator.database;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ErrorScope;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.StaticTable;

/**
 * Ensures that the static tables (reference lists) of a DocumentModel can be read before creating the
 * validation database (ex : csv removed from the server publishing the model).
 *
 * An unavailable static table is reported as MODEL_STATIC_TABLE_NOT_FOUND instead of failing the whole
 * validation.
 */
public class StaticTableChecker {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("StaticTableChecker");

    private StaticTableChecker() {
        // static methods only
    }

    /**
     * Reports the static tables of the documentModel that can't be read.
     *
     * @param context
     * @param documentModel
     * @return true if all the static tables can be read
     */
    public static boolean checkAvailability(Context context, DocumentModel documentModel) {
        boolean available = true;
        for (StaticTable staticTable : documentModel.getStaticTables()) {
            String cause = getUnavailabilityCause(staticTable);
            if (cause == null) {
                continue;
            }
            available = false;
            String url = staticTable.getData() != null ? staticTable.getData().toString()
                : String.valueOf(staticTable.getDataReference());
            log.error(MARKER, "Static table '{}' can't be read from '{}' : {}", staticTable.getName(), url, cause);
            context.report(
                context.createError(CoreErrorCodes.MODEL_STATIC_TABLE_NOT_FOUND)
                    .setScope(ErrorScope.DIRECTORY)
                    .setMessageParam("TABLE", getLabel(staticTable))
                    .setMessageParam("URL", url)
                    .setMessageParam("CAUSE", cause)
            );
        }
        return available;
    }

    /**
     * @param staticTable
     * @return null if the static table can be read, the reason otherwise
     */
    static String getUnavailabilityCause(StaticTable staticTable) {
        if (staticTable.getData() == null) {
            return "URL non résolue";
        }
        try (InputStream is = staticTable.getData().openStream()) {
            return null;
        } catch (FileNotFoundException e) {
            // HTTP 404 or missing local file
            return "fichier introuvable";
        } catch (UnknownHostException e) {
            return "serveur inconnu";
        } catch (IOException e) {
            return e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
        }
    }

    private static String getLabel(StaticTable staticTable) {
        return staticTable.getTitle() != null ? staticTable.getTitle() : staticTable.getName();
    }

}
