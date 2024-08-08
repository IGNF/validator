package fr.ign.validator.geometry;

import java.util.ArrayList;
import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

/**
 * GeometryRings - determine all rings for a given geometry
 *
 * @author cbouche
 *
 */
public class GeometryRings {

    /**
     * get all geometry for a multiple or simple geometry
     *
     * @param geometry
     * @return
     */
    public static List<LineString> getRings(Geometry geometry) {

        List<LineString> rings = new ArrayList<LineString>();

        for (int i = 0; i < geometry.getNumGeometries(); i++) {
            Geometry part = geometry.getGeometryN(i);
            List<LineString> partRings = getSimpleGeometryRings(part);
            rings.addAll(partRings);
        }

        return rings;
    }

    /**
     * get all geometry for a multiple or simple geometry
     *
     * @param geometry
     * @return
     */
    public static List<LineString> getInnerRings(Geometry geometry) {

        List<LineString> rings = new ArrayList<LineString>();

        for (int i = 0; i < geometry.getNumGeometries(); i++) {
            Geometry part = geometry.getGeometryN(i);
            List<LineString> partRings = getSimpleGeometryInnerRings(part);
            rings.addAll(partRings);
        }

        return rings;
    }

    /**
     * Determine all rings using the geometry type - point have no rings -
     * linestring is a unique ring - geometry is compose of multiple interior rings
     * and an exterior ring
     *
     * @param geometry
     * @return
     */
    private static List<LineString> getSimpleGeometryRings(Geometry geometry) {
        List<LineString> rings = new ArrayList<LineString>();

        if (geometry instanceof Point) {
            return rings;
        }

        if (geometry instanceof LineString) {
            rings.add((LineString) geometry);
        }

        if (geometry instanceof Polygon) {
            rings.add(((Polygon) geometry).getExteriorRing());
            for (int i = 0; i < ((Polygon) geometry).getNumInteriorRing(); i++) {
                rings.add(((Polygon) geometry).getInteriorRingN(i));
            }
        }

        return rings;
    }

    /**
     * Determine all inner rings using the geometry type - point have no rings -
     * linestring have no inner rings - geometry is compose of multiple inner rings
     * and an exterior ring
     *
     * @param geometry
     * @return
     */
    private static List<LineString> getSimpleGeometryInnerRings(Geometry geometry) {
        List<LineString> rings = new ArrayList<LineString>();

        if (geometry instanceof Point) {
            return rings;
        }

        if (geometry instanceof LineString) {
            return rings;
        }

        if (geometry instanceof Polygon) {
            for (int i = 0; i < ((Polygon) geometry).getNumInteriorRing(); i++) {
                rings.add(((Polygon) geometry).getInteriorRingN(i));
            }
        }

        return rings;
    }

}
