FROM ubuntu:24.04 AS builder

RUN apt-get update \
&& apt-get install -y openjdk-11-jdk maven \
&& apt-get install -y gdal-bin \
&& rm -rf /var/lib/apt/lists/*

WORKDIR /usr/src/validator
COPY validator-cli/ validator-cli/
COPY validator-core/ validator-core/
COPY validator-plugin-cnig/ validator-plugin-cnig
COPY validator-plugin-dgpr/ validator-plugin-dgpr
COPY validator-plugin-pcrs/ validator-plugin-pcrs
COPY pom.xml .

# --build-arg MAVEN_OPTS="-DskipTests -Dhttp.proxyHost=proxy -Dhttp.proxyPort=3128 -Dhttps.proxyHost=proxy -Dhttps.proxyPort=3128"
ARG MAVEN_OPTS="-DskipTests"
RUN mvn clean package ${MAVEN_OPTS}

FROM ubuntu:24.04 AS runtime

RUN apt-get update \
 && apt-get install -y openjdk-11-jre gdal-bin \
 && rm -rf /var/lib/apt/lists/*

WORKDIR /opt/validator
ENV VALIDATOR_PATH=/opt/validator/validator-cli.jar
COPY --from=builder /usr/src/validator/validator-cli/target/validator-cli.jar .

# change locale for XML validation message

# get adapted configuration for GMLAS driver
COPY validator-core/src/main/resources/gdal/gmlasconf-validator.xml /opt/validator/gmlasconf-validator.xml
ENV GMLAS_CONFIG=/opt/validator/gmlasconf-validator.xml

# TODO : test resulting image (with mborne/validator-experiments)

# switch to ubuntu user (uid=1000, gid=1000)
#USER ubuntu
