ARG REGISTRY=docker.io
ARG UBUNTU_IMAGE_VERSION=24.04
ARG OPENJDK_VERSION=21

#----------------------------------------------------------------------
# Build stage - validator-cli.jar
#----------------------------------------------------------------------
FROM ${REGISTRY}/library/ubuntu:${UBUNTU_IMAGE_VERSION} AS builder

ARG OPENJDK_VERSION
ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update -qq \
 && apt-get install --no-install-recommends -y \
    ca-certificates maven gdal-bin openjdk-${OPENJDK_VERSION}-jdk-headless \
 && rm -rf /var/lib/apt/lists/*

WORKDIR /usr/src/validator
COPY pom.xml .
COPY validator-cli/ validator-cli/
COPY validator-core/ validator-core/
COPY validator-plugin-cnig/ validator-plugin-cnig/
COPY validator-plugin-dgpr/ validator-plugin-dgpr/
COPY validator-plugin-pcrs/ validator-plugin-pcrs/

# Behind a proxy : --build-arg MAVEN_OPTS="-DskipTests -Dhttp.proxyHost=proxy -Dhttp.proxyPort=3128 -Dhttps.proxyHost=proxy -Dhttps.proxyPort=3128"
ARG MAVEN_OPTS="-DskipTests"
RUN mvn clean package ${MAVEN_OPTS}

#----------------------------------------------------------------------
# Runtime stage
#----------------------------------------------------------------------
FROM ${REGISTRY}/library/ubuntu:${UBUNTU_IMAGE_VERSION}

ARG OPENJDK_VERSION

LABEL description="validator-cli - validate and normalize datasets according to a document model." \
      org.opencontainers.image.source="https://github.com/IGNF/validator"

# The French locale (as in IGNF/validator-api) is used by the JVM to translate
# XML validation messages (PCRS). Override with `-e LANG=C.UTF-8 -e LC_ALL=C.UTF-8` for english messages.
ENV DEBIAN_FRONTEND=noninteractive \
    LANG=fr_FR.UTF-8 LC_ALL=fr_FR.UTF-8 LANGUAGE=fr_FR.UTF-8 \
    TZ=Europe/Paris \
    VALIDATOR_PATH=/opt/validator/validator-cli.jar \
    GMLAS_CONFIG=/opt/validator/gmlasconf-validator.xml

# locale, trust store, java and ogr2ogr (required by validator-cli.jar)
RUN apt-get update -qq \
 && apt-get install --no-install-recommends -y \
    locales ca-certificates openjdk-${OPENJDK_VERSION}-jre-headless gdal-bin \
 && echo "fr_FR.UTF-8 UTF-8" > /etc/locale.gen \
 && locale-gen fr_FR.UTF-8 \
 && update-locale LANG=fr_FR.UTF-8 LC_ALL=fr_FR.UTF-8 \
 && update-ca-certificates \
 && java -version \
 && ogrinfo --version \
 && apt-get autoremove -y \
 && apt-get clean \
 && rm -rf /var/lib/apt/lists/*

WORKDIR /opt/validator
COPY --from=builder /usr/src/validator/validator-cli/target/validator-cli.jar ${VALIDATOR_PATH}
# adapted configuration for the GMLAS driver
COPY validator-core/src/main/resources/gdal/gmlasconf-validator.xml ${GMLAS_CONFIG}

# Security : run as non-root user (ubuntu, uid=1000), ogr2ogr needs to write in $HOME/.gdal
ENV HOME=/home/ubuntu
USER ubuntu
WORKDIR /data

ENTRYPOINT ["java", "-jar", "/opt/validator/validator-cli.jar"]
CMD ["--help"]
