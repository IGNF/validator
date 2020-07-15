# geotools

## Relation to java version

See [Geotools - Java Install](http://docs.geotools.org/latest/userguide/build/install/jdk.html#) to get information about java version support.

## Projection

Coordinate order behavior differs between usage of `gt-epsg-hsql` or `gt-epsg-wkt`.

There is no way to manage standard lat,lon for EPSG:4326 with `gt-epsg-wkt`.

So, `gt-epsg-hsql` is used as `java -Dorg.geotools.referencing.forceXY=true` may allow non standard lon,lat order.
