# Validator

[![Build Status](https://travis-ci.org/IGNF/validator.svg?branch=master)](https://travis-ci.org/IGNF/validator)


## Description

> This programs allows to validate datasets (a folder containing shapefiles, PDF, etc.) according to a file mapping and a FeatureCatalog. It outputs a report describing validation errors and normalized data ready for database integration.
> It has been developed for the [*géoportail de l'urbanisme*](https://www.geoportail-urbanisme.gouv.fr) to validate urbanism data according to [CNIG standards](https://www.geoportail-urbanisme.gouv.fr/standard/) and allow merging in a national database.

Ce programme permet de valider et de normaliser les données présentes dans une arborescence de fichiers. Ces données peuvent être :

* Des tables, géographiques ou non, aux formats CSV, GML, Shapefile ou MapInfo
* Des fiches de métadonnées
* Des fichiers PDF
* Des dossiers (principalement pour contrôle de présence)

Le paramétrage s'effectue à l'aide de fichiers XML décrivant :

* Des modèles de table (FeatureCatalogue : FeatureType/AttributeType)
* Un mapping de fichiers (chemin d'accès, obligatoire/conseillé/optionel, type: pdf, table, dossier, etc.)

Il a été développé dans le cadre du [géoportail de l'urbanisme](https://www.geoportail-urbanisme.gouv.fr) pour la validation des [standards CNIG](https://www.geoportail-urbanisme.gouv.fr/standard/).


## Principe de fonctionnement

Ce programme permet de valider et de normaliser les données présentes dans une arborescence de fichiers. Ces données peuvent être :

* Des tables, géographiques ou non, aux formats CSV, GML, Shapefile ou MapInfo
* Des fiches de métadonnées
* Des fichiers PDF
* Des dossiers (principalement pour contrôle de présence)

Le paramétrage s'effectue à l'aide de fichiers XML décrivant :

* Des modèles de table (FeatureCatalogue : FeatureType/AttributeType)
* Un mapping de fichiers (chemin d'accès, obligatoire/conseillé/optionel, type: pdf, table, dossier, etc.)

![Working principle](doc/principe.jpg)

## Licence

Voir [LICENCE.md](LICENCE.md)

## Documentation technique

* [validator-core](doc/model.md)
* [Metadata validation](doc/metadata.md)


## Cas d'utilisation

Ce programme a été développé dans le cadre du [géoportail de l'urbanisme](https://www.geoportail-urbanisme.gouv.fr) pour la validation des [standards CNIG](https://www.geoportail-urbanisme.gouv.fr/standard/).


## Installation de ogr2ogr

Le programme s'appuie sur l'exécutable `ogr2ogr` de GDAL pour la lecture des données géographiques en utilisant le format CSV comme un pivot. Il convient de souligner qu'il est assez sensible aux changements de comportement de `ogr2ogr`, en particulier au niveau de la gestion de la précision des coordonnées.

### Versions testées

* 1.9.1
* 1.9.3
* 1.10.1
* 1.11.3
* 2.1.*
* 2.2.2

### Versions bannies

* 1.9.0 : Bug dans la production du WKT (limité à 8000 caractères)

### Remarques

* Entre 1.x et 2.x : Régression dans la gestion de la précision des coordonnées WKT et difficultés pour contrôler le nombre de décimales avec OGR_WKT_PRECISION (précision globale)



## Compilation

```bash
mvn package
```

## document_validator

### Affichage de l'aide

```bash
java -jar validator-cli/target/validator-cli.jar document_validator --help
```

### Spécification du chemin vers ogr2ogr

```bash
java -Dogr2ogr_path=/path/to/ogr2ogr -jar validator-cli/target/validator-cli.jar document_validator --help
```

### Exemple modèle classique [GEOFLA](validator-example/geofla/README.md)

```bash
java -jar validator-cli/target/validator-cli.jar document_validator -c validator-example/geofla/config/ -v GEOFLA_2015 -i validator-example/geofla/data -s EPSG:2154 -W LATIN1
```

### Utilisation en mode validateur CNIG (GpU)

Pour le GPU, il convient de charger le plugin dédié via l'option `--plugins`

## Autres commandes

* `metadata_to_json` : Conversion de métadonnées XML dans un modèle pivot simplifié JSON

