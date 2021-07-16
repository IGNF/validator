# Geometry

## Description

Une géométrie modélise une forme géométrique ponctuelle, linéaire ou surfacique. Le type `Geometry` possède des sous-type permettant de restreindre le type géométrique sur les types suivants `Point`, `LineString`, `Polygon`, `MultiPoint`, `MultiLineString`, `MultiPolygon`, `GeometryCollection`.

## Formats

Les formats supportés par ogr2ogr de GDAL sont supportés par le validateur pour les géométries. Globalement, le validateur supporte les formats :

* `Shapefile`
* `MapInfo`
* `GML`
* `GeoJSON`
* `WKT` (dans CSV)

## Mapping

| Langage    | Type                                                         |
| ---------- | ------------------------------------------------------------ |
| Java       | `com.vividsolutions.jts.geom.Geometry`                       |
| JSON       | [GeoJSON Geometry](https://geojson.org/schema/Geometry.json) |
| PostgreSQL | `geometry(Geometry)`                                         |
