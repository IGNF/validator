# ogr2ogr

## Description

`ogr2ogr` from `gdal-bin` is used to convert input data to UTF-8 encoded CSV with WKT geometries.

Version **2.3.0** or more is recommanded as it allows uniform charset handling for input data.

## Tested versions

* 1.9.1
* 1.9.3
* 1.10.1
* 1.11.3
* 2.1.*
* 2.2.2
* 2.3.0
* 2.3.3
* 2.4.0

## Banned versions

* 1.9.0 : WKT bug (8000 chars limit)

## Notes

* Between GDAL 1.x and 2.x, GDAL introduce a regression in WKT precision management while converting to CSV (OGR_WKT_PRECISION is a global precision, not a number of decimals). Meanwhile, coordinate accuracy is better with GDAL 2.x.
* Between GDAL 2.2 and 2.3, GDAL introduce a regression while converting LATIN1 TAB to CSV. Since GDAL 2.3.0, output CSV is UTF-8 encoded according to TAB declaration.





