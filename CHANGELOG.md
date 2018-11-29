# CHANGELOG

## 3.3.*

* Add SQLITE Database utility in validator-core to simplify some checks
* Upgrade tests to junit 4 syntax
* Improve validator-dgpr-plugin
* Improve validator-plugin-cnig
	* CoordinateReferenceSystem in metadata validation is now based on URI
	* Avoid fatal error when building SQLITE database in case of duplicated ID (remove index)
* Update validator-error.json messages

## 3.2.*

* Strict coordinate order for EPSG:4326 (lat,lon)
* Add validator-dgpr-plugin
* Improve template message for ValidatorError (validator-error.json) introducing named parameters

## 3.1.*

* Add support for GDAL 2.x
* Add regress tests for `FileConverter` (sensitive to changes in [GDAL's CSV driver](https://www.gdal.org/drv_csv.html)) 
* Remove LegacyReportBuilder and set JsonReportBuilder as default reporter?

## 3.0.*

* CLI subcommands for validator-cli (```java -jar validator-cli.jar COMMAND --help```)
* update java to 1.7
* update dependencies (geotools)
* add option ```--data-extent``` to check data extent (and projection)
* add option ```--plugins``` and remove maven profile "cnig"
* simplify error context management
* check ogr2ogr's version and ban some versions
* deep characters validation

	* Detect and replace double encoded UTF-8 characters by UTF-8 characters
	* Simplify strings (replace characters by equivalents supported by main fonts, replace characters not supported by charsets)
	* Escape ISO controls to hexa
	* Escape characters not supported by charset to hexa
	* CLI options for character validation (```--string-fix [CHARSET]```)
	* Classify characters validation errors (WARNING or ERROR)?


* Metadata validation (see [doc/metadata/index.md](doc/metadata/index.md))

    * Improve metadata parsing 
    * Validate INSPIRE constraints (most of them)
    * Validate CNIG constraints (additional constraints for https://www.geoportail-urbanisme.gouv.fr)

* Improve error reporting

    * Simplify report generation (copy Context informations to ValidatorError)
    * Add [JSONL](http://jsonlines.org/) report format (each line is a ValidatorError serialized in JSON)

