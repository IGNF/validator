
## document_validator - validation d'un document

## Affichage de l'aide de la commande

Exécuter la commande suivante pour récupérer la liste des paramètres disponibles :

```bash
java -jar validator-cli.jar document_validator --help
```

## Exemple d'utilisation avec les données PCRS

Pour valider le fichier "GeoVendee_LeTallud-StGemme.gml" dans le dossier "document-test" en fonction du modèle [CNIG_PCRS_v2.0](https://ignf.github.io/validator/validator-core/src/test/resources/config-json/CNIG_PCRS_v2.0/document.json), on appellera par exemple la commande suivante :

```bash
java -jar validator-cli.jar document_validator \
    --report-format jsonl \
    --model https://ignf.github.io/validator/validator-core/src/test/resources/config-json/CNIG_PCRS_v2.0/document.json \
    --input document-test \
    --output validation \
    --srs EPSG:3947 \
    --normalize
```

Les fichiers produits seront les suivants :

| Fichier                                         | Description                                                                             |
| ----------------------------------------------- | --------------------------------------------------------------------------------------- |
| `./validation/`                                 | Le répertoire de résultat de la validation                                              |
| `./validation/validation.jsonl`                 | Le rapport de validation                                                                |
| `./validation/document-info.json`               | Les informations sur le documents validés (comptages, emprises,...)                     |
| `./validation/document_database.db`             | La base de données temporaire de validation (pour contrôle d'unicité, de référence,...) |
| `./validation/document-test/DATA/{NomType}.csv` | Les données normalisées pour une éventuelle livraison à un entrepot de diffusion        |
| `./validator-debug.log`                         | Les logs du validateur java                                                             |

## Normalisation et conversion

La conversion des fichiers au format CSV est réalisées sous forme de fichier "companions" avec des extensions dédiées (`.vrows` et `.vtabs`) dans le dossier à valider :

* Pour un fichier `./document-test/TRONCON_ROUTE.shp`, on notera la présence d'un fichier CSV `./document-test/TRONCON_ROUTE.vrows` contenant le résultat de la conversion du Shapefile en CSV.
* Pour un fichier GML `./document-test/GeoVendee_LeTallud-StGemme.gml` contenant plusieurs collections, on notera la présence d'un dossier `./document-test/GeoVendee_LeTallud-StGemme.vtabs` contenant le résultat de la conversion du GML en CSV.

Ces conversions sont réalisées à l'aide de [ogr2ogr de GDAL](../dependencies/ogr2ogr.md). L'utilisation d'extension dédiées facilite principalement une nouvelle exécution du validateur sur un dossier.

## Options du plugin CNIG

Les contrôles spécifiques aux plugins sont contrôlés par des paramètres en entrée de validation. Pour la ligne de commande `document_validator` le plugin CNIG utilise les paramètres suivants :
* Option `cnig-document-emprise` attend une *géométrie* au format WKT et définie dans le système de projection EPSG:4326
* Option `cnig-complexity-tolerance` attend deux groupes de 4 *seuils* de détection de géométries complexes.
  * *Nombre maximum de point par mètre*
  * *Nombre maximum de parties*
  * *Nombre maximum de trous*
  * *Nombre maximum de points*
  * Fournir d'abord les seuils qui leverait un avertissement
  * Puis les seuils qui leverait une erreur

```bash
java -jar validator-cli.jar document_validator \
    --report-format jsonl \
    --model cnig_PLU_2017 \
    --input INSEE_PLU_DATE \
    --output validation \
    --srs EPSG:2154 \
    --normalize \
    --plugins "CNIG" \
    --cnig-complexity-tolerance "[[-1,500,500,0.1,50000],[200000,1000,1000,10,50000]]" \
    --cnig-document-emprise "POLYGON((1.186438253 47.90390196, 1.098884284 47.90390196, 1.098884284 47.83421197, 1.186438253 47.83421197, 1.186438253 47.90390196))"
```
