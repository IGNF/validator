package fr.ign.validator.io.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.locationtech.jts.geom.Envelope;

import fr.ign.validator.tools.EnvelopeUtils;

/**
 * Serialize BoundingBox to JSON format [xmin,ymin,xmax,ymax]
 * 
 * @author MBorne
 *
 */
public class EnvelopeSerializer extends JsonSerializer<Envelope> {

    @Override
    public void serialize(Envelope value, JsonGenerator gen, SerializerProvider serializers) throws IOException,
        JsonProcessingException {
        if (value.isNull()) {
            gen.writeNull();
            return;
        }
        gen.writeStartArray();
        gen.writeNumber(EnvelopeUtils.formatDouble(value.getMinX()));
        gen.writeNumber(EnvelopeUtils.formatDouble(value.getMinY()));
        gen.writeNumber(EnvelopeUtils.formatDouble(value.getMaxX()));
        gen.writeNumber(EnvelopeUtils.formatDouble(value.getMaxY()));
        gen.writeEndArray();
    }

}
