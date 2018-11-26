# Notes about geotools

## Relation to version

* Upgrade to JAVA 8 will be required to upgrade geotools to 19.3

## Projection

Coordinate order behavior differs between usage of `gt-epsg-hsql` or `gt-epsg-wkt`.

There is no way to manage standard lat,lon for EPSG:4326 with `gt-epsg-wkt`.

So, `gt-epsg-hsql` is used as `java -Dorg.geotools.referencing.forceXY=true` may allow non standard lon,lat order.



