package fr.ign.validator.info;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.jdom.JDOMException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fr.ign.validator.info.model.DocumentInfo;

/**
 * DocumentInfo writer
 *
 * @author MBorne
 */
public class DocumentInfoWriter {

    /**
     * Save DocumentInfo to JSON file
     *
     * @param documentInfo
     * @param output
     * @throws JDOMException
     * @throws IOException
     */
    public void write(DocumentInfo documentInfo, File outputFile) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            objectMapper.writeValue(new PrintStream(outputFile), documentInfo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
