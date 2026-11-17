# Image Docker

Le `Dockerfile` à la racine construit une image (ubuntu 26.04) contenant Java, GDAL (`ogr2ogr`), le jar `validator-cli.jar` et la configuration GMLAS.

```bash
docker build -t validator .
# derrière un proxy (utilisé par apt et converti en options java pour maven) :
docker build -t validator --build-arg http_proxy=$http_proxy --build-arg https_proxy=$https_proxy --build-arg no_proxy=$no_proxy .

docker run --rm -v "$PWD/data:/data" validator --help
```

L'image est publiée sur GitHub Container Registry à chaque version (tags `X.Y.Z`, `X.Y` et `latest`) et à chaque push sur `master` (tag `master`) :

```bash
docker pull ghcr.io/ignf/validator:latest
```

Pour ne pas passer le proxy à chaque build, il peut être déclaré une fois pour toutes dans `~/.docker/config.json` (transmis automatiquement à `docker build`) :

```json
{
  "proxies": {
    "default": {
      "httpProxy": "http://proxy:3128",
      "httpsProxy": "http://proxy:3128",
      "noProxy": "localhost,127.0.0.1"
    }
  }
}
```

* `ENTRYPOINT` : `java -jar /opt/validator/validator-cli.jar`
* `VALIDATOR_PATH` : chemin du jar dans l'image
* `GMLAS_CONFIG` : configuration GMLAS pour la validation XML (PCRS)
* `LANG`, `LC_ALL` : `fr_FR.UTF-8` (comme [IGNF/validator-api](https://github.com/IGNF/validator-api)), pour des messages de validation XML (`cvc-...`) en français. Pour l'anglais : `-e LANG=C.UTF-8 -e LC_ALL=C.UTF-8 -e LANGUAGE=`. Le fuseau horaire est `TZ=Europe/Paris`.
* L'image s'exécute avec l'utilisateur `ubuntu` (uid 1000), le répertoire de travail est `/data`.
* Arguments de build : `REGISTRY`, `UBUNTU_IMAGE_VERSION` (26.04), `OPENJDK_VERSION` (25), `MAVEN_OPTS` (`-DskipTests`), `http_proxy`, `https_proxy`, `no_proxy`.
