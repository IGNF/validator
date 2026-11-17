# CHANGELOG

## 4.7.x

### v4.7.0 - Behavior changes

* v4.7.0 - **Breaking change** : Java 17 or above is required to run `validator-cli.jar` (compiled for Java 17 instead of Java 11, `UnsupportedClassVersionError` with Java 11). Docker image and CI already use Java 25.

### v4.7.0 - Dependencies and build

* v4.7.1 - CNIG : a metadata thesaurus without keyword (ex : `Types de documents d’urbanisme`) is reported as `CNIG_METADATA_KEYWORD_NOT_FOUND` instead of stopping the validation (`NullPointerException`)
* v4.7.1 - Build : the code formatter configuration is moved from `eclipse/formatter-config.xml` to `.mvn/formatter-config.xml`
* v4.7.1 - XML validation : the XML parser can't access external schemas itself (`ACCESS_EXTERNAL_SCHEMA`), the imported and included schemas are only read by the validator (http(s) with timeouts, local files, other protocols rejected) [SECURITY]
* v4.7.1 - CNIG : `--cnig-document-emprise` check (`CNIG_GEOMETRY_OUTSIDE_DOCUMENT_EMPRISE_ERROR`) with the emprise prepared once (`PreparedGeometry`, indexed segments) instead of a full `contains` per feature : same result, ~1000 times faster with a detailed municipality boundary (128 s to 0.12 s for 20 000 zones and a 100 000 vertices emprise)
* v4.7.1 - Memory : the foreign key mismatches (`TABLE_FOREIGN_KEY_NOT_FOUND`) are reported while reading the query results instead of being all kept in a list (millions of objects for a massively invalid table), same report
* v4.7.1 - HTTPS : TLSv1.3 allowed for the downloads (models, static tables, XSD), `https.protocols=TLSv1,TLSv1.1,TLSv1.2` was forcing TLSv1.2 as TLSv1 and TLSv1.1 are disabled by the JDK
* v4.7.1 - Memory : charset detection reads the files by blocks (a file without line breaks was loaded as a single line), only the first 64 KB of ogr2ogr stderr are read to report a conversion failure
* v4.7.1 - Temporary files : the CSV conversions next to the data (`*.vrows`, `*.vtabs`) are removed at the end of the validation (also on failure) instead of the next run, ogr2ogr work directories (`.validator-ogr2ogr-*`) left by a killed process are removed, `--cnig-document-emprise` uses a unique temporary file instead of `cnig_document_emprise_wkt.csv` next to the GeoJSON (shared by concurrent validations)
* v4.7.1 - XML validation : the XSD schemas (and their imports) are downloaded with timeouts (the JDK defaults are infinite : a validation could hang on a remote schema) and compiled once per URL instead of once per XML file, XML file closed after the validation
* v4.7.1 - PostgreSQL : maximum duration of a query with `DB_STATEMENT_TIMEOUT` (seconds, default 3600, `0` for no limit) : a query kept running on the server when validator-cli was killed (ex : timeout of validator-api)
* v4.7.1 - PostgreSQL : `DB_SCHEMA` must be a valid identifier and can't be a system schema (`DB_SCHEMA=PUBLIC` was accepted : unquoted names are case insensitive and the schema is dropped with `CASCADE` on restart), warning when `DB_SCHEMA` is not defined (shared schema `validation`)
* v4.7.1 - Logs : `DB_URL` is no longer written in `validator-debug.log` (returned to the users by validator-api) [SECURITY]
* v4.7.1 - ogr2ogr : the input driver is forced according to the extension (`-if`, GDAL >= 3.2) and the OGR VRT driver is disabled (`GDAL_SKIP=OGR_VRT`) : a ".gml", ".dbf" or ".gpkg" file containing an OGR VRT could read the files of the server or internal URLs (exposed in `DATA/*.csv` and the report). A file whose content doesn't match its extension (ex : CSV named ".dbf") is now rejected [SECURITY]
* v4.7.1 - GeoPackage multi-table files : table names are checked before the conversion (a table named `../../x` was written by ogr2ogr outside the conversion directory) [SECURITY]
* v4.7.1 - GML : no download of the WFS schema referenced by the user file (`GML_DOWNLOAD_WFS_SCHEMA=NO`, SSRF), the validation database credentials (`DB_*`, `PG*`) are not given to ogr2ogr [SECURITY]
* v4.7.1 - Metadata : linear check of the contact email (the regex took minutes on a long `gmd:electronicMailAddress`, `NullPointerException` when missing) [SECURITY]
* v4.7.1 - ogr2ogr is killed when validator-cli is stopped (ex : timeout of validator-api) instead of running up to `OGR2OGR_TIMEOUT`
* v4.7.1 - PostgreSQL : query results are fetched by batches of 1000 rows (`setFetchSize`) instead of being loaded in memory, SQL result iterators closed on errors (try-with-resources)
* v4.7.1 - Report : relative path instead of the absolute one in `TABLE_INVALID_ROW`
* v4.7.1 - Database : the column names of the FeatureTypes created from the data (`auto` table models, ex : PCRS) are no longer concatenated as is in `CREATE TABLE` (SQL injection with a crafted column name, ex : PostgreSQL validation database of validator-api) : invalid names are ignored with a warning, `Database.createTable` / `createIndex` reject them [SECURITY]
* v4.7.1 - Model : the names of the tables, attributes and static tables must be valid identifiers (letters, digits and `_`, used in SQL queries and in paths such as `DATA/{TABLE}.csv`), a remote model can only reference http(s) types and static tables (no `file:` URL) [SECURITY]
* v4.7.1 - No regex built from the attribute names (`FeatureType.indexOf`) nor polynomial regex on the SUP table names (CodeQL alerts), CI workflow with read only `permissions`
* v4.7.1 - Build : bump `maven-dependency-plugin` to 3.11.0, GitHub actions (`actions/checkout` v7, `actions/setup-java` v6, `docker/*` actions : Node 24), Renovate configuration migrated to `config:recommended` (`commons-cli` 2.0 pre-releases ignored)
* v4.7.0 - Bump GeoTools from 27.5 to 35.2 : `org.opengis` replaced by `org.geotools.api`, `DirectPosition` by `Position`. No output change on the CNIG / PCRS / DGPR test datasets (`document-info.json`, `validation.jsonl`, normalized data, reprojections in the 26 supported projections, with Java 17 and Java 21)
* v4.7.0 - CI : build and tests with Java 17 (minimum supported version) and Java 25 (Docker image)
* v4.7.0 - `validator-cli.jar` : 24.4 Mo to 26.3 Mo (newer EPSG database, `tools.jackson.core` required by GeoTools), Eclipse ImageN (image processing, replaces JAI in GeoTools) excluded from the jar

