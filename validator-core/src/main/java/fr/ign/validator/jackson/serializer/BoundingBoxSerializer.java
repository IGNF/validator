package fr.ign.validator.jackson.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.vividsolutions.jts.geom.Envelope;

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

		Envelope env = value.toEnvelope();
		gen.writeStartArray();
		gen.writeNumber(env.getMinX());
		gen.writeNumber(env.getMinY());
		gen.writeNumber(env.getMaxX());
		gen.writeNumber(env.getMaxY());		
		gen.writeEndArray();
	}

}
