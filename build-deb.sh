#!/bin/bash
set -euo pipefail

cd validator-cli/target

VERSION=$(java -jar validator-cli.jar version)
echo "VERSION=$VERSION"

rm -rf *.deb

if hash fpm 2>/dev/null; then
	fpm -s dir -t deb -n ign-validator -v "$VERSION" \
		--architecture all \
		--description "IGNF/validator - validate and load data according to models" \
		--url "https://github.com/IGNF/validator#validator" \
		--license "Cecill-B" \
		--vendor "IGNF" \
		--maintainer "MBorne@users.noreply.github.com" \
		--prefix /opt/ign-validator validator-cli.jar
	exit 0
fi

if ! hash dpkg-deb 2>/dev/null; then
	echo >&2 "ERROR : fpm or dpkg-deb is required to build deb package"
	echo >&2 "Install fpm: https://fpm.readthedocs.io/en/latest/installing.html"
	exit 1
fi

PKG_ROOT=$(mktemp -d)
trap 'rm -rf "$PKG_ROOT"' EXIT

mkdir -p "$PKG_ROOT/DEBIAN" "$PKG_ROOT/opt/ign-validator"
cp validator-cli.jar "$PKG_ROOT/opt/ign-validator/"

cat > "$PKG_ROOT/DEBIAN/control" <<EOF
Package: ign-validator
Version: $VERSION
Architecture: all
Maintainer: MBorne@users.noreply.github.com
Description: IGNF/validator - validate and load data according to models
 Vendor: IGNF
 Homepage: https://github.com/IGNF/validator#validator
EOF

DEB_FILE="ign-validator_${VERSION}_all.deb"
fakeroot dpkg-deb --build "$PKG_ROOT" "$DEB_FILE"
echo "Built $DEB_FILE (without fpm, using dpkg-deb)"
