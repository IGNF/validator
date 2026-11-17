# Contributing

## Source formatting

Please ensure that you run `mvn formatter:format` before commit.

It relies on the following configuration file [.mvn/formatter-config.xml](.mvn/formatter-config.xml).

To configure eclipse to use this code formatter, see "Preferences window, go to Java -> Code Style -> Formatter -> Import".

## Continuous Integration (CI)

Note that CI is configured through [GitHub Actions](https://github.com/IGNF/validator/actions/workflows/main.yml).

It relies on [.github/workflows/main.yml](.github/workflows/main.yml) which :

* Install Java (matrix : Java 17, the minimum supported version, and Java 25, the version of the Docker image)
* Install ogr2ogr (GDAL from the distribution, ubuntu 26.04)
* Run tests through [.ci/build.sh](.ci/build.sh)
* Run the DGPR tests with a PostGIS service

You may run `bash .ci/build.sh` to ensure that source code is formatted, build code and run tests.

## Create releases

See [doc/developpement.md](doc/developpement.md#publication-dune-version) (release with `mvn release:prepare`, binaries, GitHub release, Docker image).
