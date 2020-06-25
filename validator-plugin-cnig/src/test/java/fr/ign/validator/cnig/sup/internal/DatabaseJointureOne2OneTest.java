package fr.ign.validator.cnig.sup.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.cnig.process.internal.DatabaseJointureSUP;
import fr.ign.validator.tools.ResourceHelper;

public class DatabaseJointureOne2OneTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File actesFile = ResourceHelper.getResourceFile(
        DatabaseJointureOne2OneTest.class,
        "/jointure_sup/one2one/DATA/ACTE_SUP.csv"
    );

    private File servitudeFiles = ResourceHelper.getResourceFile(
        DatabaseJointureOne2OneTest.class,
        "/jointure_sup/one2one/DATA/SERVITUDE_ACTE_SUP.csv"
    );

    private File generateursFile = ResourceHelper.getResourceFile(
        DatabaseJointureOne2OneTest.class,
        "/jointure_sup/one2one/DATA/AC2_GENERATEUR_SUP_S.csv"
    );

    private File assiettesFile = ResourceHelper.getResourceFile(
        DatabaseJointureOne2OneTest.class,
        "/jointure_sup/one2one/DATA/AC2_ASSIETTE_SUP_S.csv"
    );

    @Test
    public void testInit() {
        try {
            DatabaseJointureSUP db = new DatabaseJointureSUP(folder.getRoot());
            assertEquals(0, db.getCountActes());
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testLoadActe() {
        try {
            DatabaseJointureSUP db = new DatabaseJointureSUP(folder.getRoot());
            db.loadFileActe(actesFile);
            assertEquals(5, db.getCountActes());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testLoadServitude() {
        try {
            DatabaseJointureSUP db = new DatabaseJointureSUP(folder.getRoot());
            db.loadFileServitude(servitudeFiles);
            assertEquals(5, db.getCountServitude());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testLoadGenerateur() {
        try {
            DatabaseJointureSUP db = new DatabaseJointureSUP(folder.getRoot());
            db.loadFileGenerateur(generateursFile);
            assertEquals(6, db.getCountGenerateur());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testLoadAssiette() {
        try {
            DatabaseJointureSUP db = new DatabaseJointureSUP(folder.getRoot());
            db.loadFileAssiette(assiettesFile);
            assertEquals(6, db.getCountAssiette());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testFindFichiersByGenerateur() {
        try {
            DatabaseJointureSUP db = new DatabaseJointureSUP(folder.getRoot());
            db.loadFileActe(actesFile);
            db.loadFileServitude(servitudeFiles);
            db.loadFileGenerateur(generateursFile);
            db.loadFileAssiette(assiettesFile);

            // dummy
            {
                List<String> fichiers = db.findFichiersByGenerateur("dummy");
                assertEquals(0, fichiers.size());
            }

            // 19820128
            {
                List<String> fichiers = db.findFichiersByGenerateur("19820128");
                assertEquals(1, fichiers.size());
                assertEquals("AC2_02_Abers_1982012", fichiers.get(0));
            }
            // 19240922
            {
                List<String> fichiers = db.findFichiersByGenerateur("19240922");
                assertEquals(1, fichiers.size());
                assertEquals("AC2_02_Anse_Saint_Laurent_19240922_act.pdf", fichiers.get(0));
            }
            // 19771122
            {
                List<String> fichiers = db.findFichiersByGenerateur("19771122");
                assertEquals(1, fichiers.size());
                assertEquals("AC2_02_Archipel_Molene_19771122_act.pdf", fichiers.get(0));
            }
            // 198904122
            {
                List<String> fichiers = db.findFichiersByGenerateur("198904122");
                assertEquals(1, fichiers.size());
                assertEquals("AC2_02_Baie_Audierne_19890412_act.pdf", fichiers.get(0));
            }
            // 198904121
            {
                List<String> fichiers = db.findFichiersByGenerateur("198904121");
                assertEquals(1, fichiers.size());
                assertEquals("AC2_02_Baie_Audierne_19890412_act.pdf", fichiers.get(0));
            }
            // 19230309
            {
                List<String> fichiers = db.findFichiersByGenerateur("19230309");
                assertEquals(1, fichiers.size());
                assertEquals("AC2_Benodet_Parcelle7_19230309_act.pdf", fichiers.get(0));
            }

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testFindFichiersByAssiette() {
        try {
            DatabaseJointureSUP db = new DatabaseJointureSUP(folder.getRoot());
            db.loadFileActe(actesFile);
            db.loadFileServitude(servitudeFiles);
            db.loadFileGenerateur(generateursFile);
            db.loadFileAssiette(assiettesFile);

            // dummy
            {
                List<String> fichiers = db.findFichiersByAssiette("dummy");
                assertEquals(0, fichiers.size());
            }

            // 19820128
            {
                List<String> fichiers = db.findFichiersByAssiette("19820128");
                assertEquals(1, fichiers.size());
                assertEquals("AC2_02_Abers_1982012", fichiers.get(0));
            }
            // 19240922
            {
                List<String> fichiers = db.findFichiersByAssiette("19240922");
                assertEquals(1, fichiers.size());
                assertEquals("AC2_02_Anse_Saint_Laurent_19240922_act.pdf", fichiers.get(0));
            }
            // 19771122
            {
                List<String> fichiers = db.findFichiersByAssiette("19771122");
                assertEquals(1, fichiers.size());
                assertEquals("AC2_02_Archipel_Molene_19771122_act.pdf", fichiers.get(0));
            }
            // 198904122
            {
                List<String> fichiers = db.findFichiersByAssiette("198904122");
                assertEquals(1, fichiers.size());
                assertEquals("AC2_02_Baie_Audierne_19890412_act.pdf", fichiers.get(0));
            }
            // 198904121
            {
                List<String> fichiers = db.findFichiersByAssiette("198904121");
                assertEquals(1, fichiers.size());
                assertEquals("AC2_02_Baie_Audierne_19890412_act.pdf", fichiers.get(0));
            }
            // 19230309
            {
                List<String> fichiers = db.findFichiersByAssiette("19230309");
                assertEquals(1, fichiers.size());
                assertEquals("AC2_Benodet_Parcelle7_19230309_act.pdf", fichiers.get(0));
            }

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

}
