package fr.ign.validator.cnig.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.cnig.CnigRegressHelper;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.FeatureType;
import fr.ign.validator.model.FileModel;
import fr.ign.validator.model.TableModel;
import fr.ign.validator.model.file.SingleTableModel;
import fr.ign.validator.tools.ResourceHelper;

/**
 * Test VRT generation
 *
 * @author MBorne
 *
 */
public class VRTTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testDocUrba() throws Exception {
        // Copy DOC_URBA.csv
        File csvFile = folder.newFile("DOC_URBA.csv");
        FileUtils.copyFile(
            ResourceHelper.getResourceFile(getClass(), "/csv/DOC_URBA.csv"),
            csvFile
        );

        // Retrieve FeatureType
        DocumentModel documentModel = CnigRegressHelper.getDocumentModel("cnig_PLU_2017");
        FileModel fileModel = documentModel.getFileModelByName("DOC_URBA");
        assertTrue(fileModel instanceof SingleTableModel);
        FeatureType featureType = ((TableModel) fileModel).getFeatureType();
        File vrtFile = VRT.createVRTfile(csvFile, featureType);
        assertTrue(vrtFile.exists());

        File expectedVrtFile = ResourceHelper.getResourceFile(getClass(), "/csv/DOC_URBA.vrt");
        String expected = FileUtils.readFileToString(expectedVrtFile, StandardCharsets.UTF_8);
        String actual = FileUtils.readFileToString(vrtFile, StandardCharsets.UTF_8);
        assertEquals(expected, actual);
    }

}
