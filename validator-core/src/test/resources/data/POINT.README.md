# POINT

## Description

POINT files allows to test FileConverter behavior with coordinate precision.

## Files

* [POINT.csv](POINT.csv) : manually edited file with WKT coordinates (LAMBERT 93)
* [POINT.shp](POINT.shp] : POINT.csv converted to "ESRI shapefile"
* [POINT_EXCEPTED_1.11.x.csv](POINT_EXCEPTED_1.11.x.csv) : POINT.shp converted back to CSV with GDAL 1.11.3 (with default OGR_WKT_PRECISION aka : 15 significants digits)
* [POINT_EXCEPTED_2.2.x.csv](POINT_EXCEPTED_2.2.x.csv) : POINT.shp converted back to CSV with GDAL 2.2.2 (with `OGR_WKT_PRECISION=15`, the default value)

POINT.csv is converted to POINT.shp with the following commands and GDAL 2.2

```bash
ogr2ogr -f "ESRI Shapefile" POINT.shp POINT.csv
ogrinfo POINT.shp -sql "ALTER TABLE point DROP COLUMN WKT"
```

## Notes about GDAL/ogr2ogr

* With GDAL 1.11.*, an extra comma is added to the CSV header while converting from SHP ("WKT,ID," instead of "WKT,ID")
* With GDAL 1.11.*, many decimals are printed but `650010.123456789 6600010.12345679` is converted to 
* Since GDAL 2.2.*, `OGR_WKT_PRECISION` does not allow to manage a number of decimals (6600010.123456789 is rounded to 6600010.12345679)






