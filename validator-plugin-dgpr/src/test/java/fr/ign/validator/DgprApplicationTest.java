package fr.ign.validator;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.data.Document;
import fr.ign.validator.dgpr.error.DgprErrorCodes;
import fr.ign.validator.error.CoreErrorCodes;
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
        context.setDgprSafeMode(true);
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

        // validation database
        Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR).size());
        Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_INTERSECTS).size());
        // 1 au lieu de 0 ??
        // Assert.assertEquals(1,
        // report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_FUSION_NOT_SURFACE_INOND).size());
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
        context.setDgprSafeMode(true);
        Document document = new Document(documentModel, documentPath);
        document.validate(context);
        Assert.assertEquals("TRI_JTEST_TOPO_error_SIG_DI", document.getDocumentName());
        Assert.assertEquals(0, report.getErrorsByCode(DgprErrorCodes.DGPR_DOCUMENT_PREFIX_ERROR).size());
        Assert.assertEquals(29, report.getErrorsByCode(DgprErrorCodes.DGPR_FILENAME_PREFIX_ERROR).size());

        // validation database
        // Assert.assertEquals(2,
        // report.getErrorsByCode(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR).size());
        // Assert.assertEquals(1,
        // report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_INTERSECTS).size());
        // Assert.assertEquals(1,
        // report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_FUSION_NOT_SURFACE_INOND).size());
        // ValidatorError error =
        // report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_FUSION_NOT_SURFACE_INOND).get(0);
        // Assert.assertEquals(
        // "Les ISO_HT ZCH_9, ZCH_10 ne constituent pas une partition de SIN_6 à
        // laquelle elles se rapportent. Il y a un trou ou un dépassement de la surface
        // inondable.",
        // error.getMessage()
        // );
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

        /*
         * Scénarios d'inclusion : SIN_5 (Moyen) n'est pas incluse dans SIN_6 (Faible)
         * Scénarios d'inclusion : SIN_4 (Fort) n'est pas incluse dans SIN_6 (Faible)
         */
        // Assert.assertEquals(0,
        // report.getErrorsByCode(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR).size());
        // ValidatorError error0 =
        // report.getErrorsByCode(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR).get(0);
        // ValidatorError error1 =
        // report.getErrorsByCode(DgprErrorCodes.DGPR_INOND_INCLUSION_ERROR).get(1);
        // Assert.assertEquals(
        // "La surface SIN_5 du scénario 02Moy n'est pas incluse dans le scénario
        // 04Fai.", error0.getMessage()
        // );
        // Assert.assertEquals(
        // "La surface SIN_4 du scénario 01For n'est pas incluse dans le scénario
        // 04Fai.", error1.getMessage()
        // );

        /*
         * ZCH_9 et ZCH_10 (scénario Faible) ne constituent pas une partition de SIN_6
         */
        // Assert.assertEquals(1,
        // report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_INTERSECTS).size());
        /*
         * ValidatorError error20 =
         * report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_INTERSECTS).get(0); Assert.
         * assertEquals("Les ISO_HT ZCH_9, ZCH_10 ne constituent pas une partition de SIN_6. Leurs périmètres s'intersectent."
         * , error20.getMessage());
         */
        // Assert.assertEquals(2,
        // report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_FUSION_NOT_SURFACE_INOND).size());
        /*
         * ValidatorError error21 =
         * report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_FUSION_NOT_SURFACE_INOND).
         * get(0); Assert.
         * assertEquals("Les ISO_DEB ZCD_1, ZCD_2 ne constituent pas une partition de SIN_1 à laquelle elles se rapportent. Il y a un trou ou un dépassement de la surface inondable."
         * , error21.getMessage()); ValidatorError error22 =
         * report.getErrorsByCode(DgprErrorCodes.DGPR_ISO_HT_FUSION_NOT_SURFACE_INOND).
         * get(1); Assert.
         * assertEquals("Les ISO_HT ZCH_9, ZCH_10 ne constituent pas une partition de SIN_6 à laquelle elles se rapportent. Il y a un trou ou un dépassement de la surface inondable."
         * , error22.getMessage());
         */

        /*
         * Zone de suralea ZSA_2 non adjacente à l'ouvrage de protection OUV_2 Zone
         * soustraite à l'inondation ZSI_2 non adjacente à l'ouvrage de protection 0UV_2
         * Zone inondable SIN_2 non adjacente à l'ouvrage de protection OUV_2
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

}
