package fr.ign.validator.cnig.sup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.cnig.sup.DatabaseSUP.ActeServitude;
import fr.ign.validator.cnig.sup.DatabaseSUP.Servitude;
import fr.ign.validator.tools.ResourceHelper;

/**
 * Performs the following tests on {@link DatabaseSUP} :
 * 
 * <ul>
 * <li>Load CSV files from /jointure_sup/duplicated/DATA checking row
 * counts</li>
 * <li>Ensure that the process doesn't crash and the finder produces unique
 * values if multiple SERVITUDE rows have the same IDSUP (2147483647).</li>
 * </ul>
 * 
 * @author MBorne
 *
 */
public class DatabaseSUPDuplicatedTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private DatabaseSUP db;

    @Before
    public void setUp() throws Exception {
        DatabaseSUPFactory factory = new DatabaseSUPFactory(folder.getRoot());
        File dataDirectory = ResourceHelper.getResourceFile(getClass(), "/jointure_sup/duplicated/DATA");
        db = factory.createFromDataDirectory(dataDirectory);
    }

    @Test
    public void testLoad() throws Exception {
        assertEquals(7, db.getCount(DatabaseSUP.TABLE_SERVITUDE));
        assertEquals(7, db.getCount(DatabaseSUP.TABLE_ACTE));
        assertEquals(7, db.getCount(DatabaseSUP.TABLE_SERVITUDE_ACTE));
        assertEquals(7, db.getCount(DatabaseSUP.TABLE_GENERATEUR));
        assertEquals(7, db.getCount(DatabaseSUP.TABLE_ASSIETTE));
    }

    @Test
    public void testFindActesByGenerateur() throws Exception {
        // 1
        {
            List<DatabaseSUP.ActeServitude> actes = db.findActesByGenerateur("1");
            assertEquals(7, actes.size());
            // should not happen, IDACTE should be unique in ActeServitude
            for (ActeServitude acte : actes) {
                assertEquals("2147483647", acte.idacte);
            }

            /* check "fichier" */
            List<String> fichiers = db.getFichiers(actes);
            assertEquals(7, fichiers.size());
            assertTrue(fichiers.contains("AC2_LA_RAVINE_SAINT_GILLES_arrete_19800226_act.pdf"));
            assertTrue(fichiers.contains("AC2_LA_RIVIERE_DES_ROCHES_arrete_19851122_act.pdf"));
        }
    }

    @Test
    public void testFindActesByAssiette() throws Exception {
        // 1
        {
            List<DatabaseSUP.ActeServitude> actes = db.findActesByAssiette("1");
            assertEquals(7, actes.size());
            // should not happen, IDACTE should be unique in ActeServitude
            for (ActeServitude acte : actes) {
                assertEquals("2147483647", acte.idacte);
            }

            /* check "fichier" */
            List<String> fichiers = db.getFichiers(actes);
            assertEquals(7, fichiers.size());
            assertTrue(fichiers.contains("AC2_LA_RAVINE_SAINT_GILLES_arrete_19800226_act.pdf"));
            assertTrue(fichiers.contains("AC2_LA_RIVIERE_DES_ROCHES_arrete_19851122_act.pdf"));
        }
    }

    @Test
    public void testFindServitudesByGenerateur() throws Exception {
        // 19820128
        {
            List<DatabaseSUP.Servitude> servitudes = db.findServitudesByGenerateur("1");
            assertEquals(2, servitudes.size());
            /*
             * Should not happen, IDACTE should be unique in Servitude (unique pairs
             * returned for "2147483647,A")
             */
            for (Servitude servitude : servitudes) {
                assertEquals("2147483647", servitude.idsup);
            }
            List<String> nomSupLitts = db.getNomSupLitts(servitudes);
            assertEquals(2, nomSupLitts.size());
            assertEquals("A", nomSupLitts.get(0));
            assertEquals("B", nomSupLitts.get(1));
        }
    }

    @Test
    public void testFindServitudesByAssiette() throws Exception {
        // 19820128
        {
            List<DatabaseSUP.Servitude> servitudes = db.findServitudesByAssiette("1");
            assertEquals(2, servitudes.size());
            for (Servitude servitude : servitudes) {
                assertEquals("2147483647", servitude.idsup);
            }
            List<String> nomSupLitts = db.getNomSupLitts(servitudes);
            assertEquals(2, nomSupLitts.size());
            assertEquals("A", nomSupLitts.get(0));
            assertEquals("B", nomSupLitts.get(1));
        }
    }

}
