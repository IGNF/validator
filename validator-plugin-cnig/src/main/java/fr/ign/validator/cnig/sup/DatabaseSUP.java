package fr.ign.validator.cnig.sup;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import fr.ign.validator.Context;
import fr.ign.validator.database.Database;
import fr.ign.validator.database.internal.DuplicatedValuesFinder;
import fr.ign.validator.database.internal.DuplicatedValuesFinder.DuplicatedValue;
import fr.ign.validator.model.FileModel;

/**
 *
 * Helper class to manipulate relations between ACTE, SERVITUDE, GENERATEUR and
 * ASSIETTE files.
 *
 * @warning it relies on merged GENERATEUR and ASSIETTE tables.
 *
 * @see http://cnig.gouv.fr/wp-content/uploads/2014/09/20140930_STANDARD_SUP_V2013.pdf#page=20&zoom=auto,-260,773
 * @see https://github.com/IGNF/validator/issues/176 (except TABLE_GENERATEUR
 *      and TABLE_ASSIETTE, tables are already loaded in validation database)
 *
 * @author MBorne
 *
 */
public class DatabaseSUP {
    public static final Logger log = LogManager.getRootLogger();
    public static final Marker MARKER = MarkerManager.getMarker("DatabaseSUP");

    /**
     * SERVITUDE / Servitude in CNIG standard ("idsup","nomsuplitt")
     */
    public static final String TABLE_SERVITUDE = "servitude";

    /**
     * ACTE_SUP / ActeServitude in CNIG standard ("idacte","fichier")
     */
    public static final String TABLE_ACTE = "acte_sup";

    /**
     * SERVITUDE_ACTE_SUP in CNIG standard ("idsup","idacte")
     */
    public static final String TABLE_SERVITUDE_ACTE = "servitude_acte_sup";

    /**
     * GENERATEUR_SUP_(P/L/S) / GenerateurSup in CNIG standard ("idgen","idsup")
     */
    public static final String TABLE_GENERATEUR = "generateur";
    /**
     * Regex to find and merge GENERATEUR_SUP tables.
     */
    public static final String REGEX_TABLE_GENERATEUR = "(?i).*_GENERATEUR_SUP_.*";

    /**
     * ASSIETTE_SUP_(P/L/S) / AssietteSup in CNIG standard ("idass","idgen")
     */
    public static final String TABLE_ASSIETTE = "assiette";

    /**
     * Regex to find and merge ASSIETTE_SUP tables.
     */
    public static final String REGEX_TABLE_ASSIETTE = "(?i).*_ASSIETTE_SUP_.*";

    public static final String COLUMN_IDSUP = "idsup";
    public static final String COLUMN_NOMSUPLITT = "nomsuplitt";

    public static final String COLUMN_IDACTE = "idacte";
    public static final String COLUMN_FICHIER = "fichier";

    public static final String COLUMN_IDGEN = "idgen";

    public static final String COLUMN_IDASS = "idass";

    /**
     * Lightweight model for ActeServitude
     */
    public class ActeServitude {
        public String idacte;
        public String fichier;
    }

    /**
     * Lightweight model for Servitude
     */
    public class Servitude {
        public String idsup;
        public String nomsuplitt;
    }

    /**
     * Lightweight model for AssietteSup
     */
    public class AssietteSup {
        public String idass;
        public String idgen;
    }

    /**
     * database connection
     */
    private Database database;

    /**
     * Create DatabaseSUP as an SQLite database.
     *
     * @param tempDirectory
     * @throws SQLException
     */
    public DatabaseSUP(Database database) throws SQLException {
        this.database = database;
    }

    /**
     * Create DatabaseSUP merging GENERATEUR and ASSIETTE tables in the validation
     * database.
     *
     * @param validationDatabase
     * @return
     */
    public static DatabaseSUP createFromValidationDatabase(Context context) {
        try {
            /*
             * open previously created validation database
             */
            Database validationDatabase = Database.createDatabase(context, false);
            DatabaseSUP database = new DatabaseSUP(validationDatabase);
            /* create merged tables */
            database.createTableGenerateur();
            database.createTableAssiete();
            /* fill merged tables */
            for (FileModel fileModel : context.getDocumentModel().getFileModels()) {
                String tableName = fileModel.getName();
                if (tableName.matches(DatabaseSUP.REGEX_TABLE_ASSIETTE)) {
                    database.loadAssiettesFromTable(tableName);
                } else if (tableName.matches(DatabaseSUP.REGEX_TABLE_GENERATEUR)) {
                    database.loadGenerateursFromTable(tableName);
                }
            }
            return database;
        } catch (SQLException e) {
            log.error(MARKER, "Fail to create DatabaseSUP from ValidationDatabase", e);
            return null;
        }
    }

