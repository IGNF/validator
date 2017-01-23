package fr.ign.validator.cnig.utils.internal;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

public class DatabaseJointureMany2ManyTest extends TestCase {

	private File parentDirectory;

	private String actesFilename = "/jointure_sup_many2many/validation/jointure_sup_many2many/DATA/ACTE_SUP.csv";
	private File actesFile = new File(getClass().getResource(actesFilename).getPath());

	private String servitudesFilename = "/jointure_sup_many2many/validation/jointure_sup_many2many/DATA/SERVITUDE_ACTE_SUP.csv";
	private File servitudeFiles = new File(getClass().getResource(servitudesFilename).getPath());

	private String generateursFilename = "/jointure_sup_many2many/validation/jointure_sup_many2many/DATA/AS1_GENERATEUR_SUP_P.csv";
	File generateursFile = new File(getClass().getResource(generateursFilename).getPath());

	private String assiettesFilename = "/jointure_sup_many2many/validation/jointure_sup_many2many/DATA/AS1_ASSIETTE_SUP_S.csv";
	File assiettesFile = new File(getClass().getResource(assiettesFilename).getPath());

	@Override
	protected void setUp() throws Exception {
		parentDirectory = File.createTempFile("temp", Long.toString(System.nanoTime()));
		parentDirectory.delete();
		parentDirectory.mkdir();
	}

	@Override
	protected void tearDown() throws Exception {
		parentDirectory.delete();
	}

	public void testFindFichiersByGenerateur() {
		try {
			DatabaseJointureSUP db = new DatabaseJointureSUP(parentDirectory);
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

	public void testFindFichiersByAssiette() {
		try {
			DatabaseJointureSUP db = new DatabaseJointureSUP(parentDirectory);
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

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
