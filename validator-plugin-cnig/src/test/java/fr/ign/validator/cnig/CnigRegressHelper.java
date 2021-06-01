package fr.ign.validator.cnig;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.io.JsonModelReader;
import fr.ign.validator.io.ModelReader;
import fr.ign.validator.io.XmlModelReader;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.tools.ResourceHelper;

/**
 * 
 * Helper class to build regress test for CNIG validation
 * 
 * @author MBorne
 *
 */
public class CnigRegressHelper {

    /**
     * Get document model
     * 
     * @see validator-plugin-cnig/test/resources/config/{documentModelName}
     * 
     * @param documentModelName
     * @return
     * @throws Exception
     */
    public static DocumentModel getDocumentModel(String documentModelName) {
        try {
            return getDocumentModelXML(documentModelName);
        } catch (Exception e) {
            return getDocumentModelJSON(documentModelName);
        }
    }

    private static DocumentModel getDocumentModelXML(String documentModelName) {
        File documentModelPath = ResourceHelper.getResourceFile(
            CnigRegressHelper.class,
            "/config/" + documentModelName + "/files.xml"
        );
        ModelReader loader = new XmlModelReader();
        DocumentModel documentModel = loader.loadDocumentModel(documentModelPath);
        documentModel.setName(documentModelName);
        return documentModel;
    }

    private static DocumentModel getDocumentModelJSON(String documentModelName) {
        File documentModelPath = ResourceHelper.getResourceFile(
            CnigRegressHelper.class,
            "/config/" + documentModelName + "/files.json"
        );
        ModelReader loader = new JsonModelReader();
        DocumentModel documentModel = loader.loadDocumentModel(documentModelPath);
        documentModel.setName(documentModelName);
        return documentModel;
    }

    /**
     * Get sample document. If folder is not null, document is copied to a temp
     * folder (keep this explicit)
     * 
     * @see validator-plugin-cnig/test/resources/documents/{documentName}
     * 
     * @param documentName
     * @param folder
     * @return
     * @throws IOException
     */
    public static File getSampleDocument(String documentName, TemporaryFolder folder) throws IOException {
        File sourcePath = ResourceHelper.getResourceFile(
            CnigRegressHelper.class,
            "/documents/" + documentName
        );
        if (folder == null) {
            return sourcePath;
        }
        File documentPath = folder.newFolder(documentName);
        FileUtils.copyDirectory(sourcePath, documentPath);
        return documentPath;
    }

    /**
     * Get path to expected document-info.json
     * 
     * @param documentName
     * @return
     */
    public static File getExpectedDocumentInfos(String documentName) {
        return ResourceHelper.getResourceFile(
            CnigRegressHelper.class,
            "/documents-expected/" + documentName + "/document-info.json"
        );
    }

}
