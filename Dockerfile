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

RUN mvn clean package -DskipTests

FROM ubuntu:24.04 AS runtime

RUN apt-get update \
 && apt-get install -y openjdk-11-jre gdal-bin \
 && rm -rf /var/lib/apt/lists/*

# TODO : create dedicated user (run-as-non-root) : uid=1000, gid=1000
# TODO : config GMLAS
# TODO : prepare volumes
# TODO : test resulting image

WORKDIR /opt/validator
COPY --from=builder /usr/src/validator/validator-cli/target/validator-cli-*.jar ./validator-cli.jar

