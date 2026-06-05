# Développement, build et release

Ce document décrit l'installation des dépendances, la compilation du projet et la procédure de publication (push, tag et release GitHub).

## Prérequis

### Java

Le projet compile avec **Java 11** (voir `java.version` dans `pom.xml`). L'exécution du validateur requiert **Java 17 ou supérieur** (voir [README.md](../README.md)).

```bash
java -version
```

Sous Debian/Ubuntu :

```bash
sudo apt-get install openjdk-17-jdk
```

La CI GitHub Actions utilise actuellement **Java 21**.

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
bash .ci/build-openjdk11.sh
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

## Publication : push, tag et release

### Avant de commencer

1. Être sur la branche `master` à jour.
2. Vérifier que le dépôt est propre : `git status`.
3. Mettre à jour [CHANGELOG.md](../CHANGELOG.md) avec les changements de la version à publier.
4. S'assurer que les tests passent : `make test`.

### Convention de versionnement

Le projet suit le versionnement sémantique Maven :

- En développement : `X.Y.Z-SNAPSHOT` (ex. `4.5.4-SNAPSHOT`)
- En release : `X.Y.Z` (ex. `4.5.4`)
- Tags Git : `vX.Y.Z` (ex. `v4.5.4`)

### Méthode recommandée : `mvn release:prepare`

Le plugin Maven Release automatise la création du tag et la mise à jour des `pom.xml` :

```bash
mvn release:prepare
```

Le plugin demande interactivement :

1. La version de release (ex. `4.5.4`)
2. Le nom du tag (par défaut `v4.5.4`, format configuré dans `pom.xml`)
3. La version de développement suivante (ex. `4.5.5-SNAPSHOT`)

Il crée ensuite **deux commits** :

1. `[maven-release-plugin] prepare release vX.Y.Z` — passe les POM en version release et met à jour `<scm><tag>`
2. `[maven-release-plugin] prepare for next development iteration` — passe les POM en `X.Y.(Z+1)-SNAPSHOT`

Et le tag Git `vX.Y.Z` sur le premier commit.

#### Pousser les commits et le tag

```bash
git push origin master
git push origin vX.Y.Z
```

> **Attention SSH** : le `pom.xml` référence `scm:git:git@github.com:IGNF/validator.git`. Si la clé SSH n'est pas configurée, utiliser `-DpushChanges=false` lors du `release:prepare` et pousser manuellement en HTTPS :
>
> ```bash
> mvn release:prepare -DpushChanges=false
> git push origin master
> git push origin vX.Y.Z
> ```

#### Reprendre une release interrompue

Si `release:prepare` échoue en cours de route (ex. tag déjà existant), ne pas relancer depuis le début. Vérifier l'état :

```bash
git log --oneline -5
git tag -l 'v4.5.*'
grep '<version>' pom.xml
```

- Si le commit `prepare release` et le tag existent déjà : terminer manuellement l'itération suivante (passer en `SNAPSHOT`, committer, supprimer `release.properties` et `*.releaseBackup`).
- Ou reprendre avec `mvn release:prepare -Dresume=true -DpushChanges=false` si `release.properties` est cohérent.

### Méthode manuelle

Si le plugin Maven Release pose problème, la release peut être faite à la main :

```bash
# 1. Passer en version release
mvn versions:set -DnewVersion=4.5.4 -DgenerateBackupPoms=false

# 2. Mettre à jour <scm><tag>v4.5.4</scm><tag> dans pom.xml

# 3. Committer et tagger
git add pom.xml validator-*/pom.xml CHANGELOG.md
git commit -m "[maven-release-plugin] prepare release v4.5.4"
git tag -a v4.5.4 -m "[maven-release-plugin] copy for tag v4.5.4"

# 4. Passer à la version SNAPSHOT suivante
mvn versions:set -DnewVersion=4.5.5-SNAPSHOT -DgenerateBackupPoms=false
# Remettre <scm><tag>4.4.20</scm><tag> dans pom.xml (convention du projet)

git add pom.xml validator-*/pom.xml
git commit -m "[maven-release-plugin] prepare for next development iteration"

# 5. Pousser
git push origin master
git push origin v4.5.4
```

### Construire les binaires depuis le tag

Les binaires doivent être construits **depuis le tag**, pas depuis `master` :

```bash
git checkout v4.5.4
make package
git checkout master
```

Fichiers à publier :

- `validator-cli/target/validator-cli.jar`
- `validator-cli/target/ign-validator_4.5.4_all.deb`
- `validator-cli/target/ign-validator-4.5.4-1.noarch.rpm`

### Créer la release GitHub

#### Via l'interface web

1. Aller sur [github.com/IGNF/validator/releases](https://github.com/IGNF/validator/releases)
2. Cliquer sur **Draft a new release**
3. Choisir le tag `vX.Y.Z`
4. Rédiger les notes (reprenant le [CHANGELOG.md](../CHANGELOG.md))
5. Attacher les 3 fichiers binaires

#### Via GitHub CLI

```bash
gh release create v4.5.4 \
  --title "v4.5.4" \
  --notes "Notes de la release..." \
  validator-cli/target/validator-cli.jar \
  validator-cli/target/ign-validator_4.5.4_all.deb \
  validator-cli/target/ign-validator-4.5.4-1.noarch.rpm
```

## Récapitulatif rapide

```bash
# Installation
sudo apt-get install openjdk-17-jdk maven gdal-bin

# Développement
mvn formatter:format
make test

# Release
mvn release:prepare -DpushChanges=false
git push origin master
git push origin vX.Y.Z
git checkout vX.Y.Z && make package && git checkout master
gh release create vX.Y.Z --title "vX.Y.Z" --notes "..." validator-cli/target/*
```

## Voir aussi

- [CONTRIBUTING.md](../CONTRIBUTING.md) — formatage du code et CI
- [cli.md](cli.md) — utilisation du validateur en ligne de commande
- [ogr2ogr.md](dependencies/ogr2ogr.md) — installation de GDAL