## 4.6.x

### v4.6.0 - Behavior changes

* v4.6.0 - Compare values with `=` instead of `LIKE` for foreign keys and DGPR controls (`_` and `%` were wildcards, case insensitive comparison with SQLITE)
* v4.6.0 - Fail the validation when ogr2ogr fails (exit code, stderr reported) instead of validating a partial output, add `OGR2OGR_TIMEOUT` (seconds, default 3600)
* v4.6.0 - DGPR : `DGPR_INOND_INCLUSION_ERROR` checks the inclusion in the union of the surfaces of each weaker scenario (Fort in Moyen and Faible, Moyen in Faible) instead of a single surface of the next scenario
* v4.6.0 - `document_geometry` : return exit code 1 on failure
* v4.6.0 - Deprecate `--dgpr-safe-simplify` (no effect, topology preserving simplification is always used)
* v4.6.2 - DGPR : add `--dgpr-skip-inclusion` (skip `DGPR_INOND_INCLUSION_*`) and `--dgpr-skip-graph-topology` (skip `DGPR_ISO_HT_*`, ex : `DGPR_ISO_HT_FUSION_NOT_SURFACE_INOND`) to disable these controls
* v4.6.0 - Report unavailable static tables (reference lists of the model, ex : csv removed from the server) as `MODEL_STATIC_TABLE_NOT_FOUND` instead of failing the validation (database validators are skipped)
* v4.6.4 - **Breaking change** : the conversion of the normalized CSV files to shapefile (`DATA/*.shp`, `DATA/*.dbf`, `DATA/*.vrt`, `DATA/*.cpg`) is removed (`CreateShapefilesPostProcess`, `VRT`, `FileConverter.convertToShapefile`), only `DATA/*.csv` is produced (#360)

### v4.6.4 - Features

* v4.6.4 - Docker : `Dockerfile` (ubuntu 24.04, Java 21, GDAL, GMLAS config, `fr_FR.UTF-8` locale for XML validation messages, non-root user), see `doc/docker.md` (#349)

### v4.6.0 - Fixes

* v4.6.5 - Metadata : reject DOCTYPE declarations in XML metadata files (XXE : external entities could read local files of the server and expose them in the metadata properties, CVE-2021-33813) [SECURITY]
* v4.6.5 - Logs : the tables of a multi-table file (ex : PCRS GML) missing from the data are logged as INFO instead of WARN (~25 WARN less per PCRS dataset, read errors still logged as WARN)
* v4.6.5 - `document-info.json` : no more `fail to compute stats` errors for the tables of a multi-table file (ex : PCRS GML) not found in the data
* v4.6.5 - CNIG : fix `DOC_URBA.csv` not filtered when the document name is not a valid DU name with CNIG v1 models (`NullPointerException` in `IdurbaFormatV1`, a partial `DOC_URBA.new.csv` was left in `DATA`)
* v4.6.5 - Logs : directories are skipped by the normalization without `ERROR` (`Normalization is not supported for ... (DirectoryModel)`), no more `ERROR` for the tables of a multi-table file not found in the data (`MultiTableReader`, ex : PCRS), clearer `converted to null (bad type)` warnings (`TABLE.ATTRIBUTE : 'value'`)
* v4.6.5 - Charset : a few invalid UTF-8 byte sequences (ex : text truncated in the middle of an accented character to fit a DBF field length) no longer lead to read the whole table as LATIN1 (double encoding of every accented character, ex : `ChÃ¢teau` for `Château`, propagated to `nomsuplitt` by `SupRelationsPostProcess`). `CharsetDetector.detectCharset` returns UTF-8 when valid multi-byte sequences are more frequent than invalid ones
* v4.6.5 - Charset : invalid byte sequences are removed from the values and reported as `ATTRIBUTE_INVALID_BYTE_SEQUENCE` (WARNING, with line and attribute) instead of `ATTRIBUTE_CHARACTERS_ILLEGAL` (ERROR with `--string-all LATIN1`). `TABLE_UNEXPECTED_ENCODING` is no longer reported when the detected charset is the declared one
* v4.6.4 - Metadata : `gmd:useLimitation` with `gmx:Anchor` : the `xlink:href` is exposed in the optional `useLimitationUrls` property (aligned with `useLimitations`, `useLimitations` is unchanged) (#22)
* v4.6.4 - Code lists : `.properties` files replaced by JSON arrays `[{"code":..., "description":...}]` in `codes/[NAME].json` (#124)
* v4.6.4 - Metadata : `METADATA_EXTENT_INVALID` also reported for longitudes outside [-180,180], latitudes outside [-90,90] and south latitude greater than north latitude (#34)
* v4.6.0 - DGPR : fix topology and inclusion controls (PostgreSQL) disabled without `--dgpr-simplify` (`ST_SimplifyPreserveTopology(geom, NULL)` returns NULL)
* v4.6.0 - DGPR : fix inclusion control matching ids by substring (ex : `SIN_1` considered as included when `SIN_10` is)
* v4.6.0 - DGPR : rollback to a savepoint when a PostGIS error occurs (the following controls were failing with "current transaction is aborted")
* v4.6.0 - Fix validation database support for PostgreSQL (driver detection, hash indexes for large values, `getCount`, SUP columns detection without `pragma_table_info`)
* v4.6.0 - Fix rows with missing values (`ArrayIndexOutOfBoundsException` while validating and loading the validation database) and values shifted by columns without name (rows aligned on the header, invalid rows reported as `TABLE_INVALID_ROW`)
* v4.6.0 - Fix JDBC leaks (statements of `Database.query()` and `DatabaseSUP`, connections not closed on failure and after `SupRelationsPostProcess`)
* v4.6.0 - Close the files and HTTP connections opened by `TableReader`
* v4.6.0 - Add network timeouts (30s to connect, 120s to read) for models and static tables (a server not responding was blocking the validation)
* v4.6.0 - Download remote static tables once per validation (`static_tables/{name}.csv` in the validation directory, they were read 3 times)
* v4.6.0 - Use `Locale.ROOT` for case conversions (ex : `IDURBA` was converted to `ıdurba` with a turkish locale)
* v4.6.0 - DGPR : fix `NullPointerException` in inclusion control without `--dgpr-tolerance`

### v4.6.0 - Dependencies and build

* v4.6.0 - Bump dependencies (Jackson 2.22.3, commons-io 2.22.0, commons-lang3 3.21.0, PostgreSQL 42.7.13, sqlite-jdbc 3.53.4.0, Maven plugins) and replace `commons-lang` 2.6 by `commons-lang3` (CVE-2025-48924) [SECURITY]
* v4.6.5 - Bump dependencies (Jackson 2.23.0, commons-lang3 3.22.0, PostgreSQL 42.7.14, sqlite-jdbc 3.54.1.0, maven-surefire-plugin 3.6.1, jacoco-maven-plugin 0.8.16)
* v4.6.5 - Replace `jdom` 1.0 by `jdom2` 2.0.6.1 and bump `jaxen` to 2.0.6 (metadata parsing)
* v4.6.5 - Build : exclude the unused GeoTools OGC XML models (EMF) from the Maven dependencies (EMF poms declare version ranges : metadata downloaded from every repository, slow builds and warnings behind a proxy)
* v4.6.6 - Tests : `FileConverterTest` compares the WKT of the GML and GeoJSON conversions with a tolerance (ogr2ogr may parse a 17 digits coordinate to the next double depending on the platform : CI failure of v4.6.5 on the GitHub ubuntu-26.04 runner)
* v4.6.5 - Tests : Mockito loaded as a Java agent by surefire (`maven-dependency-plugin:properties`, self-attaching will be disallowed by the JDK)
* v4.6.5 - Build : no more `maven-shade-plugin` overlapping resources warnings (dependencies manifests excluded, `LICENSE` texts concatenated, `NOTICE` files merged : the licenses of all the dependencies are kept in `validator-cli.jar`)
* v4.6.0 - Reduce `validator-cli.jar` size from 35.6 to 23.0 Mo (SQLite native libraries limited to Linux, Mac and Windows x86_64, unused GeoTools dependencies JAI and EMF excluded, `woodstox-core-asl` removed)
* v4.6.0 - CI : test the DGPR controls with PostGIS
* v4.6.0 - Remove unused files (obsolete `dependency-reduced-pom.xml` and `validator.sh`, unused classes and test resources)

## 4.5.x

* v4.5.6 - Add `ORIGINAL_PATH` column to normalized `TITRES_PIECES_ECRITES.csv` (relative path under `Pieces_ecrites`)
* v4.5.4 - Bump Jackson (FasterXML) to 2.21.4 [SECURITY]
* v4.5.4 - Bump commons-lang3 to 3.18.0 (override GeoTools transitive) [SECURITY]
* v4.5.4 - Add `make trivy` target to scan Maven dependencies for vulnerabilities
* v4.5.4 - Reduce build warnings (compiler, shade plugin, surefire)
* v4.5.3 - Improve deb/rpm build scripts (fallback without fpm/rpmbuild)
* v4.5.2 - Add `typeproc` document tag from DOC_URBA (`TYPEPROC` for SCOT, `NOMPROC` for PLU, PLUi, CC and PSMV)
* v4.5.2 - Bump log4j-core to 2.25.4 [SECURITY]
* v4.5.2 - Bump postgresql to 42.7.11 [SECURITY]

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


* Metadata validation (see [doc/metadata.md](doc/metadata.md))

    * Improve metadata parsing 
    * Validate INSPIRE constraints (most of them)
    * Validate CNIG constraints (additional constraints for https://www.geoportail-urbanisme.gouv.fr)

* Improve error reporting

    * Simplify report generation (copy Context informations to ValidatorError)
    * Add [JSONL](http://jsonlines.org/) report format (each line is a ValidatorError serialized in JSON)

