package fr.ign.validator.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ErrorScope;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.StaticTable;
import fr.ign.validator.tools.Networking;

/**
 * Ensures that the static tables (reference lists) of a DocumentModel can be
 * read before creating the validation database (ex : csv removed from the
 * server publishing the model).
 *
 * An unavailable static table is reported as MODEL_STATIC_TABLE_NOT_FOUND
 * instead of failing the whole validation.
 *
 * Remote static tables (http/https) are downloaded once in the validation
 * directory (static_tables/{name}.csv) to avoid downloading them for each read
 * (table creation and loading).
 */
public class StaticTableChecker {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("StaticTableChecker");

    private StaticTableChecker() {
        // static methods only
    }

    /**
     * Reports the static tables of the documentModel that can't be read and
     * replaces the remote ones by a local copy.
     *
     * @param context
     * @param documentModel
     * @return true if all the static tables can be read
     */
    public static boolean checkAvailability(Context context, DocumentModel documentModel) {
        boolean available = true;
        for (StaticTable staticTable : documentModel.getStaticTables()) {
            File localCopy = getLocalCopyPath(context, staticTable);
            String cause = getUnavailabilityCause(staticTable, localCopy);
            if (cause != null && localCopy != null) {
                // remove partial download
                FileUtils.deleteQuietly(localCopy);
            }
            if (cause == null) {
                if (localCopy != null) {
                    log.info(MARKER, "Static table '{}' downloaded to '{}'", staticTable.getName(), localCopy);
                    staticTable.setData(toURL(localCopy));
                }
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
        return getUnavailabilityCause(staticTable, null);
    }

    /**
     * @param staticTable
     * @param localCopy   optional file where the data is copied
     * @return null if the static table can be read, the reason otherwise
     */
    static String getUnavailabilityCause(StaticTable staticTable, File localCopy) {
        if (staticTable.getData() == null) {
            return "URL non résolue";
        }
        try (InputStream is = Networking.openStream(staticTable.getData())) {
            if (localCopy != null) {
                FileUtils.copyInputStreamToFile(is, localCopy);
            }
            return null;
        } catch (FileNotFoundException e) {
            // HTTP 404 or missing local file
            return "fichier introuvable";
        } catch (UnknownHostException e) {
            return "serveur inconnu";
        } catch (SocketTimeoutException e) {
            return "délai dépassé";
        } catch (IOException e) {
            return e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
        }
    }

    /**
     * Path of the local copy for remote static tables (null for local files or
     * without validation directory)
     */
    private static File getLocalCopyPath(Context context, StaticTable staticTable) {
        URL data = staticTable.getData();
        if (data == null || context.getValidationDirectory() == null) {
            return null;
        }
        String protocol = data.getProtocol();
        if (!protocol.equals("http") && !protocol.equals("https")) {
            return null;
        }
        return new File(context.getValidationDirectory(), "static_tables/" + staticTable.getName() + ".csv");
    }

    private static URL toURL(File file) {
        try {
            return file.toURI().toURL();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static String getLabel(StaticTable staticTable) {
        return staticTable.getTitle() != null ? staticTable.getTitle() : staticTable.getName();
    }

}
