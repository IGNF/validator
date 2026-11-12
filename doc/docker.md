# Image Docker

Le `Dockerfile` à la racine construit une image (ubuntu 24.04) contenant Java, GDAL (`ogr2ogr`), le jar `validator-cli.jar` et la configuration GMLAS.

```bash
docker build -t validator .
# derrière un proxy :
docker build -t validator --build-arg http_proxy=$http_proxy --build-arg https_proxy=$https_proxy --build-arg MAVEN_OPTS="-DskipTests -Dhttp.proxyHost=proxy -Dhttp.proxyPort=3128 -Dhttps.proxyHost=proxy -Dhttps.proxyPort=3128" .

docker run --rm -v "$PWD/data:/data" validator --help
```

* `ENTRYPOINT` : `java -jar /opt/validator/validator-cli.jar`
* `VALIDATOR_PATH` : chemin du jar dans l'image
* `GMLAS_CONFIG` : configuration GMLAS pour la validation XML (PCRS)
* `LANG`, `LC_ALL` : `fr_FR.UTF-8` (comme [IGNF/validator-api](https://github.com/IGNF/validator-api)), pour des messages de validation XML (`cvc-...`) en français. Pour l'anglais : `-e LANG=C.UTF-8 -e LC_ALL=C.UTF-8`.
* L'image s'exécute avec l'utilisateur `ubuntu` (uid 1000), le répertoire de travail est `/data`.
* Arguments de build : `REGISTRY`, `UBUNTU_IMAGE_VERSION` (24.04), `OPENJDK_VERSION` (21).
