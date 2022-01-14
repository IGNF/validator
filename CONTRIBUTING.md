# Contributing

## Source formatting

Please ensure that you run `mvn formatter:format` before commit.

It relies on the following configuration file [eclipse/formatter-config.xml](eclipse/formatter-config.xml).

To configure eclipse to use this code formatter, see "Preferences window, go to Java -> Code Style -> Formatter -> Import".

## Continuous Integration (CI)

Note that CI is configured throw [GitHub Actions](https://github.com/IGNF/validator/actions/workflows/main.yml).

It relies on [.github/workflows/main.yml](.github/workflows/main.yml) which :

* Install OpenJDK 11 and maven
* Install ogr2ogr
* Run tests throw [.ci/build-openjdk11.sh](.ci/build-openjdk11.sh)

You may run `bash .ci/build-openjdk11.sh` to ensure that source code is formatted, build code and run tests.

## Create releases

* Ensure that you are on the good branch and that all code is committed and push (`git status`)
* Run `mvn release:prepare` and follow instructions to automatically create tags and update version in pom.xml files
* Build JAR and packages for the new tag

```bash
git checkout v4.2.5
make package
```

* Create release from github tag with the following binary files : `validator-cli/target/validator-cli.jar`, `ign-validator_{VERSION}_all.deb` and `ign-validator-{VERSION}-1.noarch.rpm`



