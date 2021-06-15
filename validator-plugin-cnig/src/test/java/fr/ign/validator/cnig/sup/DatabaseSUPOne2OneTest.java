package fr.ign.validator.cnig.sup;

import static org.junit.Assert.assertEquals;

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
 * <li>Load CSV files from /jointure_sup/one2one/DATA checking row counts</li>
 * <li>Ensure that ONE-TO-ONE relations are supported by find methods.</li>
 * </ul>
 * 
 * @author MBorne
 *
 */
public class DatabaseSUPOne2OneTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private DatabaseSUP db;

    @Before
    public void setUp() throws Exception {
        DatabaseSUPFactory factory = new DatabaseSUPFactory(folder.getRoot());
        File dataDirectory = ResourceHelper.getResourceFile(getClass(), "/jointure_sup/one2one/DATA");
        db = factory.createFromDataDirectory(dataDirectory);
    }

    @Test
    public void testLoad() throws Exception {
        assertEquals(5, db.getCount(DatabaseSUP.TABLE_SERVITUDE));
        assertEquals(5, db.getCount(DatabaseSUP.TABLE_ACTE));
        assertEquals(5, db.getCount(DatabaseSUP.TABLE_SERVITUDE_ACTE));
        assertEquals(6, db.getCount(DatabaseSUP.TABLE_GENERATEUR));
        assertEquals(6, db.getCount(DatabaseSUP.TABLE_ASSIETTE));
    }

    @Test
    public void testFindActesByGenerateur() throws Exception {
        // 19820128
        {
            List<DatabaseSUP.ActeServitude> actes = db.findActesByGenerateur("19820128");
            assertEquals(1, actes.size());
            List<String> fichiers = db.getFichiers(actes);
            assertEquals(1, fichiers.size());
            assertEquals("AC2_02_Abers_1982012", fichiers.get(0));
        }
        // 19240922
        {
            List<DatabaseSUP.ActeServitude> actes = db.findActesByGenerateur("19240922");
            assertEquals(1, actes.size());
            List<String> fichiers = db.getFichiers(actes);
            assertEquals(1, fichiers.size());
            assertEquals("AC2_02_Anse_Saint_Laurent_19240922_act.pdf", fichiers.get(0));
        }
    }

    @Test
    public void testFindActesByAssiette() throws Exception {
        // 19820128
        {
            List<DatabaseSUP.ActeServitude> actes = db.findActesByAssiette("19820128");
            assertEquals(1, actes.size());
            List<String> fichiers = db.getFichiers(actes);
            assertEquals(1, fichiers.size());
            assertEquals("AC2_02_Abers_1982012", fichiers.get(0));
        }
        // 19240922
        {
            List<DatabaseSUP.ActeServitude> actes = db.findActesByAssiette("19240922");
            assertEquals(1, actes.size());
            List<String> fichiers = db.getFichiers(actes);
            assertEquals(1, fichiers.size());
            assertEquals("AC2_02_Anse_Saint_Laurent_19240922_act.pdf", fichiers.get(0));
        }
    }

    @Test
    public void testFindServitudesByGenerateur() throws Exception {
        // 19820128
        {
            List<DatabaseSUP.Servitude> servitudes = db.findServitudesByGenerateur("19820128");
            assertEquals(1, servitudes.size());
            List<String> nomSupLitts = db.getNomSupLitts(servitudes);
            assertEquals(1, nomSupLitts.size());
            assertEquals("site des Abers", nomSupLitts.get(0));
        }
    }

    @Test
    public void testFindServitudesByAssiette() throws Exception {
        // 19820128
        {
            List<DatabaseSUP.Servitude> servitudes = db.findServitudesByAssiette("19820128");
            assertEquals(1, servitudes.size());
            List<String> nomSupLitts = db.getNomSupLitts(servitudes);
            assertEquals(1, nomSupLitts.size());
            assertEquals("site des Abers", nomSupLitts.get(0));
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
