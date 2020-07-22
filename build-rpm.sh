#!/bin/bash

hash fpm 2>/dev/null || { echo >&2 "ERROR : fpm is required to build rpm package (see https://fpm.readthedocs.io/en/latest/installing.html)"; exit 1; }

cd validator-cli/target

VERSION=$(java -jar validator-cli.jar version)
echo "VERSION=$VERSION"

rm -rf *.rpm
fpm -s dir -t rpm -n ign-validator -v $VERSION \
    --architecture all \
    --description "IGNF/validator - validate and load data according to models" \
    --url "https://github.com/IGNF/validator#validator" \
    --license "Cecill-B" \
    --vendor "IGNF" \
    --maintainer "MBorne@users.noreply.github.com" \
    --prefix /opt/ign-validator validator-cli.jar
