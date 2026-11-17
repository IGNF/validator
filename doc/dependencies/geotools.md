# GeoTools

## Description

[GeoTools](https://geotools.org/) is mainly used to manipulate projection.

## Projection

Coordinate order behavior differs between usage of `gt-epsg-hsql` or `gt-epsg-wkt`.

There is no way to manage standard lat,lon for EPSG:4326 with `gt-epsg-wkt`.

So, `gt-epsg-hsql` is used as `java -Dorg.geotools.referencing.forceXY=true` may allow non standard lon,lat order.

## Relation to java version

The validator requires Java 17, as GeoTools 34 and above (GeoTools 33.x is the last series supporting Java 11).

Since GeoTools 30, the `org.opengis` packages are replaced by `org.geotools.api` (ex : `org.geotools.api.referencing.crs.CoordinateReferenceSystem`), see [upgrade guide](https://docs.geotools.org/latest/userguide/welcome/upgrade.html).

See [Geotools - Java Install](http://docs.geotools.org/latest/userguide/build/install/jdk.html#) for more details.




