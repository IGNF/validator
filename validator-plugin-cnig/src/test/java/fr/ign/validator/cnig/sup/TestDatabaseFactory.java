package fr.ign.validator.cnig.sup;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.cnig.sup.internal.FileLocator;
import fr.ign.validator.database.Database;
import fr.ign.validator.tools.TableReader;

/**
 * Create DatabaseSUP instances for test purpose (legacy method replaced after
 * moving validation database to the core).
 * 
 * @author MBorne
 *
 */
public class TestDatabaseFactory {

    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("TestDatabaseFactory");

    private File tempDirectory;

    public TestDatabaseFactory(File tempDirectory) {
        this.tempDirectory = tempDirectory;
    }

    /**
     * Create DatabaseSUP from CSV files in dataDirectory for test purpose
     * 
     * @param dataDirectory
     * @return
     */
    public DatabaseSUP createFromDataDirectory(File dataDirectory) {
        /*
         * Locate files in data directory (stop process if some are not found)
         */
        FileLocator fileLocator = new FileLocator(dataDirectory);
        File servitudeFile = fileLocator.findServitudeFile();
        if (servitudeFile == null) {
            log.error(MARKER, "SERVITUDE file not found!");
            return null;
        }

        File acteSupFile = fileLocator.findActeSupFile();
        if (acteSupFile == null) {
            log.error(MARKER, "ACTE_SUP file not found!");
            return null;
        }

        File servitudeActeSupFile = fileLocator.findServitudeActeSupFile();
        if (servitudeActeSupFile == null) {
            log.error(MARKER, "SERVITUDE_ACTE_SUP file not found!");
            return null;
        }

        List<File> generateurSupFiles = fileLocator.findGenerateurSupFiles();
        if (generateurSupFiles.isEmpty()) {
            log.error(MARKER, "GENERATEUR_SUP file not found!");
            return null;
        }

        List<File> assietteSupFiles = fileLocator.findAssietteSupFiles();
        if (assietteSupFiles.isEmpty()) {
            log.error(MARKER, "ASSIETTE_SUP file not found!");
            return null;
        }

        /*
         * Create a DatabaseJointureSUP dedicated to explore relations
         */
        log.info(MARKER, "Create temp directory {}...", tempDirectory);
        tempDirectory.mkdir();

        try {
            File databasePath = new File(tempDirectory, "jointure_sup.db");
            log.info(MARKER, "Create DatabaseSUP ...");
            Database database = new Database(databasePath);
            /* create standard validation tables */
            createTableServitude(database);
            createTableActe(database);
            createTableServitudeActe(database);

            DatabaseSUP databaseSUP = new DatabaseSUP(database);
            databaseSUP.createTableGenerateur();
            databaseSUP.createTableAssiete();

            loadTableServitude(database, servitudeFile);
            loadTableActe(database, acteSupFile);
            loadTableServitudeActe(database, servitudeActeSupFile);
            for (File generateurSupFile : generateurSupFiles) {
                loadTableGenerateur(database, generateurSupFile);
            }
            for (File assietteSupFile : assietteSupFiles) {
                loadTableAssiette(database, assietteSupFile);
            }

            return databaseSUP;
        } catch (Exception e) {
            log.error(MARKER, "Fail to create DatabaseSUP from data directory", e);
            return null;
        }
    }

    /**
     * Create SQL table TABLE_SERVITUDE.
     */
    private void createTableServitude(Database database) throws SQLException {
        List<String> columns = new ArrayList<>();
        columns.add(DatabaseSUP.COLUMN_IDSUP);
        columns.add(DatabaseSUP.COLUMN_NOMSUPLITT);
        database.createTable(DatabaseSUP.TABLE_SERVITUDE, columns);
        database.createIndex(DatabaseSUP.TABLE_SERVITUDE, DatabaseSUP.COLUMN_IDSUP);
    }

    /**
     * Load a CSV file into TABLE_SERVITUDE.
     */
    private void loadTableServitude(Database database, File servitudeFile) throws Exception {
        log.info(MARKER, "load table SERVITUDE from {}...", servitudeFile);
        TableReader reader = TableReader.createTableReader(servitudeFile, StandardCharsets.UTF_8);

        int indexIdSup = reader.findColumnRequired(DatabaseSUP.COLUMN_IDSUP);
        int indexNomSupLitt = reader.findColumn(DatabaseSUP.COLUMN_NOMSUPLITT);
        if (indexNomSupLitt < 0) {
            log.warn(MARKER, "Column NOMSUPLITT not found, using empty values (optional)");
        }

        PreparedStatement sth = database.getConnection().prepareStatement(
            "INSERT INTO servitude (idsup,nomsuplitt) VALUES (?,?)"
        );

        while (reader.hasNext()) {
            String[] row = reader.next();

            String idSup = row[indexIdSup];
            String nomSupLitt = indexNomSupLitt < 0 ? "" : row[indexNomSupLitt];

            sth.setString(1, idSup);
            sth.setString(2, nomSupLitt);

            sth.addBatch();
        }
        sth.executeBatch();
        database.getConnection().commit();
    }

