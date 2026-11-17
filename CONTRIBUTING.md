# Contributing

## Source formatting

Please ensure that you run `mvn formatter:format` before commit.

It relies on the following configuration file [.mvn/formatter-config.xml](.mvn/formatter-config.xml).

To configure eclipse to use this code formatter, see "Preferences window, go to Java -> Code Style -> Formatter -> Import".

## Continuous Integration (CI)

Note that CI is configured throw [GitHub Actions](https://github.com/IGNF/validator/actions/workflows/main.yml).

It relies on [.github/workflows/main.yml](.github/workflows/main.yml) which :

* Install Java (matrix : Java 17, the minimum supported version, and Java 25, the version of the Docker image)
* Install ogr2ogr (GDAL from the distribution, ubuntu 26.04)
* Run tests through [.ci/build.sh](.ci/build.sh)
* Run the DGPR tests with a PostGIS service

You may run `bash .ci/build.sh` to ensure that source code is formatted, build code and run tests.

## Create releases

* Ensure that you are on the right branch and that all code is committed and push (`git status`)
* Run `mvn release:prepare` and follow instructions to automatically create tags (`vX.Y.Z`) and update version in pom.xml files. Nothing is pushed (`pushChanges=false` in pom.xml) : `git push origin master && git push origin vX.Y.Z`
* Build JAR and packages for the new tag

```bash
git checkout vX.Y.Z
make package
```

* Create release from github tag with the following binary files : `validator-cli/target/validator-cli.jar`, `validator-cli/target/ign-validator_{VERSION}_all.deb` and `validator-cli/target/ign-validator-{VERSION}-1.noarch.rpm`

The Docker image `ghcr.io/ignf/validator` (tags `X.Y.Z`, `X.Y` and `latest`) is published by [.github/workflows/docker.yml](.github/workflows/docker.yml) when the tag is pushed.

See [doc/developpement.md](doc/developpement.md) for the detailed procedure.



