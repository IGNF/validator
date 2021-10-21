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

.PHONY: build
build:
	mvn clean package -Dmaven.test.skip=true

.PHONY: sonar
sonar: coverage
	mvn sonar:sonar

.PHONY: coverage
coverage:
	mvn clean package
	mvn jacoco:report

.PHONY: clean
clean:
	mvn clean


