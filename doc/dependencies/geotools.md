# GeoTools

## Description

[GeoTools](https://geotools.org/) is mainly used to manipulate projection.

## Projection

Coordinate order behavior differs between usage of `gt-epsg-hsql` or `gt-epsg-wkt`.

There is no way to manage standard lat,lon for EPSG:4326 with `gt-epsg-wkt`.

So, `gt-epsg-hsql` is used as `java -Dorg.geotools.referencing.forceXY=true` may allow non standard lon,lat order.

## Relation to java version

As the validator requires Java 11, GeoTools 21.x and above is supported.

See [Geotools - Java Install](http://docs.geotools.org/latest/userguide/build/install/jdk.html#) for more details.




