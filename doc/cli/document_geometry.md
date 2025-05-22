
# document_geometry

## Description

Calcul géométrique de l'union de géométries, et valide en profondeur les données.

## Exemple d'utilisation

Pour convertir réaliser l'union de [ZONE_URBA.csv](../../validator-plugin-cnig/src/test/resources/geometry/ZONE_URBA.csv) et de [SECTEUR_CC.csv](../../validator-plugin-cnig/src/test/resources/geometry/SECTEUR_CC.csv) vers [union_geometries.csv](../../validator-plugin-cnig/src/test/resources/geometry/union_geometries):

```bash
java -jar validator-cli.jar document_geometry \
    -i "validator-plugin-cnig/src/test/resources/geometry/ZONE_URBA.csv,validator-plugin-cnig/src/test/resources/geometry/SECTEUR_CC.csv" \
    -g "WKT" \
    -o ../../validator-plugin-cnig/src/test/resources/geometry/union_geometries
```

## Remarques

* Le modèle pivot est une simplification du modèle ISO 19915 se concentrant sur les éléments utilisés dans les profiles INSPIRE et CNIG.
* **ATTENTION : Ce modèle pivot JSON n'est pas standardisé et est susceptible d'évoluer**