    /**
     * Get database connection
     *
     * @return
     */
    private Connection getConnection() {
        return database.getConnection();
    }

    /**
     * Create SQL table "generateur".
     *
     * @throws SQLException
     *
     * @see {@link #loadTableGenerateur(File)}
     */
    void createTableGenerateur() throws SQLException {
        List<String> columns = new ArrayList<>();
        columns.add(COLUMN_IDGEN);
        columns.add(COLUMN_IDSUP);
        database.createTable(TABLE_GENERATEUR, columns);
        database.createIndex(TABLE_GENERATEUR, COLUMN_IDGEN);
    }

    /**
     * Create SQL table "assiette".
     *
     * @throws SQLException
     *
     * @see {@link #loadTableAssiette(File)}
     */
    void createTableAssiete() throws SQLException {
        List<String> columns = new ArrayList<>();
        columns.add(COLUMN_IDASS);
        columns.add(COLUMN_IDGEN);
        database.createTable(TABLE_ASSIETTE, columns);
        database.createIndex(TABLE_ASSIETTE, COLUMN_IDASS);
    }

    /**
     * Insert rows from tableName into TABLE_GENERATEUR.
     *
     * @param tableName
     * @throws SQLException
     */
    void loadGenerateursFromTable(String tableName) throws SQLException {
        log.info(MARKER, "Merge {} into {} ...", tableName, TABLE_GENERATEUR);
        String sql = "INSERT INTO " + TABLE_GENERATEUR + "(" + COLUMN_IDGEN + "," + COLUMN_IDSUP + ") ";
        sql += " SELECT " + COLUMN_IDGEN + "," + COLUMN_IDSUP + " FROM " + tableName;
        database.update(sql);
        getConnection().commit();
    }

    /**
     * Insert rows from tableName into TABLE_ASSIETTE.
     *
     * @param tableName
     * @throws SQLException
     */
    void loadAssiettesFromTable(String tableName) throws SQLException {
        log.info(MARKER, "Merge {} into {} ...", tableName, TABLE_ASSIETTE);
        String sql = "INSERT INTO " + TABLE_ASSIETTE + "(" + COLUMN_IDASS + "," + COLUMN_IDGEN + ") ";
        sql += " SELECT " + COLUMN_IDASS + "," + COLUMN_IDGEN + " FROM " + tableName;
        database.update(sql);
        getConnection().commit();
    }

    /**
     * Return the number of rows in a given table.
     *
     * @return
     * @throws SQLException
     */
    public int getCount(String tableName) throws SQLException {
        return database.getCount(tableName);
    }

