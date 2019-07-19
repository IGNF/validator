#!/bin/bash

cd validator-cli/target

VERSION=$(java -jar validator-cli.jar version)
echo "VERSION=$VERSION"

rm -rf *.deb
fpm -s dir -t deb -n ign-validator -v $VERSION \
    --license "Cecill-B" \
    --description "IGNF/validator - validate and load data according to models" \
    --prefix /opt/ign-validator validator-cli.jar
