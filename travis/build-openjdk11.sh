#!/bin/bash

echo "-----------------------------------------------------------------------"
echo "-- Display informations"
echo "-----------------------------------------------------------------------"

export OGR2OGR_PATH=${OGR2OGR_PATH:-ogr2ogr}
echo "-- OGR2OGR_PATH : ${OGR2OGR_PATH}"
echo "-- GDAL_VERSION : $($OGR2OGR_PATH --version)"

echo ""

echo "-----------------------------------------------------------------------"
echo "-- Run 'mvn formatter:validate' to ensure that code is formatted"
echo "-----------------------------------------------------------------------"
mvn formatter:validate || {
	echo "please run 'mvn formatter:format' before commiting"
	exit 1
}

echo ""

echo "-----------------------------------------------------------------------"
echo "-- Run 'mvn clean package' to build running unit tests"
echo "-----------------------------------------------------------------------"

mvn clean package



