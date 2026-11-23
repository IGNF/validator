# Développement

Ce document décrit l'installation de l'environnement de développement, la compilation et les contrôles du projet. La publication d'une version est décrite dans [release.md](release.md).

## Prérequis

| Outil | Version | Usage | Installation (Debian/Ubuntu) |
|-------|---------|-------|------------------------------|
| JDK | 21 ou supérieur | compilation et exécution | `sudo apt-get install openjdk-21-jdk-headless` |
| Maven | 3.6.3 ou supérieur | build | `sudo apt-get install maven` |
| ogr2ogr (GDAL) | 2.3.0 ou supérieur | exécution **et tests** | `sudo apt-get install gdal-bin` (voir [ogr2ogr.md](dependencies/ogr2ogr.md)) |

Un **JDK complet** est nécessaire, pas seulement un JRE. Avec `openjdk-21-jre` seul, la compilation échoue avec `release version 21 not supported`, car le fichier `lib/ct.sym` utilisé par `<release>` manque.

```bash
java -version
mvn -version      # doit afficher "Java version: 21" ou supérieur
ogr2ogr --version
```

La version Java cible est définie par `java.version` dans le [pom.xml](../pom.xml). Elle doit rester alignée sur la version Java de gpu-site.

## Build

Le [Makefile](../Makefile) fournit les commandes principales :

| Commande | Effet |
|----------|-------|
| `make test` | `mvn clean package` : compilation et tests |
| `make build` | compilation sans tests |
| `make package` | compilation sans tests, puis paquets `.deb` et `.rpm` |
| `make deb` / `make rpm` | un seul des deux paquets |
| `make clean` | `mvn clean` |

Fichiers produits :

| Fichier | Description |
|---------|-------------|
| `validator-cli/target/validator-cli.jar` | exécutable Java (uber-jar) |
| `validator-cli/target/ign-validator_{VERSION}_all.deb` | paquet Debian |
| `validator-cli/target/ign-validator-{VERSION}-1.noarch.rpm` | paquet RPM |

Pour vérifier la version compilée : `java -jar validator-cli/target/validator-cli.jar version`.

### Paquets .deb et .rpm (optionnel)

| Outil | Usage | Installation |
|-------|-------|--------------|
| `fpm` | construction des paquets `.deb` et `.rpm` | `sudo apt-get install ruby ruby-dev build-essential && sudo gem install fpm` |
| `dpkg-deb` + `fakeroot` | `.deb` sans `fpm` | `sudo apt-get install dpkg-dev fakeroot` |
| `rpmbuild` | requis par `fpm` pour le `.rpm` | `sudo apt-get install rpm` |

Si `fpm` est absent, `build-deb.sh` utilise `dpkg-deb`, et `build-rpm.sh` télécharge `rpmbuild` dans `.tools/rpm-pkg/`.

## Formatage du code

Le code doit être formaté avant chaque commit, sinon la CI échoue :

```bash
mvn formatter:format
```

La configuration est dans [.mvn/formatter-config.xml](../.mvn/formatter-config.xml). Pour reproduire la CI en local (formatage, puis build et tests) :

```bash
bash .ci/build.sh
```

## Intégration continue

| Workflow | Déclenchement | Contrôle |
|----------|---------------|----------|
| [main.yml](../.github/workflows/main.yml) | push, pull request | formatage, build et tests avec Java 21 (version minimale) et 25 (version de l'image Docker), avec un service PostGIS pour les tests DGPR |
| [osv.yml](../.github/workflows/osv.yml) | chaque lundi, pull request modifiant un `pom.xml` | dépendances contrôlées contre la base de vulnérabilités [OSV](https://osv.dev) ; en local : `bash .ci/osv-check.sh` |
| [docker.yml](../.github/workflows/docker.yml) | push sur `master`, tag `vX.Y.Z` | image `ghcr.io/ignf/validator` |

Renovate propose les mises à jour des dépendances par pull request.

## Listes de codes

Les listes de codes (langues, charsets, scopes, types géométriques d'erreur, etc.) sont des fichiers JSON `validator-core/src/main/resources/codes/[NOM].json`. Chaque fichier contient un tableau d'objets ayant au moins une propriété `code`, et optionnellement `description`. Elles sont chargées par `CodeList.getCodeList("NOM")`.

## Voir aussi

- [release.md](release.md) : publication d'une version
- [CONTRIBUTING.md](../CONTRIBUTING.md) : formatage du code et CI
- [cli.md](cli.md) : utilisation du validateur en ligne de commande
- [docker.md](docker.md) : image Docker
