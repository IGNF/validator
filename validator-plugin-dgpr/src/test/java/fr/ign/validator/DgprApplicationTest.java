package fr.ign.validator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.data.Document;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.io.JsonModelReader;
import fr.ign.validator.io.ModelReader;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.plugin.PluginManager;
import fr.ign.validator.report.InMemoryReportBuilder;

/**
 *
 */
public class DgprApplicationTest {

    public static final Logger log = LogManager.getRootLogger();

    protected InMemoryReportBuilder report;

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setUp() {
        report = new InMemoryReportBuilder();
    }

    private Context createContext(File documentPath) throws Exception {
        Context context = new Context();
        context.setReportBuilder(report);
        context.setProjection("EPSG:2154");
        context.setDgprTolerance(1.0);
        context.setDgprSimplification(null);
        File validationDirectory = new File(documentPath.getParentFile(), "validation");
        context.setValidationDirectory(validationDirectory);
        PluginManager pluginManager = new PluginManager();
        pluginManager.getPluginByName("DGPR").setup(context);
        return context;
    }

    private DocumentModel getDocumentModel(String documentModelName) throws Exception {
        File documentModelPath = new File(
            getClass().getResource("/config/" + documentModelName + "/files.json").getPath()
        );
        ModelReader loader = new JsonModelReader();
        DocumentModel documentModel = loader.loadDocumentModel(documentModelPath);
        documentModel.setName(documentModelName);
        return documentModel;
    }

    private File getSampleDocument(String documentName) throws IOException {
        URL resource = getClass().getResource("/documents/" + documentName);
        Assert.assertNotNull(resource);
        File sourcePath = new File(resource.getPath());

        File documentPath = folder.newFolder(documentName);
        FileUtils.copyDirectory(sourcePath, documentPath);
        return documentPath;
    }

