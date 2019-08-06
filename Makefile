NAME=ign-validator

all: build

.PHONY: package
package: deb rpm

.PHONY: deb
deb: build
	bash build-deb.sh

.PHONY: rpm
rpm: build
	bash build-rpm.sh

.PHONY: test
test:
	mvn clean package

.PHONY: test-pg
test-pg:
	dropdb validator-test || true
	createdb validator-test
	psql -d validator-test -c "CREATE EXTENSION postgis"
	DB_URL=jdbc:postgresql:validator-test mvn clean package

.PHONY: build
build:
	mvn clean package -Dmaven.test.skip=true

.PHONY: clean
clean:
	mvn clean


