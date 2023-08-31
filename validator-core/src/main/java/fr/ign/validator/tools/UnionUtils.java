package fr.ign.validator.tools;

import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;

import fr.ign.validator.geometry.GeometryReader;

public class UnionUtils {

    /**
     * Get bounding box from WKT string.
     * 
     * @param wkt
     * @return
     */
    public static Geometry getUnion(List<String> wkts) {
        if (wkts.isEmpty()) {
            return null;
        }
        try {
            Geometry union = null;
            for (String wkt : wkts) {
                GeometryReader reader = new GeometryReader();
                Geometry geometry = reader.read(wkt);
                if (union == null) {
                    union = geometry;
                } else {
                    union = union.union(geometry);
                }
            }
            return union;
        } catch (ParseException e) {
            return null;
        }
    }

}
