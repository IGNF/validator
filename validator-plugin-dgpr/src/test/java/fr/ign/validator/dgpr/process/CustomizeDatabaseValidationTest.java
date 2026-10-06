package fr.ign.validator.dgpr.process;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import fr.ign.validator.Context;
import fr.ign.validator.data.Document;
import fr.ign.validator.dgpr.validation.database.GraphTopologyValidator;
import fr.ign.validator.dgpr.validation.database.InclusionValidator;
import fr.ign.validator.dgpr.validation.database.ScenarioValidator;
import fr.ign.validator.model.DocumentModel;

public class CustomizeDatabaseValidationTest {

    private List<Class<?>> registerValidators(Context context) throws Exception {
        DocumentModel documentModel = new DocumentModel();
        Document document = new Document(documentModel, new File("TRI_TEST_SIG_DI"));
        new CustomizeDatabaseValidation().beforeMatching(context, document);
        return documentModel.getDatabaseValidators().stream()
            .map(Object::getClass)
            .collect(Collectors.toList());
    }

    @Test
    public void testAllControlsByDefault() throws Exception {
        List<Class<?>> validators = registerValidators(new Context());

        Assert.assertTrue(validators.contains(ScenarioValidator.class));
        Assert.assertTrue(validators.contains(GraphTopologyValidator.class));
        Assert.assertTrue(validators.contains(InclusionValidator.class));
    }

    @Test
    public void testSkipInclusion() throws Exception {
        Context context = new Context();
        context.setDgprSkipInclusion(true);
        List<Class<?>> validators = registerValidators(context);

        Assert.assertTrue(validators.contains(ScenarioValidator.class));
        Assert.assertTrue(validators.contains(GraphTopologyValidator.class));
        Assert.assertFalse(validators.contains(InclusionValidator.class));
    }

    @Test
    public void testSkipGraphTopology() throws Exception {
        Context context = new Context();
        context.setDgprSkipGraphTopology(true);
        List<Class<?>> validators = registerValidators(context);

        Assert.assertTrue(validators.contains(ScenarioValidator.class));
        Assert.assertFalse(validators.contains(GraphTopologyValidator.class));
        Assert.assertTrue(validators.contains(InclusionValidator.class));
    }

}
