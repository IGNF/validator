package fr.ign.validator.cnig.validation.metadata;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import fr.ign.validator.cnig.model.DocumentName;
import fr.ign.validator.cnig.validation.metadata.internal.RequiredKeyword;

public class CnigMetadataKeywordsValidatorTest {

    @Test
    public void testGetRequiredKeywordsPlu() {
        List<RequiredKeyword> requiredKeywords = CnigMetadataKeywordsValidator.getRequiredKeywords(new DocumentName("25349_PLU_20200101"));
        assertEquals(2, requiredKeywords.size());
        int index = 0;
        {
            RequiredKeyword item = requiredKeywords.get(index++);
            assertEquals("TYPE_DOC", item.name);
            assertEquals("PLU", item.expectedValue);
        }
        {
            RequiredKeyword item = requiredKeywords.get(index++);
            assertEquals("EMPRISE", item.name);
            assertEquals("25349", item.expectedValue);
        }
    }

    @Test
    public void testGetRequiredKeywordsPluPartial() {
        List<RequiredKeyword> requiredKeywords = CnigMetadataKeywordsValidator.getRequiredKeywords(
            new DocumentName("25349_PLU_20200101_B")
        );
        assertEquals(3, requiredKeywords.size());
        int index = 0;
        {
            RequiredKeyword item = requiredKeywords.get(index++);
            assertEquals("TYPE_DOC", item.name);
            assertEquals(CnigMetadataKeywordsValidator.THESAURUS_TYPE_DOC, item.thesaurusName);
            assertEquals("PLU", item.expectedValue);
        }
        {
            RequiredKeyword item = requiredKeywords.get(index++);
            assertEquals("EMPRISE", item.name);
            assertEquals(CnigMetadataKeywordsValidator.THESAURUS_COG, item.thesaurusName);
            assertEquals("25349", item.expectedValue);
        }
        {
            RequiredKeyword item = requiredKeywords.get(index++);
            assertEquals("CodeDU", item.name);
            assertEquals(CnigMetadataKeywordsValidator.THESAURUS_CODE_DU, item.thesaurusName);
            assertEquals("B", item.expectedValue);
        }
    }

    @Test
    public void testGetRequiredKeywordsPos() {
        List<RequiredKeyword> requiredKeywords = CnigMetadataKeywordsValidator.getRequiredKeywords(new DocumentName("25349_POS_20200101"));
        assertEquals(2, requiredKeywords.size());
        int index = 0;
        {
            RequiredKeyword item = requiredKeywords.get(index++);
            assertEquals("TYPE_DOC", item.name);
            assertEquals(CnigMetadataKeywordsValidator.THESAURUS_TYPE_DOC, item.thesaurusName);
            assertEquals("POS", item.expectedValue);
        }
        {
            RequiredKeyword item = requiredKeywords.get(index++);
            assertEquals("EMPRISE", item.name);
            assertEquals(CnigMetadataKeywordsValidator.THESAURUS_COG, item.thesaurusName);
            assertEquals("25349", item.expectedValue);
        }
    }

    @Test
    public void testGetRequiredKeywordsCc() {
        List<RequiredKeyword> requiredKeywords = CnigMetadataKeywordsValidator.getRequiredKeywords(new DocumentName("25349_CC_20200101"));
        assertEquals(2, requiredKeywords.size());
        int index = 0;
        {
            RequiredKeyword item = requiredKeywords.get(index++);
            assertEquals("TYPE_DOC", item.name);
            assertEquals(CnigMetadataKeywordsValidator.THESAURUS_TYPE_DOC, item.thesaurusName);
            assertEquals("CC", item.expectedValue);
        }
        {
            RequiredKeyword item = requiredKeywords.get(index++);
            assertEquals("EMPRISE", item.name);
            assertEquals(CnigMetadataKeywordsValidator.THESAURUS_COG, item.thesaurusName);
            assertEquals("25349", item.expectedValue);
        }
    }

