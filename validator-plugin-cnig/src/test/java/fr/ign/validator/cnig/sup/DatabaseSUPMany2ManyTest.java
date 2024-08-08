package fr.ign.validator.cnig.sup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.cnig.sup.DatabaseSUP.AssietteSup;
import fr.ign.validator.tools.ResourceHelper;

/**
 * Performs the following tests on {@link DatabaseSUP} :
 *
 * <ul>
 * <li>Load CSV files from /jointure_sup/many2many/DATA checking row counts</li>
 * <li>Ensure that "dummy" is supported by find methods.</li>
 * <li>Ensure that MANY-TO-MANY relations are supported by find methods.</li>
 * </ul>
 *
 * @author MBorne
 *
 */
public class DatabaseSUPMany2ManyTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private DatabaseSUP db;

    @Before
    public void setUp() throws Exception {
        TestDatabaseFactory factory = new TestDatabaseFactory(folder.getRoot());
        File dataDirectory = ResourceHelper.getResourceFile(getClass(), "/jointure_sup/many2many/DATA");
        db = factory.createFromDataDirectory(dataDirectory);
    }

    @Test
    public void testLoad() throws Exception {
        assertEquals(159, db.getCount(DatabaseSUP.TABLE_SERVITUDE));
        assertEquals(172, db.getCount(DatabaseSUP.TABLE_ACTE));
        assertEquals(172, db.getCount(DatabaseSUP.TABLE_SERVITUDE_ACTE));
        assertEquals(229, db.getCount(DatabaseSUP.TABLE_GENERATEUR));
        assertEquals(392, db.getCount(DatabaseSUP.TABLE_ASSIETTE));
    }

    @Test
    public void testFindActesByGenerateurNotFound() throws Exception {
        List<DatabaseSUP.ActeServitude> actes = db.findActesByGenerateur("dummy");
        assertEquals(0, actes.size());
    }

    @Test
    public void testFindActesByAssietteNotFound() throws Exception {
        List<DatabaseSUP.ActeServitude> actes = db.findActesByAssiette("dummy");
        assertEquals(0, actes.size());
    }

    @Test
    public void testFindServitudesByGenerateurNotFound() throws Exception {
        List<DatabaseSUP.Servitude> servitudes = db.findServitudesByGenerateur("dummy");
        assertEquals(0, servitudes.size());
    }

    @Test
    public void testFindServitudesByAssietteNotFound() throws Exception {
        List<DatabaseSUP.Servitude> servitudes = db.findServitudesByAssiette("dummy");
        assertEquals(0, servitudes.size());
    }

    @Test
    public void testFindActesByGenerateur() throws Exception {
        // 10 - multiple acte
        {
            List<DatabaseSUP.ActeServitude> actes = db.findActesByGenerateur("10");
            assertEquals(2, actes.size());
            List<String> fichiers = db.getFichiers(actes);
            assertEquals(2, fichiers.size());
            assertTrue(fichiers.contains("37015_AS1_AZAY-SUR-CHER_Coteau_Duvellerie_F1_F2_act1.pdf"));
            assertTrue(fichiers.contains("37015_AS1_AZAY-SUR-CHER_Coteau_Duvellerie_F1_F2_act2.pdf"));
        }

        // 21 - single acte
        {
            List<DatabaseSUP.ActeServitude> actes = db.findActesByGenerateur("21");
            assertEquals(1, actes.size());
            List<String> fichiers = db.getFichiers(actes);
            assertEquals(1, fichiers.size());
            assertEquals("37030_AS1_LE BOULAY_Gare_du_Sentier_F_act.pdf", fichiers.get(0));
        }
    }

    @Test
    public void testFindActesByAssiette() throws Exception {
        // 362 - multiple acte
        {
            List<DatabaseSUP.ActeServitude> actes = db.findActesByAssiette("362");
            assertEquals(2, actes.size());
            List<String> fichiers = db.getFichiers(actes);
            assertEquals(2, fichiers.size());
            assertTrue(fichiers.contains("37273_AS1_LA VILLE-AUX-DAMES_Ile_Rochecorbon_F1_F3_F4_act1.pdf"));
            assertTrue(fichiers.contains("37273_AS1_LA VILLE-AUX-DAMES_Ile_Rochecorbon_F1_F3_F4_act2.pdf"));
        }
    }

    @Test
    public void testFindServitudesByGenerateur() throws Exception {
        // 10 - single servitude
        {
            List<DatabaseSUP.Servitude> servitudes = db.findServitudesByGenerateur("10");
            assertEquals(1, servitudes.size());
            List<String> nomSupLitts = db.getNomSupLitts(servitudes);
            assertEquals(1, nomSupLitts.size());
            assertEquals("AS1_AZAY-SUR-CHER_Coteau_Duvellerie_F1_F2_sup", nomSupLitts.get(0));
        }

        // 21 - single servitude
        {
            List<DatabaseSUP.Servitude> servitudes = db.findServitudesByGenerateur("21");
            assertEquals(1, servitudes.size());
            List<String> nomSupLitts = db.getNomSupLitts(servitudes);
            assertEquals(1, nomSupLitts.size());
            assertEquals("AS1_LE BOULAY_Gare_du_Sentier_F_sup", nomSupLitts.get(0));
        }
    }

    @Test
    public void testFindServitudesByAssiette() throws Exception {
        // 362 - single servitude
        {
            List<DatabaseSUP.Servitude> servitudes = db.findServitudesByAssiette("362");
            assertEquals(1, servitudes.size());
            List<String> nomSupLitts = db.getNomSupLitts(servitudes);
            assertEquals(1, nomSupLitts.size());
            assertEquals("AS1_LA VILLE-AUX-DAMES_Ile_Rochecorbon_F1_F3_F4_sup", nomSupLitts.get(0));
        }
    }

    @Test
    public void testFindDuplicatedValuesForIDASS() throws Exception {
        assertEquals(0, db.findDuplicatedValuesForIDASS().size());
    }

    @Test
    public void testFindDuplicatedValuesForIDGEN() throws Exception {
        assertEquals(0, db.findDuplicatedValuesForIDGEN().size());
    }

    @Test
    public void testFindAssiettesWithInvalidIDGEN() throws Exception {
        List<AssietteSup> assiettes = db.findAssiettesWithInvalidIDGEN(100);
        assertEquals(0, assiettes.size());
    }
}
