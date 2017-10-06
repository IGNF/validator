# 3.0.* (in progress)

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

TODO :

* [ ] Classify characters validation errors (WARNING or ERROR)
* [ ] CLI options for character validation
* [ ] CLI subcommands to clean cnig_pre_validator and to ease futur development (metadata only validation, string only validation in CSV or PGDUMPs, & co)


# 3.1.* (next)

* [ ] Improve validation report
* [ ] Replace LegacyReportBuilder by JsonReportBuilder

