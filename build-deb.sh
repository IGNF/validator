#!/bin/bash

hash fpm 2>/dev/null || { echo >&2 "ERROR : fpm is required to build deb package (see https://fpm.readthedocs.io/en/latest/installing.html)"; exit 1; }

cd validator-cli/target

VERSION=$(java -jar validator-cli.jar version)
echo "VERSION=$VERSION"

rm -rf *.deb
fpm -s dir -t deb -n ign-validator -v $VERSION \
    --license "Cecill-B" \
    --description "IGNF/validator - validate and load data according to models" \
    --prefix /opt/ign-validator validator-cli.jar