    @Test
    public void testGetRequiredKeywordsPsmv() {
        List<RequiredKeyword> requiredKeywords = CnigMetadataKeywordsValidator.getRequiredKeywords(new DocumentName("25349_PSMV_20200101"));
        assertEquals(2, requiredKeywords.size());
        int index = 0;
        {
            RequiredKeyword item = requiredKeywords.get(index++);
            assertEquals("TYPE_DOC", item.name);
            assertEquals(CnigMetadataKeywordsValidator.THESAURUS_TYPE_DOC, item.thesaurusName);
            assertEquals("PSMV", item.expectedValue);
        }
        {
            RequiredKeyword item = requiredKeywords.get(index++);
            assertEquals("EMPRISE", item.name);
            assertEquals(CnigMetadataKeywordsValidator.THESAURUS_COG, item.thesaurusName);
            assertEquals("25349", item.expectedValue);
        }
    }

    @Test
    public void testGetRequiredKeywordsPlui() {
        List<RequiredKeyword> requiredKeywords = CnigMetadataKeywordsValidator.getRequiredKeywords(
            new DocumentName("123456789_PLUi_20200101")
        );
        assertEquals(2, requiredKeywords.size());
        int index = 0;
        {
            RequiredKeyword item = requiredKeywords.get(index++);
            assertEquals("TYPE_DOC", item.name);
            assertEquals(CnigMetadataKeywordsValidator.THESAURUS_TYPE_DOC, item.thesaurusName);
            assertEquals("PLUI", item.expectedValue);
        }
        {
            RequiredKeyword item = requiredKeywords.get(index++);
            assertEquals("EMPRISE", item.name);
            assertEquals(CnigMetadataKeywordsValidator.THESAURUS_SIREN, item.thesaurusName);
            assertEquals("123456789", item.expectedValue);
        }
    }

    @Test
    public void testGetRequiredKeywordsScot() {
        List<RequiredKeyword> requiredKeywords = CnigMetadataKeywordsValidator.getRequiredKeywords(new DocumentName("123456789_scot"));
        assertEquals(2, requiredKeywords.size());
        int index = 0;
        {
            RequiredKeyword item = requiredKeywords.get(index++);
            assertEquals("TYPE_DOC", item.name);
            assertEquals(CnigMetadataKeywordsValidator.THESAURUS_TYPE_DOC, item.thesaurusName);
            assertEquals("SCOT", item.expectedValue);
        }
        {
            RequiredKeyword item = requiredKeywords.get(index++);
            assertEquals("SIREN_GESTIONNAIRE", item.name);
            assertEquals(CnigMetadataKeywordsValidator.THESAURUS_SIREN, item.thesaurusName);
            assertEquals("123456789", item.expectedValue);
        }
    }

    @Test
    public void testGetRequiredKeywordsSup() {
        List<RequiredKeyword> requiredKeywords = CnigMetadataKeywordsValidator.getRequiredKeywords(
            new DocumentName("172014607_AC1_2A_20180130")
        );
        assertEquals(3, requiredKeywords.size());
        int index = 0;
        {
            RequiredKeyword item = requiredKeywords.get(index++);
            assertEquals("CATEGORIE_SUP", item.name);
            assertEquals(CnigMetadataKeywordsValidator.THESAURUS_CATEGORIE_SUP, item.thesaurusName);
            assertEquals("AC1", item.expectedValue);
        }
        {
            RequiredKeyword item = requiredKeywords.get(index++);
            assertEquals("SIREN_GESTIONNAIRE", item.name);
            assertEquals(CnigMetadataKeywordsValidator.THESAURUS_SIREN, item.thesaurusName);
            assertEquals("172014607", item.expectedValue);
        }
        {
            RequiredKeyword item = requiredKeywords.get(index++);
            assertEquals("EMPRISE", item.name);
            assertEquals(CnigMetadataKeywordsValidator.THESAURUS_COG, item.thesaurusName);
            assertEquals("2A", item.expectedValue);
        }
    }

}