    /**
     * @throws Exception
     */
    @Test
    public void testDocumentOkTolerance5() throws Exception {
        DocumentModel documentModel = getDocumentModel("covadis_di_2018");
        File documentPath = getSampleDocument("TRI_JTEST_TOPO_SIG_DI");

        Context context = createContext(documentPath);
        context.setDgprTolerance(5.0);
        context.setDgprSimplification(null);
        Document document = new Document(documentModel, documentPath);
        document.validate(context);
        Assert.assertEquals("TRI_JTEST_TOPO_SIG_DI", document.getDocumentName());
        Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_DOCUMENT_PREFIX_ERROR).size());
        Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_FILENAME_PREFIX_ERROR).size());

        // controls performed with PostGIS : see testDocumentOkTolerance5Postgis
    }

    /**
     * @throws Exception
     */
    @Test
    public void testDocumentNotOkTolerance10safe() throws Exception {
        DocumentModel documentModel = getDocumentModel("covadis_di_2018");
        File documentPath = getSampleDocument("TRI_JTEST_TOPO_error_SIG_DI");

        Context context = createContext(documentPath);
        context.setDgprTolerance(10.0);
        context.setDgprSimplification(5.0);
        Document document = new Document(documentModel, documentPath);
        document.validate(context);
        Assert.assertEquals("TRI_JTEST_TOPO_error_SIG_DI", document.getDocumentName());
        Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_DOCUMENT_PREFIX_ERROR).size());
        Assert.assertEquals(29, report.getErrorsByCode(DgprErrorCodes.DGPR_FILENAME_PREFIX_ERROR).size());

        // controls performed with PostGIS : see testDocumentNotOkTolerance10Postgis
    }

    /**
     * @throws Exception
     */
    @Test
    public void testDocumentNotOk() throws Exception {
        DocumentModel documentModel = getDocumentModel("covadis_di_2018");
        File documentPath = getSampleDocument("TRI_JTEST_TOPO_error_SIG_DI");

        Context context = createContext(documentPath);
        Document document = new Document(documentModel, documentPath);

        document.validate(context);
        Assert.assertEquals("TRI_JTEST_TOPO_error_SIG_DI", document.getDocumentName());
        Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_DOCUMENT_PREFIX_ERROR).size());
        Assert.assertEquals(29, report.getErrorsByCode(DgprErrorCodes.DGPR_FILENAME_PREFIX_ERROR).size());

        // controls performed with PostGIS (inclusion, partition) : see
        // testDocumentNotOkPostgis

        /*
         * Zone de suralea ZSA_2 non adjacente à l'ouvrage de protection OUV_2 Zone
         * soustraite à l'inondation ZSI_2 non adjacente à l'ouvrage de protection 0UV_2
         * Zone inondable SIN_2 non adjacente à l'ouvrage de protection OUV_2
         *
         * TODO not implemented (no corresponding control in the DGPR plugin)
         */

        /*
         * Appartenance au même scenario. L'objet LIC_2 de la classe ISO_COTE_L a pour
         * scénario 02Moy différent de celui de la surface inondable SIN_4, de scénario
         * 01For, à laquelle il est rattaché.
         *
         * L'objet ZCH_7 de la classe ISO_HT_S a pour scénario 01For différent de celui
         * de la surface inondable SIN_5, de scénario 02Moy, à laquelle il est rattaché.
         */
        Assert.assertEquals(2, report.getErrorsByCode(DgprErrorCodes.DGPR_UNMATCHED_SCENARIO).size());
        ValidatorError error40 = report.getErrorsByCode(DgprErrorCodes.DGPR_UNMATCHED_SCENARIO).get(0);
        ValidatorError error41 = report.getErrorsByCode(DgprErrorCodes.DGPR_UNMATCHED_SCENARIO).get(1);
        Assert.assertEquals(
            "L'objet ZCH_7 de la classe N_prefixTri_ISO_HT_suffixIsoHt_S_ddd a un scénario (01For) différent de celui de la surface inondable SIN_5 (02Moy) à laquelle il est rattaché.",
            error40.getMessage()
        );
        Assert.assertEquals(
            "L'objet LIC_2 de la classe N_prefixTri_ISO_COTE_L_ddd a un scénario (02Moy) différent de celui de la surface inondable SIN_4 (01For) à laquelle il est rattaché.",
            error41.getMessage()
        );

        /*
         * Validation unicite et relation
         */
        Assert.assertEquals(1, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_NOT_UNIQUE).size());
        ValidatorError error50 = report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_NOT_UNIQUE).get(0);
        Assert.assertEquals(
            "La valeur 'ZE_2' est présente 2 fois pour le champ 'ID_ZONE' de la table 'N_prefixTri_ECOUL_S_ddd'.",
            error50
                .getMessage()
        );

        Assert.assertEquals(5, report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_REFERENCE_NOT_FOUND).size());
        {
            List<ValidatorError> errors = report.getErrorsByCode(CoreErrorCodes.ATTRIBUTE_REFERENCE_NOT_FOUND);
            int index = 0;
            {
                ValidatorError error = errors.get(index++);
                Assert.assertEquals(
                    "La référence N_prefixTri_CARTE_INOND_S_ddd.ID_TRI n'est pas validée. Le champ N_prefixTri_TRI_S_ddd.ID_TRI ne prend pas la valeur 'TRI_ZOB'.",
                    error.getMessage()
                );
            }
        }

    }

    /*
     * Controls performed with PostGIS (GraphTopologyValidator, InclusionValidator)
     * are skipped with SQLITE. The following tests run only if DB_URL is defined
     * (ex : DB_URL=jdbc:postgresql://localhost:5432/validator DB_USER=postgis
     * DB_PASSWORD=postgis).
     *
     * Inclusion of the scenarios (Fort in Moyen, Moyen in Faible and so Fort in
     * Faible) : the surfaces of a scenario must be included in the union of the
     * surfaces of the weaker scenarios.
     */

    private void assumePostgis() {
        Assume.assumeTrue("DB_URL is required (PostGIS)", !StringUtils.isEmpty(System.getenv("DB_URL")));
    }

    private List<String> getMessages(ErrorCode code) {
        return report.getErrorsByCode(code).stream().map(ValidatorError::getMessage).collect(Collectors.toList());
    }

    @Test
    public void testDocumentOkTolerance5Postgis() throws Exception {
        assumePostgis();
        DocumentModel documentModel = getDocumentModel("covadis_di_2018");
        File documentPath = getSampleDocument("TRI_JTEST_TOPO_SIG_DI");

        Context context = createContext(documentPath);
        context.setDgprTolerance(5.0);
        context.setDgprSimplification(null);
        Document document = new Document(documentModel, documentPath);
        document.validate(context);

        Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_GEOM_ERROR).size());
        Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_INTERSECTS).size());
        // TODO to be confirmed (document expected to be valid)
        Assert.assertEquals(
            Arrays.asList(
                "Les ISO_DEB ZCD_1, ZCD_2 ne constituent pas une partition de SIN_1 à laquelle elles se rapportent. Il y a un trou ou un dépassement de la surface inondable."
            ),
            getMessages(DgprErrorCodes.DGPR_ISO_HT_FUSION_NOT_SURFACE_INOND)
        );
        // SIN_1 (01For) is included in the union of SIN_2 and SIN_5 (02Moy)
        Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR).size());
    }

    @Test
    public void testDocumentNotOkPostgis() throws Exception {
        assumePostgis();
        DocumentModel documentModel = getDocumentModel("covadis_di_2018");
        File documentPath = getSampleDocument("TRI_JTEST_TOPO_error_SIG_DI");

        Context context = createContext(documentPath);
        Document document = new Document(documentModel, documentPath);
        document.validate(context);

        Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_GEOM_ERROR).size());
        /*
         * ZCH_9 et ZCH_10 (scénario Faible) ne constituent pas une partition de SIN_6
         */
        Assert.assertEquals(
            Arrays.asList(
                "Les ISO_HT ZCH_9 ZCH_10 ne constituent pas une partition de SIN_6. Leurs périmètres s'intersectent."
            ),
            getMessages(DgprErrorCodes.DGPR_ISO_HT_INTERSECTS)
        );
        List<String> fusionErrors = getMessages(DgprErrorCodes.DGPR_ISO_HT_FUSION_NOT_SURFACE_INOND);
        Assert.assertEquals(2, fusionErrors.size());
        Assert.assertTrue(
            fusionErrors.get(0), fusionErrors.get(0).matches(
                "Les ISO_HT ZCH_(9|10), ZCH_(9|10) ne constituent pas une partition de SIN_6 .*"
            )
        );
        Assert.assertTrue(
            fusionErrors.get(1), fusionErrors.get(1).startsWith(
                "Les ISO_DEB ZCD_1, ZCD_2 ne constituent pas une partition de SIN_1 "
            )
        );
        /*
         * SIN_5 (02Moy) n'est pas incluse dans 04Fai et SIN_4 (01For), incluse dans
         * SIN_5, dépasse donc de 04Fai
         */
        Assert.assertEquals(
            Arrays.asList(
                "La surface SIN_4 du scénario 01For n'est pas incluse dans le scénario 04Fai - SIN_3, SIN_6.",
                "La surface SIN_5 du scénario 02Moy n'est pas incluse dans le scénario 04Fai - SIN_3, SIN_6."
            ),
            getMessages(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR)
        );
    }

    @Test
    public void testDocumentNotOkTolerance10Postgis() throws Exception {
        assumePostgis();
        DocumentModel documentModel = getDocumentModel("covadis_di_2018");
        File documentPath = getSampleDocument("TRI_JTEST_TOPO_error_SIG_DI");

        Context context = createContext(documentPath);
        context.setDgprTolerance(10.0);
        context.setDgprSimplification(5.0);
        Document document = new Document(documentModel, documentPath);
        document.validate(context);

        Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_GEOM_ERROR).size());
        Assert.assertEquals(1, report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_INTERSECTS).size());
        // ZCD_1, ZCD_2 / SIN_1 is accepted with a tolerance of 10 meters
        List<String> fusionErrors = getMessages(DgprErrorCodes.DGPR_ISO_HT_FUSION_NOT_SURFACE_INOND);
        Assert.assertEquals(1, fusionErrors.size());
        Assert.assertTrue(fusionErrors.get(0), fusionErrors.get(0).contains("de SIN_6"));
        Assert.assertEquals(
            Arrays.asList(
                "La surface SIN_4 du scénario 01For n'est pas incluse dans le scénario 04Fai - SIN_3, SIN_6.",
                "La surface SIN_5 du scénario 02Moy n'est pas incluse dans le scénario 04Fai - SIN_3, SIN_6."
            ),
            getMessages(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR)
        );
    }

}
