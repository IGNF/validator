# Validator

[![Build Status](https://travis-ci.org/IGNF/validator.svg?branch=master)](https://travis-ci.org/IGNF/validator)

# Principe de fonctionnement

![Working principle](doc/principe.jpg)

Ce programme permet de valider et de normaliser les données présentes dans une arborescence de fichiers. Ces données peuvent être :

* Des tables, géographiques ou non, aux formats CSV, GML, Shapefile ou MapInfo
* Des fiches de métadonnées
* Des fichiers PDF
* Des dossiers (principalement pour contrôle de présence)

Le paramétrage s'effectue à l'aide de fichiers XML décrivant :

* Des modèles de table (FeatureCatalogue : FeatureType/AttributeType)
* Un mapping de fichiers (chemin d'accès, obligatoire/conseillé/optionel, type: pdf, table, dossier, etc.)

# Licence

Voir [LICENCE.md](LICENCE.md)

# Documentation technique

Voir [doc/model.md](doc/model.md)


# Cas d'utilisation

Ce programme a été développé dans le cadre du [géoportail de l'urbanisme](https://www.geoportail-urbanisme.gouv.fr) pour la validation des [standards CNIG](https://www.geoportail-urbanisme.gouv.fr/standard/).


# Compilation

## Version standard

```
mvn package
```

## Version CNIG (GpU)

```
mvn package -P cnig
```

# Exécution

```
java -jar validator-cli/target/validator-cli-2.2.2-SNAPSHOT.jar --help
```

# Exemple

* [GEOFLA](validator-example/geofla/README.md)

```
java -jar validator-cli/target/validator-cli-2.2.2-SNAPSHOT.jar -c validator-example/geofla/config/ -v GEOFLA_2015 -i validator-example/geofla/data -s EPSG:2154 -W LATIN1
```

# Mise en garde

La stabilité est assurée (pour l'instant) uniquement au niveau de l'appel à l'exécutable en ligne de commande. L'organisation des classes peut évoluer.
