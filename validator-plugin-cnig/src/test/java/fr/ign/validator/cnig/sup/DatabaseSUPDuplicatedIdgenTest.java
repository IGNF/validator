package fr.ign.validator.cnig.sup;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.cnig.sup.DatabaseSUP.AssietteSup;
import fr.ign.validator.database.internal.DuplicatedValuesFinder.DuplicatedValue;
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
public class DatabaseSUPDuplicatedIdgenTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private DatabaseSUP db;

    @Before
    public void setUp() throws Exception {
        TestDatabaseFactory factory = new TestDatabaseFactory(folder.getRoot());
        File dataDirectory = ResourceHelper.getResourceFile(getClass(), "/jointure_sup/idgen_not_unique/DATA");
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
    public void testFindDuplicatedValuesForIDASS() throws Exception {
        assertEquals(0, db.findDuplicatedValuesForIDASS().size());
    }

    @Test
    public void testFindDuplicatedValuesForIDGEN() throws Exception {
        List<DuplicatedValue> duplicatedValues = db.findDuplicatedValuesForIDGEN();
        assertEquals(2, duplicatedValues.size());

        assertEquals("222", duplicatedValues.get(0).value);
        assertEquals(3, duplicatedValues.get(0).count);

        assertEquals("111", duplicatedValues.get(1).value);
        assertEquals(2, duplicatedValues.get(1).count);
    }

    @Test
    public void testFindAssiettesWithInvalidIDGEN() throws Exception {
        List<AssietteSup> assiettes = db.findAssiettesWithInvalidIDGEN(100);
        assertEquals(0, assiettes.size());
    }
}
