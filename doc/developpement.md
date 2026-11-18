# Développement, build et release

Ce document décrit l'installation des dépendances, la compilation du projet et la procédure de publication (push, tag et release GitHub).

## Prérequis

### Java

Le projet compile pour **Java 17** (voir `java.version` dans `pom.xml`) et requiert **Java 17 ou supérieur** pour l'exécution (voir [README.md](../README.md)). Un JDK (et non un JRE) est nécessaire pour compiler (paquet `openjdk-17-jdk-headless` ou supérieur).

```bash
java -version
```

Sous Debian/Ubuntu :

```bash
sudo apt-get install openjdk-17-jdk
```

La CI GitHub Actions (ubuntu-26.04) teste **Java 17** (version minimale) et **Java 25** (version de l'image Docker).

Les dépendances sont aussi contrôlées contre la base de vulnérabilités [OSV](https://osv.dev) par [.github/workflows/osv.yml](../.github/workflows/osv.yml) (chaque lundi, et quand un `pom.xml` change). En local : `bash .ci/osv-check.sh`. Renovate propose les mises à jour de versions.

### Maven

Maven 3.6+ est requis pour compiler le projet.

```bash
mvn -version
```

Sous Debian/Ubuntu :

```bash
sudo apt-get install maven
```

### ogr2ogr (GDAL)

**Requis pour l'exécution** du validateur et **pour les tests**. Version minimale : **2.3.0**.

```bash
ogr2ogr --version
```

Sous Debian/Ubuntu :

```bash
sudo apt-get update
sudo apt-get install gdal-bin
```

Voir aussi [ogr2ogr.md](dependencies/ogr2ogr.md).

### Dépendances optionnelles (paquets .deb / .rpm)

Les paquets Debian et RPM sont produits par les scripts `build-deb.sh` et `build-rpm.sh`.

| Outil | Usage | Installation |
|-------|-------|--------------|
| `fpm` | Construction des paquets .deb et .rpm | `sudo apt-get install ruby ruby-dev build-essential && sudo gem install fpm` |
| `dpkg-deb` + `fakeroot` | Repli pour le .deb si `fpm` est absent | `sudo apt-get install dpkg-dev fakeroot` |
| `rpmbuild` | Requis par `fpm` pour le .rpm | `sudo apt-get install rpm` |

> **Note** : si `fpm` n'est pas installé, `build-deb.sh` utilise `dpkg-deb` en repli. Pour le RPM, `build-rpm.sh` télécharge automatiquement `rpmbuild` dans `.tools/rpm-pkg/` si l'outil système est absent.

### GitHub CLI (optionnel)

Utile pour créer la release GitHub et y attacher les binaires :

```bash
sudo apt-get install gh
gh auth login
```

## Cloner le dépôt

```bash
git clone https://github.com/IGNF/validator.git
cd validator
```

## Build

Le [Makefile](../Makefile) fournit les commandes principales.

### Lancer les tests

Compile le projet et exécute les tests unitaires :

```bash
make test
# équivalent à : mvn clean package
```

### Compiler sans tests

```bash
make build
# équivalent à : mvn clean package -Dmaven.test.skip=true
```

### Produire les paquets (.deb et .rpm)

```bash
make package
# compile (sans tests) puis exécute build-deb.sh et build-rpm.sh
```

Commandes individuelles :

```bash
make deb    # JAR + paquet .deb
make rpm    # JAR + paquet .rpm
make clean  # mvn clean
```

### Formater le code source

Avant chaque commit, le code doit être formaté :

```bash
mvn formatter:format
```

La CI vérifie le formatage via :

```bash
bash .ci/build.sh
```

Ce script exécute `mvn formatter:validate` puis `mvn clean package` (avec tests).

### Artefacts produits

| Fichier | Description |
|---------|-------------|
| `validator-cli/target/validator-cli.jar` | Exécutable Java (uber-jar) |
| `validator-cli/target/ign-validator_{VERSION}_all.deb` | Paquet Debian |
| `validator-cli/target/ign-validator-{VERSION}-1.noarch.rpm` | Paquet RPM |

Vérifier la version compilée :

```bash
java -jar validator-cli/target/validator-cli.jar version
```

## Publication d'une version

Versions : `X.Y.Z-SNAPSHOT` en développement, `X.Y.Z` en release, tag Git `vX.Y.Z` (ex : `4.7.1-SNAPSHOT`, `4.7.1`, `v4.7.1`).

### Avant de commencer

1. Être sur la branche `master` à jour, avec un dépôt propre (`git status`).
2. Mettre à jour [CHANGELOG.md](../CHANGELOG.md) avec les changements de la version.
3. S'assurer que les tests passent : `make test`.

### Étapes

```bash
# 1. fichiers laissés par une release précédente (sinon release:prepare reprend l'ancienne release)
rm -f release.properties pom.xml.releaseBackup

# 2. version de release (ex : 4.7.1), tag (v4.7.1) et version suivante (ex : 4.7.2-SNAPSHOT)
#    deux commits "[maven-release-plugin] ..." et le tag sont créés, rien n'est poussé (pushChanges=false)
mvn release:prepare

# 3. pousser : le tag déclenche la publication de l'image Docker ghcr.io/ignf/validator
#    (tags X.Y.Z, X.Y et latest, voir .github/workflows/docker.yml)
git push origin master
git push origin vX.Y.Z

# 4. binaires construits depuis le tag : validator-cli.jar, .deb et .rpm dans validator-cli/target/
git checkout vX.Y.Z
make package
git checkout master

# 5. release GitHub (ou via https://github.com/IGNF/validator/releases : "Draft a new release")
gh release create vX.Y.Z --title "vX.Y.Z" --notes "voir CHANGELOG.md" \
  validator-cli/target/validator-cli.jar validator-cli/target/*.deb validator-cli/target/*.rpm

# 6. nettoyage
rm -f release.properties pom.xml.releaseBackup
```

### En cas de problème

* **`release:prepare` interrompu** (ex : tag déjà existant) : vérifier l'état (`git log --oneline -5`, `git tag -l 'vX.Y.*'`, `grep '<version>' pom.xml`), puis reprendre avec `mvn release:prepare -Dresume=true` si `release.properties` est cohérent, ou terminer à la main.
* **Release à la main** : `mvn versions:set -DnewVersion=X.Y.Z -DgenerateBackupPoms=false`, `<scm><tag>vX.Y.Z</tag></scm>` dans `pom.xml`, commit `[maven-release-plugin] prepare release vX.Y.Z` et tag annoté `vX.Y.Z` ; puis `mvn versions:set -DnewVersion=X.Y.(Z+1)-SNAPSHOT -DgenerateBackupPoms=false`, `<scm><tag>v4.6.1</tag></scm>` (valeur conservée entre les releases) et commit `[maven-release-plugin] prepare for next development iteration`.

## Listes de codes

Les listes de codes (langues, charsets, scopes, types géométriques d'erreur, etc.) sont des fichiers JSON `validator-core/src/main/resources/codes/[NOM].json` : un tableau d'objets ayant au moins une propriété `code` (et optionnellement `description`). Elles sont chargées par `CodeList.getCodeList("NOM")`.

## Voir aussi

- [CONTRIBUTING.md](../CONTRIBUTING.md) — formatage du code et CI
- [cli.md](cli.md) — utilisation du validateur en ligne de commande
- [ogr2ogr.md](dependencies/ogr2ogr.md) — installation de GDAL
