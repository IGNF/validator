package fr.ign.validator.io.json;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.ign.validator.model.AttributeType;
import fr.ign.validator.model.AttributeTypeFactory;

/**
 * 
 * Create ObjectMapper instances
 * 
 * @author MBorne
 *
 */
public class ObjectMapperFactory {

    private static final Logger log = LogManager.getRootLogger();
    private static final Marker MARKER = MarkerManager.getMarker("ObjectMapperFactory");

    /**
     * Create an object mapper configured to deal with AttributeType subtypes
     * 
     * @return
     */
    public static ObjectMapper createObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        /*
         * ignore extra field while
         */
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        /*
         * register all known attribute types so that jackson can deserialize attributes
         */
        AttributeTypeFactory attributeTypeFactory = AttributeTypeFactory.getInstance();
        Collection<String> typeNames = attributeTypeFactory.getTypeNames();
        for (String typeName : typeNames) {
            log.trace(MARKER, "Add AttributeType of type {} to ObjectMapper...", typeName);
            AttributeType<?> prototype = attributeTypeFactory.createAttributeTypeByName(typeName);
            objectMapper.registerSubtypes(prototype.getClass());
        }

        return objectMapper;
    }

}
