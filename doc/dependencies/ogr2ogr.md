# ogr2ogr from GDAL

## Description

[ogr2ogr](https://gdal.org/programs/ogr2ogr.html) from [GDAL](https://gdal.org/index.html) is used to convert input data to UTF-8 encoded CSV with WKT geometries.

Version **2.3.0** or more is required as it allows uniform charset handling for input data.

## Tested versions

* 2.4.2

## Notes

* Between GDAL 1.x and 2.x, GDAL introduce a regression in WKT precision management while converting to CSV (OGR_WKT_PRECISION is a global precision, not a number of decimals). Meanwhile, coordinate accuracy is better with GDAL 2.x.
* Between GDAL 2.2 and 2.3, GDAL introduce a regression while converting LATIN1 TAB to CSV. Since GDAL 2.3.0, output CSV is UTF-8 encoded according to charset declaration in TAB files.

## Setup

* Official documentation : [gdal.org - Download / Binaries](https://gdal.org/download.html#binaries)
* Windows : [OSGeo4W](https://www.osgeo.org/projects/osgeo4w/)
* Debian/Ubuntu :

```bash
# for newer versions :
# sudo apt-add-repository ppa:ubuntugis/ppa
sudo apt-get update
sudo apt-get install gdal-bin
ogr2ogr --version
```