    /**
     * Find actes for a given idGen
     *
     * @param idGen
     * @return
     */
    public List<ActeServitude> findActesByGenerateur(String idGen) {
        String sql = "SELECT DISTINCT a.idacte,a.fichier FROM acte_sup a "
            + " LEFT JOIN servitude_acte_sup sa ON a.idacte = sa.idacte "
            + " LEFT JOIN generateur ON generateur.idsup = sa.idsup "
            + " WHERE generateur.idgen = ?";
        try {
            PreparedStatement sth = getConnection().prepareStatement(sql);
            sth.setString(1, idGen);
            return fetchActes(sth.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Find actes for a given idAss.
     *
     * @param idAss
     * @return
     */
    public List<ActeServitude> findActesByAssiette(String idAss) {
        String sql = "SELECT DISTINCT a.idacte,a.fichier FROM acte_sup a "
            + " LEFT JOIN servitude_acte_sup sa ON a.idacte = sa.idacte "
            + " LEFT JOIN generateur ON generateur.idsup = sa.idsup "
            + " LEFT JOIN assiette ON assiette.idgen = generateur.idgen "
            + " WHERE assiette.idass = ?";
        try {
            PreparedStatement sth = getConnection().prepareStatement(sql);
            sth.setString(1, idAss);
            return fetchActes(sth.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Fetch unique Acte from ResultSet.
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    private List<ActeServitude> fetchActes(ResultSet rs) throws SQLException {
        List<ActeServitude> result = new ArrayList<ActeServitude>();
        while (rs.next()) {
            ActeServitude acte = new ActeServitude();
            acte.idacte = rs.getString(COLUMN_IDACTE);
            acte.fichier = rs.getString(COLUMN_FICHIER);
            result.add(acte);
        }
        return result;
    }

    /**
     * Helper to extract "fichier" values
     *
     * @param actes
     * @return
     */
    public List<String> getFichiers(List<ActeServitude> actes) {
        HashSet<String> result = new HashSet<>(actes.size());
        for (ActeServitude acte : actes) {
            if (StringUtils.isEmpty(acte.fichier)) {
                continue;
            }
            result.add(acte.fichier);
        }
        return new ArrayList<>(result);
    }

    /**
     * Find servitudes for a given idGen
     *
     * @param idGen
     * @return
     */
    public List<Servitude> findServitudesByGenerateur(String idGen) {
        String sql = "SELECT DISTINCT s.idsup,s.nomsuplitt FROM generateur g "
            + " LEFT JOIN servitude s ON s.idsup = g.idsup "
            + " WHERE g.idgen = ?";
        try {
            PreparedStatement sth = getConnection().prepareStatement(sql);
            sth.setString(1, idGen);
            return fetchServitudes(sth.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Find servitudes for a given idAss.
     *
     * @param idAss
     * @return
     */
    public List<Servitude> findServitudesByAssiette(String idAss) {
        String sql = "SELECT DISTINCT s.idsup,s.nomsuplitt FROM assiette a "
            + " LEFT JOIN generateur g ON a.idgen = g.idgen "
            + " LEFT JOIN servitude s ON s.idsup = g.idsup "
            + " WHERE a.idass = ?";
        try {
            PreparedStatement sth = getConnection().prepareStatement(sql);
            sth.setString(1, idAss);
            return fetchServitudes(sth.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Fetch unique Acte from ResultSet.
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    private List<Servitude> fetchServitudes(ResultSet rs) throws SQLException {
        List<Servitude> result = new ArrayList<Servitude>();
        while (rs.next()) {
            Servitude servitude = new Servitude();
            servitude.idsup = rs.getString(COLUMN_IDSUP);
            servitude.nomsuplitt = rs.getString(COLUMN_NOMSUPLITT);
            result.add(servitude);
        }
        return result;
    }

    /**
     * Helper to extract "nomSupLitt" values
     *
     * @param actes
     * @return
     */
    public List<String> getNomSupLitts(List<Servitude> servitudes) {
        HashSet<String> result = new HashSet<>(servitudes.size());
        for (Servitude servitude : servitudes) {
            if (StringUtils.isEmpty(servitude.nomsuplitt)) {
                continue;
            }
            result.add(servitude.nomsuplitt);
        }
        return new ArrayList<>(result);
    }

    /**
     * Validation - Find non unique IDGEN values.
     *
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public List<DuplicatedValue> findDuplicatedValuesForIDGEN() throws SQLException, IOException {
        DuplicatedValuesFinder duplicatedValuesFinder = new DuplicatedValuesFinder();
        return duplicatedValuesFinder.findDuplicatedValues(database, TABLE_GENERATEUR, COLUMN_IDGEN);
    }

    /**
     * Validation - Find non unique IDASS values.
     *
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public List<DuplicatedValue> findDuplicatedValuesForIDASS() throws SQLException, IOException {
        DuplicatedValuesFinder duplicatedValuesFinder = new DuplicatedValuesFinder();
        return duplicatedValuesFinder.findDuplicatedValues(database, TABLE_ASSIETTE, COLUMN_IDASS);
    }

    /**
     * Validation - find AssietteSup with invalid IDGEN
     *
     * @param limit maximum number of results.
     * @return
     * @throws SQLException
     * @throws IOException
     */
    public List<AssietteSup> findAssiettesWithInvalidIDGEN(int limit) throws SQLException, IOException {
        String sql = "SELECT a.idass,a.idgen FROM assiette a ";
        sql += " WHERE NOT EXISTS (SELECT * FROM generateur g WHERE g.idgen = a.idgen ) LIMIT ?";
        try {
            PreparedStatement sth = getConnection().prepareStatement(sql);
            sth.setInt(1, limit);
            return fetchAssietteSup(sth.executeQuery());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Fetch unique AssietteSup from ResultSet.
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    private List<AssietteSup> fetchAssietteSup(ResultSet rs) throws SQLException {
        List<AssietteSup> result = new ArrayList<AssietteSup>();
        while (rs.next()) {
            AssietteSup assiette = new AssietteSup();
            assiette.idass = rs.getString(COLUMN_IDASS);
            assiette.idgen = rs.getString(COLUMN_IDGEN);
            result.add(assiette);
        }
        return result;
    }

}
