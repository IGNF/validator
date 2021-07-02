package fr.ign.validator.regress;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ErrorLevel;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.Projection;
import fr.ign.validator.model.file.DirectoryModel;
import fr.ign.validator.model.file.MetadataModel;
import fr.ign.validator.model.file.PdfModel;
import fr.ign.validator.model.file.TableModel;
import fr.ign.validator.model.type.GeometryType;
import fr.ign.validator.model.type.StringType;
import fr.ign.validator.report.InMemoryReportBuilder;
import fr.ign.validator.tools.ResourceHelper;

/**
 *
 * Regress test with sample document
 *
 * @author MBorne
 *
 */
public class ValidateDocumentARegressTest {

    private File documentPath;

    private DocumentModel documentModel;

    @Before
    public void setUp() throws Exception {
        documentPath = ResourceHelper.getResourceFile(getClass(), "/documents/commune-sample");

        documentModel = new DocumentModel();
        List<FileModel> fileModels = new ArrayList<FileModel>();
        {
            TableModel tableModel = new TableModel();
            tableModel.setName("COMMUNE");
            tableModel.setPath("commune");

            FeatureType featureType = new FeatureType();
            featureType.setName("COMMUNE");
            // INSEE
            {
                StringType attributeType = new StringType();
                attributeType.setName("INSEE");
                featureType.addAttribute(attributeType);
            }
            // NOM
            {
                StringType attributeType = new StringType();
                attributeType.setName("NOM");
                featureType.addAttribute(attributeType);
            }
            // WKT
            {
                GeometryType attributeType = new GeometryType();
                attributeType.setName("WKT");
                featureType.addAttribute(attributeType);
            }
            tableModel.setFeatureType(featureType);

            fileModels.add(tableModel);
        }
        {
            MetadataModel metadata = new MetadataModel();
            metadata.setName("metadata");
            metadata.setPath(".*");
            fileModels.add(metadata);
        }
        {
            DirectoryModel directory = new DirectoryModel();
            directory.setName("a_directory");
            directory.setPath("a_directory");
            fileModels.add(directory);
        }
        {
            PdfModel directory = new PdfModel();
            directory.setName("a_file");
            directory.setPath("a_directory/a_file");
            fileModels.add(directory);
        }

        documentModel.setFileModels(fileModels);
    }

    @Test
    public void testValidate() throws Exception {
        Context context = new Context();
        context.setNormalizeEnabled(true);
        context.setCurrentDirectory(documentPath);
        context.setProjection(Projection.CODE_CRS84);
        Document document = new Document(documentModel, documentPath);
        File validationDirectory = new File(documentPath.getParentFile(), "validation");
        validationDirectory.mkdirs();
        context.setValidationDirectory(validationDirectory);
        InMemoryReportBuilder reportBuilder = new InMemoryReportBuilder();
        context.setReportBuilder(reportBuilder);

        document.validate(context);

        /* check errors */
        Assert.assertEquals(2, reportBuilder.getErrorsByLevel(ErrorLevel.ERROR).size());
        Assert.assertEquals(1, reportBuilder.getErrorsByCode(CoreErrorCodes.METADATA_SPATIALRESOLUTIONS_EMPTY).size());
        Assert.assertEquals(1, reportBuilder.getErrorsByCode(CoreErrorCodes.METADATA_SPECIFICATIONS_EMPTY).size());

        /* check warnings */
        Assert.assertEquals(0, reportBuilder.getErrorsByLevel(ErrorLevel.WARNING).size());

        /* check infos */
        Assert.assertEquals(2, reportBuilder.getErrorsByLevel(ErrorLevel.INFO).size());
        Assert.assertEquals(1, reportBuilder.getErrorsByCode(CoreErrorCodes.METADATA_IGNORED_FILE).size());
        Assert.assertEquals(1, reportBuilder.getErrorsByCode(CoreErrorCodes.VALIDATOR_PROJECTION_INFO).size());

        File expectedNormalized = new File(context.getDataDirectory(), "COMMUNE.csv");
        Assert.assertTrue(expectedNormalized.exists());

        // from metadata
        Assert.assertEquals(StandardCharsets.ISO_8859_1, context.getEncoding());
    }

}
