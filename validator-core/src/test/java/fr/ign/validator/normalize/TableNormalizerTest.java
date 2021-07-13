package fr.ign.validator.normalize;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.io.ModelReader;
import fr.ign.validator.io.XmlModelReader;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.Projection;
import fr.ign.validator.model.TableModel;
import fr.ign.validator.model.file.SingleTableModel;
import fr.ign.validator.report.InMemoryReportBuilder;
import fr.ign.validator.tools.ResourceHelper;

public class TableNormalizerTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    InMemoryReportBuilder reportBuilder = new InMemoryReportBuilder();

    private Context context;

    private Document document;

    @Before
    public void setUp() throws Exception {
        context = new Context();
        // ATTENTION dépendance à sourceCRS (ne doit pas être nul du point de vu de
        // CSVNormalizer)
        context.setProjection(Projection.CODE_CRS84);
        context.setReportBuilder(reportBuilder);

        File documentModelPath = ResourceHelper.getResourceFile(getClass(), "/config-xml/adresse/files.xml");
        ModelReader modelLoader = new XmlModelReader();
        DocumentModel documentModel = modelLoader.loadDocumentModel(documentModelPath);

        File documentPath = ResourceHelper.getResourceFile(getClass(), "/documents/adresse-multiple");
        File copy = folder.newFolder(documentPath.getName());
        FileUtils.copyDirectory(documentPath, copy);

        document = new Document(documentModel, copy);

        File validationDirectory = new File(copy.getParentFile(), "validation");
        context.setValidationDirectory(validationDirectory);
    }

    @Test
    public void testNormalise() throws Exception {
        FileModel fileModel = document.getDocumentModel().getFileModelByName("ADRESSE");
        Assert.assertNotNull(fileModel);
        Assert.assertTrue(fileModel instanceof SingleTableModel);
        FeatureType featureType = ((TableModel) fileModel).getFeatureType();

        File targetFile = new File(document.getDocumentPath(), "adresse_normalized.csv");
        TableNormalizer csvNormalizer = new TableNormalizer(context, featureType, targetFile);

        File csvFile1 = new File(document.getDocumentPath(), "adresse_1.csv");
        Assert.assertTrue(csvFile1.exists());
        File csvFile2 = new File(document.getDocumentPath(), "adresse_2.csv");
        Assert.assertTrue(csvFile2.exists());
        File csvFile3 = new File(document.getDocumentPath(), "adresse_3.csv");
        Assert.assertTrue(csvFile3.exists());
        File csvFile4 = new File(document.getDocumentPath(), "adresse_4.csv");
        Assert.assertTrue(csvFile4.exists());

        csvNormalizer.append(csvFile1);
        csvNormalizer.append(csvFile2);
        csvNormalizer.append(csvFile3);
        csvNormalizer.append(csvFile4);
        csvNormalizer.close();

        File expectedFile = ResourceHelper.getResourceFile(getClass(), "/normalizer/expected_adresse_normalized.csv");
        Assert.assertTrue(expectedFile.exists());

        Assert.assertEquals(
            FileUtils.readFileToString(expectedFile, StandardCharsets.UTF_8),
            FileUtils.readFileToString(targetFile, StandardCharsets.UTF_8)
        );
    }

}
