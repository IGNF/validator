# CHANGELOG

## 4.3.x

* v4.3.0 - Update Documentation; add Foreign Key constraints validation

## 4.2.x

[See milestone](https://github.com/IGNF/validator/milestone/12?closed=1) :

* v4.2.0 - Upgrade GeoTools and JTS (note that coordinate precision changes for the last decimals in normalized data and geometry errors)
* v4.2.0 - Add XSD validation for XML and GML files (PCRS and metadata file)
* v4.2.0 - Add support for GML with MultiTable for PCRS
* v4.2.0 - Add support for some french projections used in PCRS datasets ("Coniques Conformes Zone 1 to 9")
* v4.2.0 - Improve GML reading using GMLAS driver if an xsdSchema is provided
* v4.2.1 - Add support for curved geometries
* v4.2.2 - Subcommand --help
* v4.2.3 - Improve test
* v4.2.4 - Add geometry complexity validation 
* v4.2.5 - Update log4j version
* v4.2.6 - Rename licence; Add SQL constraints validation

## 4.1.x

[See milestone](https://github.com/IGNF/validator/milestone/10?closed=1)

## 4.0.x

[See milestone](https://github.com/IGNF/validator/milestone/8?closed=1)

## Core

* Remove java 7 support, support java 9, 10,... and upgrade dependencies
* Remove ogr2ogr 1.x support and improve convertion to CSV robutness
* Add support for `HTTP_PROXY`, `HTTPS_PROXY` and `NO_PROXY` environment variables (`--proxy` deprecated)

## Models

* Add hability to load JSON models from URL (XML is deprecated)
* Improve and document JSON models with JSON schemas (validator-schema repository)
* Add hability to load data to PostgreSQL in a custom schema
* Handle changes for metadata validation and conversion to JSON

## Deploy

* Provide `.deb` or `.rpm` throw `fpm` to deploy to `/opt/validator/validator-cli.jar`?


## 3.4.*

* Update jackson
* Add support for HTTP proxy with authentication
* Improve validator-dgpr-plugin

## 3.3.*

* Add SQLITE Database utility in validator-core to simplify some checks
* Upgrade tests to junit 4 syntax
* Change Model
    * Add Reference property to fr.ign.validator.model.AttributeType
        * Loading from <reference> tag in XML file model
        * Follow the convetion TABLE_NAME.ATTRIBUTE_NAME
        * Used in RelationValidator (validator-plugin-dgpr)
* Improve validator-dgpr-plugin
    * DatabasePostProcess perform topologic validation and relation validation
    * Database validation class must implements Validator<Database>
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

