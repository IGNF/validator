package fr.ign.validator.cnig.sup.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.cnig.process.internal.DatabaseJointureSUP;
import fr.ign.validator.tools.ResourceHelper;


public class DatabaseJointureMany2ManyTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private File actesFile = ResourceHelper.getResourceFile(
		DatabaseJointureOne2OneTest.class, 
		"/jointure_sup/many2many/DATA/ACTE_SUP.csv"
	);

	private File servitudeFiles = ResourceHelper.getResourceFile(
		DatabaseJointureOne2OneTest.class, 
		"/jointure_sup/many2many/DATA/SERVITUDE_ACTE_SUP.csv"
	);

	private File generateursFile = ResourceHelper.getResourceFile(
		DatabaseJointureOne2OneTest.class, 
		"/jointure_sup/many2many/DATA/AS1_GENERATEUR_SUP_P.csv"
	);

	private File assiettesFile = ResourceHelper.getResourceFile(
		DatabaseJointureOne2OneTest.class, 
		"/jointure_sup/many2many/DATA/AS1_ASSIETTE_SUP_S.csv"
	);

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

			// 10
			{
				List<String> fichiers = db.findFichiersByGenerateur("10");
				assertEquals(2, fichiers.size());
				assertTrue(fichiers.contains("37015_AS1_AZAY-SUR-CHER_Coteau_Duvellerie_F1_F2_act1.pdf"));
				assertTrue(fichiers.contains("37015_AS1_AZAY-SUR-CHER_Coteau_Duvellerie_F1_F2_act2.pdf"));
			}

			// 21
			{
				List<String> fichiers = db.findFichiersByGenerateur("21");
				assertEquals(1, fichiers.size());
				assertEquals("37030_AS1_LE BOULAY_Gare_du_Sentier_F_act.pdf", fichiers.get(0));
			}

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testFindFichiersByAssiette() throws Exception {
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

		// 65
		{
			List<String> fichiers = db.findFichiersByAssiette("362");
			assertEquals(2, fichiers.size());
			assertTrue(fichiers.contains("37273_AS1_LA VILLE-AUX-DAMES_Ile_Rochecorbon_F1_F3_F4_act1.pdf"));
			assertTrue(fichiers.contains("37273_AS1_LA VILLE-AUX-DAMES_Ile_Rochecorbon_F1_F3_F4_act2.pdf"));
		}
	}

}
