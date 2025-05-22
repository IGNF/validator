
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
