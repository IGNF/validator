NAME=ign-validator

TRIVY_VERSION ?= 0.72.0
TRIVY := .tools/trivy

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

$(TRIVY):
	mkdir -p .tools
	curl -sfL "https://github.com/aquasecurity/trivy/releases/download/v$(TRIVY_VERSION)/trivy_$(TRIVY_VERSION)_Linux-64bit.tar.gz" \
		| tar xz -C .tools trivy

.PHONY: trivy
trivy: $(TRIVY)
	mvn install -Dmaven.test.skip=true -q
	$(TRIVY) fs --download-db-only --quiet pom.xml
	@echo "=== Scan Maven dependencies (pom.xml) ==="
	$(TRIVY) fs --scanners vuln --severity HIGH,CRITICAL,MEDIUM pom.xml

.PHONY: sonar
sonar: coverage
	mvn sonar:sonar

.PHONY: coverage
coverage:
	mvn clean package -Dmaven.test.failure.ignore=true
	mvn jacoco:report

.PHONY: clean
clean:
	mvn clean


