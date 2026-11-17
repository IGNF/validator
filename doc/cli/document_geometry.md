
# document_geometry

## Description

Calcule l'union stable des géométries de fichiers CSV et l'écrit au format WKT dans le fichier de sortie (pas de validation des données).

## Options

| Option                    | Description                                                                                          |
| ------------------------- | ---------------------------------------------------------------------------------------------------- |
| `-i`, `--input <FILES>`   | **Obligatoire.** Fichiers CSV en entrée, séparés par des virgules.                                   |
| `-g`, `--geometries <COLS>` | Noms des colonnes géométriques (WKT), séparés par des virgules (`geom` et `geometry` sont toujours pris en compte). |
| `-o`, `--output <FILE>`   | **Obligatoire.** Fichier de sortie (géométrie WKT).                                                  |
| `-p`, `--proxy <HOST:PORT>` | Proxy réseau (ex : `proxy.ign.fr:3128`).                                                          |

## Exemple d'utilisation

Pour réaliser l'union de [ZONE_URBA.csv](../../validator-plugin-cnig/src/test/resources/geometry/ZONE_URBA.csv) et de [SECTEUR_CC.csv](../../validator-plugin-cnig/src/test/resources/geometry/SECTEUR_CC.csv), depuis la racine du dépôt :

```bash
java -jar validator-cli.jar document_geometry \
    -i "validator-plugin-cnig/src/test/resources/geometry/ZONE_URBA.csv,validator-plugin-cnig/src/test/resources/geometry/SECTEUR_CC.csv" \
    -g "WKT" \
    -o union_geometries.wkt
```
