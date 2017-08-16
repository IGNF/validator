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

Remarque : Le programme s'appuie sur l'exécutable ```ogr2ogr``` de GDAL pour la lecture des données géographiques. Le format CSV sert de format pivot.

# Licence

Voir [LICENCE.md](LICENCE.md)

# Documentation technique

Voir [doc/model.md](doc/model.md)


# Cas d'utilisation

Ce programme a été développé dans le cadre du [géoportail de l'urbanisme](https://www.geoportail-urbanisme.gouv.fr) pour la validation des [standards CNIG](https://www.geoportail-urbanisme.gouv.fr/standard/).


# Compilation

```
mvn package
```

# Exécution

```
java -jar validator-cli/target/validator-cli.jar --help
```

## Exemple modèle classique [GEOFLA](validator-example/geofla/README.md)

```
java -jar validator-cli/target/validator-cli.jar -c validator-example/geofla/config/ -v GEOFLA_2015 -i validator-example/geofla/data -s EPSG:2154 -W LATIN1 ```

## Utilisateur en mode validateur CNIG (GpU)

Pour le GPU, il convient de charger le plugin dédié via l'option ```--plugins```
