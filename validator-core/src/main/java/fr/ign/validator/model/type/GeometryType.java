package fr.ign.validator.model.type;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.io.WKTReader;

import com.fasterxml.jackson.annotation.JsonTypeName;

import fr.ign.validator.model.AttributeType;
import fr.ign.validator.validation.attribute.GeometryDataExtentValidator;
import fr.ign.validator.validation.attribute.GeometryIsValidValidator;

/**
 * 
 * A geometry of any type
 * 
 * @author MBorne
 *
 */
@JsonTypeName(GeometryType.TYPE)
public class GeometryType extends AttributeType<Geometry> {

    public static final String TYPE = "Geometry";

    public GeometryType() {
        super(Geometry.class);
        addValidator(new GeometryIsValidValidator());
        addValidator(new GeometryDataExtentValidator());
    }

    public static WKTReader format = new WKTReader();

    @Override
    public String getTypeName() {
        return TYPE;
    }

    @Override
    public boolean isGeometry() {
        return true;
    }

    @Override
    public Geometry bind(Object value) {
        if (value == null || value instanceof Geometry) {
            return (Geometry) value;
        }

        String wkt = (String) value;
        try {
            Geometry geometry = format.read(wkt);
            return geometry;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                String.format("Format de géométrie invalide : {}", wkt)
            );
        }
    }

    @Override
    public String format(Geometry value) {
        if (null == value) {
            return null;
        }
        return value.toText();
    }

    /**
     * Extracting a geometry from a collection
     * 
     * @param geometry
     * @param c
     * @return
     */
    protected Geometry extractOneFromCollection(Geometry geometry, Class<?> c) {
        if (null == geometry || geometry.isEmpty()) {
            return geometry;
        } else if (c.isAssignableFrom(geometry.getClass())) {
            return geometry;
        } else if (geometry instanceof GeometryCollection) {
            if (1 != geometry.getNumGeometries()) {
                throw new IllegalArgumentException(getMessageInvalidGeometryType(geometry));
            }
            if (c.isAssignableFrom(geometry.getGeometryN(0).getClass())) {
                return geometry.getGeometryN(0);
            }
        }
        throw new IllegalArgumentException(getMessageInvalidGeometryType(geometry));
    }

    /**
     * Returns an IllegalArgumentException saying the geometry type is not correct
     * 
     * @param value
     */
    protected String getMessageInvalidGeometryType(Geometry value) {
        return String.format("Type de géométrie invalide %1s (%2s)", value.getGeometryType(), value.toText());
    }

}
