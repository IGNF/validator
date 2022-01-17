# Validator

[![CI](https://github.com/IGNF/validator/actions/workflows/main.yml/badge.svg)](https://github.com/IGNF/validator/actions/workflows/main.yml)
[![License: CeCILL-B](https://img.shields.io/badge/License-CeCILL--B-blue.svg)](LICENSE)

## Description

> This programs allows to validate datasets (a folder containing shapefiles, PDF, etc.) according to a file mapping and a FeatureCatalog. It outputs a report describing validation errors and normalized data ready for database integration.
> It has been developed for the [*géoportail de l'urbanisme*](https://www.geoportail-urbanisme.gouv.fr) to validate urbanism data according to [CNIG standards](https://www.geoportail-urbanisme.gouv.fr/standard/) and allow merging in a national database.

Ce programme permet de valider et de normaliser les données présentes dans une arborescence de fichiers. Ces données peuvent être :

* Des tables, géographiques ou non, aux formats CSV, GML, Shapefile ou MapInfo
* Des fiches de métadonnées
* Des fichiers PDF
* Des dossiers (principalement pour contrôle de présence)

Le paramétrage s'effectue à l'aide de [fichiers JSON décrivant des arborescences de fichiers et des tables](validator-core/src/main/resources/schema/README.md).

## Cas d'usage

* [Géoportail de l'Urbanisme](https://www.geoportail-urbanisme.gouv.fr) : Validation des données en fonction des [standards CNIG PLU, POS, CC, PSMV, SUP et SCOT](https://www.geoportail-urbanisme.gouv.fr/standard/) en amont de l'aggrégation dans une base nationale.
* [Validateur TRI](https://validateur-tri.ign.fr/) : Validation de la conformité d'un jeu de données géographiques sur les territoires à risque important d'inondation (TRI) vis à vis du standard de données COVADIS du thème "Directive Inondation" version 2.

## Principe de fonctionnement

Le schéma suivant illustre le [principe de fonctionnement du validateur de document](doc/principe.md) :

![Working principle](doc/img/principe.jpg)

## Principales fonctionnalités

* Validation d'une arborescence de fichiers en fonction d'un modèle de document.
* Validation des tables en fonction d'un modèle de table.
* Validation des fiches de métadonnées.
* Production d'un rapport d'erreur au format JSON.
* Production de données normalisées (pour agrégation et diffusion).
* Validation métier à l'aide de [plugins](doc/plugins.md) (CNIG pour GpU, DGPR pour TRI,...) pour les contrôles qui sont pas formalisés dans le modèle de validation.

## Utilisation

Le validateur se présente sous la forme d'un exécutable java (`validator-cli.jar`) utilisable en ligne de commande. Il n'offre pas d'interface graphique car il a vocation à être utilisé pour la mise en oeuvre de services web tel le [Géoportail de l'Urbanisme](https://www.geoportail-urbanisme.gouv.fr) offrant ces interfaces.

Les techniciens peuvent se référer à la documentation [utilisation du validateur en ligne de commande](doc/cli.md).

## Dépendances

Les exécutables systèmes ci-après sont requis pour l'exécution du programme :

* java >= 11
* [ogr2ogr >= v2.3.0](doc/dependencies/ogr2ogr.md) : Utilisé pour lire et convertir les données en entrée dans un format pivot avant validation (CSV)

Les dépendances java telle [GeoTools](doc/dependencies/geotools.md) sont décrites dans les fichiers [pom.xml](pom.xml) et intégrées dans l'exécutable JAVA.

## Documentation technique

Les principaux documents sont les suivants :

* [Modélisation des données](validator-core/src/main/resources/schema/README.md)
* [Exemples de modèles de document](validator-core/src/test/resources/config-json/README.md)
* [Utilisation du validateur en ligne de commande](doc/cli.md) pour une utilisation directe.
* [Principe de fonctionnement du validateur de document](doc/principe.md)
* [Principe de fonctionnement des plugins](doc/plugins.md)
* [Liste des codes d'erreurs (JSON)](validator-core/src/main/resources/error-code.json)
* [Projection supportées (JSON)](validator-core/src/main/resources/projection.json)

Les documents ci-après traitent des problématiques particulières :

* [Metadata (english)](doc/metadata.md)
* [Characters validation (english)](doc/characters.md)
* [plugin-cnig - validation des champs IDURBA](doc/plugin-cnig/idurba.md)
* [plugin-cnig - Validation des mots clés en fonction des CSMD CNIG](doc/plugin-cnig/keywords.md)

