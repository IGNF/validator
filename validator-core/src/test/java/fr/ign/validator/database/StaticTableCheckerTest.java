package fr.ign.validator.database;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fr.ign.validator.Context;
import fr.ign.validator.error.CoreErrorCodes;
import fr.ign.validator.error.ValidatorError;
import fr.ign.validator.model.DocumentModel;
import fr.ign.validator.model.StaticTable;
import fr.ign.validator.report.InMemoryReportBuilder;

public class StaticTableCheckerTest {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private InMemoryReportBuilder reportBuilder = new InMemoryReportBuilder();

    private Context context;

    @Before
    public void setUp() {
        context = new Context();
        context.setReportBuilder(reportBuilder);
    }

    private StaticTable createStaticTable(String name, File data) throws Exception {
        StaticTable staticTable = new StaticTable();
        staticTable.setName(name);
        staticTable.setTitle(name);
        staticTable.setDataReference("./codes/" + data.getName());
        staticTable.setData(data.toURI().toURL());
        return staticTable;
    }

    @Test
    public void testAvailable() throws Exception {
        File csv = folder.newFile("DocUrbaType.csv");
        FileUtils.writeStringToFile(csv, "TYPEDOC\nPLU\n", "UTF-8");

        DocumentModel documentModel = new DocumentModel();
        documentModel.getStaticTables().add(createStaticTable("DocUrbaType", csv));

        Assert.assertTrue(StaticTableChecker.checkAvailability(context, documentModel));
        Assert.assertEquals(0, reportBuilder.getErrorsByCode(CoreErrorCodes.MODEL_STATIC_TABLE_NOT_FOUND).size());
    }

    /**
     * ex :
     * https://www.geoportail-urbanisme.gouv.fr/standard/codes/ListeTypedocPLU.csv
     * removed from the server
     */
    @Test
    public void testNotFound() throws Exception {
        File csv = folder.newFile("DocUrbaType.csv");
        File missing = new File(folder.getRoot(), "ListeTypedocPLU.csv");

        DocumentModel documentModel = new DocumentModel();
        documentModel.getStaticTables().add(createStaticTable("DocUrbaType", csv));
        documentModel.getStaticTables().add(createStaticTable("ListeTypedocPLU", missing));

        Assert.assertFalse(StaticTableChecker.checkAvailability(context, documentModel));

        List<ValidatorError> errors = reportBuilder.getErrorsByCode(CoreErrorCodes.MODEL_STATIC_TABLE_NOT_FOUND);
        Assert.assertEquals(1, errors.size());
        String message = errors.get(0).getMessage();
        Assert.assertTrue(
            message, message.startsWith("La table de référence ListeTypedocPLU du modèle est inaccessible")
        );
        Assert.assertTrue(message, message.contains(missing.toURI().toURL().toString() + " : fichier introuvable"));
    }

    @Test
    public void testUrlNotResolved() throws Exception {
        StaticTable staticTable = new StaticTable();
        staticTable.setName("PrescriptionUrbaType");
        staticTable.setDataReference("./codes/PrescriptionUrbaType.csv");

        DocumentModel documentModel = new DocumentModel();
        documentModel.getStaticTables().add(staticTable);

        Assert.assertFalse(StaticTableChecker.checkAvailability(context, documentModel));
        Assert.assertEquals(1, reportBuilder.getErrorsByCode(CoreErrorCodes.MODEL_STATIC_TABLE_NOT_FOUND).size());
    }

}
