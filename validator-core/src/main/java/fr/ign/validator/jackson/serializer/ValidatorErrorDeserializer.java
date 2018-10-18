package fr.ign.validator.jackson.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import fr.ign.validator.error.ErrorCode;
import fr.ign.validator.error.ErrorLevel;
import fr.ign.validator.error.ValidatorError;

/**
 * Deserialize ValidatorError
 * 
 * @see https://www.baeldung.com/jackson-deserialization
 * 
 * @author MBorne
 *
 */
public class ValidatorErrorDeserializer extends StdDeserializer<ValidatorError> { 

	private static final long serialVersionUID = 1L;

	public ValidatorErrorDeserializer() { 
        this(null); 
    } 
 
    public ValidatorErrorDeserializer(Class<?> vc) { 
        super(vc); 
    }

    
    @Override
    public ValidatorError deserialize(JsonParser jp, DeserializationContext ctxt) 
      throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
 
        ErrorCode code = ErrorCode.valueOf( node.get("name").asText() ) ;
        ValidatorError error = new ValidatorError(code);
        error.setLevel(ErrorLevel.valueOf(node.get("level").asText()));
        error.setMessage(node.get("message").asText());
        return error;
    }
}