    /**
     * Create SQL table TABLE_ACTE.
     */
    private void createTableActe(Database database) throws SQLException {
        List<String> columns = new ArrayList<>();
        columns.add(DatabaseSUP.COLUMN_IDACTE);
        columns.add(DatabaseSUP.COLUMN_FICHIER);
        database.createTable(DatabaseSUP.TABLE_ACTE, columns);
        database.createIndex(DatabaseSUP.TABLE_ACTE, DatabaseSUP.COLUMN_IDACTE);
    }

    /**
     * Load a CSV file into TABLE_ACTE.
     */
    public void loadTableActe(Database database, File acteSupFile) throws Exception {
        log.info(MARKER, "load table ACTE_SUP from {}...", acteSupFile);
        TableReader reader = TableReader.createTableReader(acteSupFile, StandardCharsets.UTF_8);

        int indexIdActe = reader.findColumnRequired(DatabaseSUP.COLUMN_IDACTE);
        int indexFichier = reader.findColumnRequired(DatabaseSUP.COLUMN_FICHIER);

        PreparedStatement sth = database.getConnection().prepareStatement(
            "INSERT INTO acte_sup (idacte,fichier) VALUES (?,?)"
        );

        while (reader.hasNext()) {
            String[] row = reader.next();

            String idActe = row[indexIdActe];
            String fichier = row[indexFichier];

            sth.setString(1, idActe);
            sth.setString(2, fichier);

            sth.addBatch();
        }
        sth.executeBatch();
        database.getConnection().commit();
    }

    /**
     * Create SQL table TABLE_SERVITUDE_ACTE.
     */
    private void createTableServitudeActe(Database database) throws SQLException {
        List<String> columns = new ArrayList<>();
        columns.add(DatabaseSUP.COLUMN_IDSUP);
        columns.add(DatabaseSUP.COLUMN_IDACTE);
        database.createTable(DatabaseSUP.TABLE_SERVITUDE_ACTE, columns);
        database.createIndex(DatabaseSUP.TABLE_SERVITUDE_ACTE, DatabaseSUP.COLUMN_IDSUP);
    }

    /**
     * Load a CSV file into TABLE_SERVITUDE_ACTE.
     */
    private void loadTableServitudeActe(Database database, File servitudeActeSupFile) throws Exception {
        log.info(MARKER, "load table SERVITUDE_ACTE_SUP from {}...", servitudeActeSupFile);
        TableReader reader = TableReader.createTableReader(servitudeActeSupFile, StandardCharsets.UTF_8);

        int indexIdSup = reader.findColumnRequired(DatabaseSUP.COLUMN_IDSUP);
        int indexIdActe = reader.findColumnRequired(DatabaseSUP.COLUMN_IDACTE);

        PreparedStatement sth = database.getConnection().prepareStatement(
            "INSERT INTO servitude_acte_sup (idsup,idacte) VALUES (?,?)"
        );

        while (reader.hasNext()) {
            String[] row = reader.next();

            String idSup = row[indexIdSup];
            String idActe = row[indexIdActe];

            sth.setString(1, idSup);
            sth.setString(2, idActe);

            sth.addBatch();
        }
        sth.executeBatch();
        database.getConnection().commit();
    }

    /**
     * Load a CSV file into TABLE_GENERATEUR.
     */
    private void loadTableGenerateur(Database database, File generateurSupFile) throws Exception {
        log.info(MARKER, "load table GENERATEUR from {}...", generateurSupFile);
        TableReader reader = TableReader.createTableReader(generateurSupFile, StandardCharsets.UTF_8);

        int indexIdGen = reader.findColumnRequired(DatabaseSUP.COLUMN_IDGEN);
        int indexIdSup = reader.findColumnRequired(DatabaseSUP.COLUMN_IDSUP);

        PreparedStatement sth = database.getConnection().prepareStatement(
            "INSERT INTO generateur (idgen,idsup) VALUES (?,?)"
        );

        while (reader.hasNext()) {
            String[] row = reader.next();

            String idGen = row[indexIdGen];
            String idSup = row[indexIdSup];

            sth.setString(1, idGen);
            sth.setString(2, idSup);

            sth.addBatch();
        }
        sth.executeBatch();
        database.getConnection().commit();
    }

    /**
     * Load a CSV file into TABLE_ASSIETTE.
     */
    public void loadTableAssiette(Database database, File assietteSupFile) throws Exception {
        log.info(MARKER, "load table GENERATEUR from {}...", assietteSupFile);
        TableReader reader = TableReader.createTableReader(assietteSupFile, StandardCharsets.UTF_8);

        int indexIdAss = reader.findColumnRequired(DatabaseSUP.COLUMN_IDASS);
        int indexIdGen = reader.findColumnRequired(DatabaseSUP.COLUMN_IDGEN);

        PreparedStatement sth = database.getConnection().prepareStatement(
            "INSERT INTO assiette (idass,idgen) VALUES (?,?)"
        );

        while (reader.hasNext()) {
            String[] row = reader.next();

            String idAss = row[indexIdAss];
            String idGen = row[indexIdGen];

            sth.setString(1, idAss);
            sth.setString(2, idGen);

            sth.addBatch();
        }
        sth.executeBatch();
        database.getConnection().commit();
    }

}
