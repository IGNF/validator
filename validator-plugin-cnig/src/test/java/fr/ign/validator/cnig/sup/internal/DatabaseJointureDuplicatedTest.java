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


public class DatabaseJointureDuplicatedTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private File actesFile = ResourceHelper.getResourceFile(
		DatabaseJointureOne2OneTest.class, 
		"/jointure_sup/duplicated/DATA/ACTE_SUP.csv"
	);

	private File servitudeFiles = ResourceHelper.getResourceFile(
		DatabaseJointureOne2OneTest.class, 
		"/jointure_sup/duplicated/DATA/SERVITUDE_ACTE_SUP.csv"
	);

	private File generateursFile = ResourceHelper.getResourceFile(
		DatabaseJointureOne2OneTest.class, 
		"/jointure_sup/duplicated/DATA/AC2_GENERATEUR_SUP_S.csv"
	);

	private File assiettesFile = ResourceHelper.getResourceFile(
		DatabaseJointureOne2OneTest.class, 
		"/jointure_sup/duplicated/DATA/AC2_ASSIETTE_SUP_S.csv"
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

			// 1
			{
				List<String> fichiers = db.findFichiersByGenerateur("1");
				assertEquals(7, fichiers.size());
				assertTrue(fichiers.contains("AC2_LA_RAVINE_SAINT_GILLES_arrete_19800226_act.pdf"));
				assertTrue(fichiers.contains("AC2_LA_RIVIERE_DES_ROCHES_arrete_19851122_act.pdf"));
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

		// 1
		{
			List<String> fichiers = db.findFichiersByAssiette("1");
			System.out.println(fichiers);
			assertEquals(7, fichiers.size());
			assertTrue(fichiers.contains("AC2_LA_RAVINE_SAINT_GILLES_arrete_19800226_act.pdf"));
			assertTrue(fichiers.contains("AC2_LA_RIVIERE_DES_ROCHES_arrete_19851122_act.pdf"));
		}
	}

}
