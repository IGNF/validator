package fr.ign.validator.model;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.ign.validator.io.json.ObjectMapperFactory;
import fr.ign.validator.model.file.SingleTableModel;

public class FileModelTest {

    /**
     * Test jackson's config
     * 
     * @throws IOException
     */
    @Test
    public void testJsonIO() throws IOException {
        FileModel model = new SingleTableModel();
        model.setName("TEST");
        model.setPath("sample/path/to/file");

        // save to JSON
        ObjectMapper mapper = ObjectMapperFactory.createObjectMapper();
        String result = mapper.writeValueAsString(model);
        assertEquals(
            "{\"type\":\"table\",\"name\":\"TEST\",\"path\":\"sample/path/to/file\",\"mandatory\":\"WARN\"}", result
        );

        // read from JSON
        FileModel newModel = mapper.readValue(result, FileModel.class);
        assertEquals(model.getName(), newModel.getName());
        assertEquals(model.getPath(), newModel.getPath());
    }

}
