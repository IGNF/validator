# ogr2ogr

## Description

`ogr2ogr` from `gdal-bin` is used to convert input data to CSV.

## Tested versions

* 1.9.1
* 1.9.3
* 1.10.1
* 1.11.3
* 2.1.*
* 2.2.2

## Banned versions

* 1.9.0 : WKT bug (8000 chars limit)

## Notes

Between GDAL 1.x and 2.x, GDAL introduce a regression in WKT precision management while converting to CSV (OGR_WKT_PRECISION is a global precision, not a number of decimals). Meanwhile, coordinate accuracy is better with GDAL 2.x.




