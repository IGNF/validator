# 3.0.* (in progress)

* [x] CLI subcommands for validator-cli (```java -jar validator-cli.jar COMMAND --help```)
* [x] update java to 1.7
* [x] update dependencies (geotools)
* [x] add option ```--data-extent``` to check data extent (and projection)
* [x] add option ```--plugins``` and remove maven profile "cnig"
* [x] simplify error context management
* [x] check ogr2ogr's version and ban some versions
* [x] deep characters validation

    * [x] Detect and replace double encoded UTF-8 characters by UTF-8 characters
	* [x] Simplify strings (replace characters by equivalents supported by main fonts, replace characters not supported by charsets)
	* [x] Escape ISO controls to hexa
	* [x] Escape characters not supported by charset to hexa
    * [x] CLI options for character validation (```--string-fix [CHARSET]```)
    * [ ] Classify characters validation errors (WARNING or ERROR)?


* [x] Metadata validation (see [doc/metadata/index.md](doc/metadata/index.md))

    * [x] Improve metadata parsing 
    * [x] Validate INSPIRE constraints (most of them)
    * [x] Validate CNIG constraints (additional constraints for https://www.geoportail-urbanisme.gouv.fr)

* [x] Improve error reporting

    * [x] Simplify report generation (copy Context informations to ValidatorError)
    * [x] Add [JSONL](http://jsonlines.org/) report format (each line is a ValidatorError serialized in JSON)


# 3.1.* (next)

* [ ] Remove LegacyReportBuilder and set JsonReportBuilder as default reporter
