package fr.ign.validator.jackson.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import fr.ign.validator.metadata.BoundingBox;

/**
 * Serialize BoundingBox to JSON format [xmin,ymin,xmax,ymax]
 * @author MBorne
 *
 */
public class BoundingBoxSerializer  extends JsonSerializer<BoundingBox> {

	@Override
	public void serialize(BoundingBox value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {

		double[] env = value.toArray();
		gen.writeStartArray();
		gen.writeNumber(env[0]);
		gen.writeNumber(env[1]);
		gen.writeNumber(env[2]);
		gen.writeNumber(env[3]);		
		gen.writeEndArray();
	}

}
