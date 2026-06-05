#!/bin/bash
set -euo pipefail

setup_rpmbuild() {
	if hash rpmbuild 2>/dev/null; then
		return 0
	fi

	local script_dir rpm_pkg_dir rpm_root
	script_dir=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
	rpm_pkg_dir="$script_dir/.tools/rpm-pkg"
	rpm_root="$rpm_pkg_dir/extracted"

	if [[ ! -x "$rpm_root/usr/bin/rpmbuild" ]]; then
		mkdir -p "$rpm_pkg_dir"
		(
			cd "$rpm_pkg_dir"
			apt-get download rpm librpm9t64 librpmbuild9t64 librpmio9t64 librpmsign9t64 \
				rpm-common rpm2cpio debugedit liblua5.3-0
			rm -rf extracted
			mkdir extracted
			for deb in *.deb; do
				dpkg-deb -x "$deb" extracted
			done
		)
	fi

	export PATH="$rpm_root/usr/bin:$PATH"
	export LD_LIBRARY_PATH="$rpm_root/usr/lib/x86_64-linux-gnu${LD_LIBRARY_PATH:+:$LD_LIBRARY_PATH}"
	export RPM_CONFIGDIR="$rpm_root/usr/lib/rpm"
}

hash fpm 2>/dev/null || {
	echo >&2 "ERROR : fpm is required to build rpm package (see https://fpm.readthedocs.io/en/latest/installing.html)"
	exit 1
}

setup_rpmbuild

cd validator-cli/target

VERSION=$(java -jar validator-cli.jar version)
echo "VERSION=$VERSION"

rm -rf *.rpm
fpm -s dir -t rpm -n ign-validator -v "$VERSION" \
	--architecture all \
	--description "IGNF/validator - validate and load data according to models" \
	--url "https://github.com/IGNF/validator#validator" \
	--license "Cecill-B" \
	--vendor "IGNF" \
	--maintainer "MBorne@users.noreply.github.com" \
	--prefix /opt/ign-validator validator-cli.jar
