package fr.ign.validator.cnig.utils.internal;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import junit.framework.TestCase;

public class DatabaseJointureOne2OneTest extends TestCase {

	private File parentDirectory;

	private String actesFilename = "/jointure_sup_one2one/validation/jointure_sup_one2one/DATA/ACTE_SUP.csv";
	private File actesFile = new File(getClass().getResource(actesFilename).getPath());

	private String servitudesFilename = "/jointure_sup_one2one/validation/jointure_sup_one2one/DATA/SERVITUDE_ACTE_SUP.csv";
	private File servitudeFiles = new File(getClass().getResource(servitudesFilename).getPath());

	private String generateursFilename = "/jointure_sup_one2one/validation/jointure_sup_one2one/DATA/AC2_GENERATEUR_SUP_S.csv";
	File generateursFile = new File(getClass().getResource(generateursFilename).getPath());

	private String assiettesFilename = "/jointure_sup_one2one/validation/jointure_sup_one2one/DATA/AC2_ASSIETTE_SUP_S.csv";
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

	public void testInit() {
		try {
			DatabaseJointureSUP db = new DatabaseJointureSUP(parentDirectory);
			assertEquals(0, db.getCountActes());
		} catch (SQLException e) {
			fail(e.getMessage());
		}
	}

	public void testLoadActe() {
		try {
			DatabaseJointureSUP db = new DatabaseJointureSUP(parentDirectory);
			db.loadFileActe(actesFile);
			assertEquals(5, db.getCountActes());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	public void testLoadServitude() {
		try {
			DatabaseJointureSUP db = new DatabaseJointureSUP(parentDirectory);
			db.loadFileServitude(servitudeFiles);
			assertEquals(5, db.getCountServitude());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	public void testLoadGenerateur() {
		try {
			DatabaseJointureSUP db = new DatabaseJointureSUP(parentDirectory);
			db.loadFileGenerateur(generateursFile);
			assertEquals(6, db.getCountGenerateur());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	public void testLoadAssiette() {
		try {
			DatabaseJointureSUP db = new DatabaseJointureSUP(parentDirectory);
			db.loadFileAssiette(assiettesFile);
			assertEquals(6, db.getCountAssiette());
		} catch (Exception e) {
			fail(e.getMessage());
		}
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
