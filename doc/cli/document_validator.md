
## document_validator - validation d'un document

## Affichage de l'aide de la commande

Exécuter la commande suivante pour récupérer la liste des paramètres disponibles :

```bash
java -jar validator-cli.jar document_validator --help
```

## Options

La liste ci-dessous correspond à la sortie de `--help`, regroupée par usage. Les options sans valeur par défaut indiquée sont facultatives, sauf `--input` et `--model`.

### Entrée

| Option                         | Description                                                                                                  |
| ------------------------------ | ------------------------------------------------------------------------------------------------------------ |
| `-i`, `--input <DIR>`          | **Obligatoire.** Dossier contenant le document à valider.                                                    |
| `-m`, `--model <URL>`          | **Obligatoire.** URL du [modèle de document](../document_model.md) (ex : `cnig_PLU_2017` ou l'URL d'un `document.json`). |
| `-v`, `--version <NOM>`        | *[déprécié]* Nom du modèle de document (ex : `cnig_PLU_2013`). Utiliser `--model`.                           |
| `-c`, `--config <DIR>`         | *[déprécié]* Dossier contenant les modèles de document. Utiliser `--model`.                                  |
| `-p`, `--proxy <HOST:PORT>`    | Proxy réseau (ex : `proxy.ign.fr:3128`). Voir aussi les variables d'environnement de [cli.md](../cli.md).    |

### Sortie et rapport

| Option                                  | Description                                                                                                                                  |
| --------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------- |
| `-O`, `--output <DIR>`                  | Dossier de résultat de la validation. Par défaut : dossier `validation` à côté du dossier d'entrée. Il ne peut pas être dans le dossier d'entrée. |
| `--no-overwrite`                        | Arrête la validation si le dossier de sortie existe déjà et n'est pas vide. Sans cette option, le dossier de sortie est **vidé** avant la validation. |
| `--max-errors <N>`                      | Nombre maximum d'erreurs rapportées pour un même code. Un message `VALIDATOR_ERROR_LIMIT_REACHED` (INFO) signale chaque code dont la limite est atteinte. |
| `--error-config <FILE>`                 | Fichier `error-config.json` pour modifier les messages ou niveaux de certains codes d'erreur (la liste est exportée par la commande `error_config`). |
| `--normalize`                           | Produit les données normalisées (CSV) dans le dossier de sortie. Obligatoire pour obtenir `document-info.json`.                              |
| `--output-projection <CRS>`             | Projection des données normalisées. Par défaut `CRS:84`. `same-as-source` désactive la reprojection.                                         |
| `--report-format <FORMAT>`              | *[déprécié]* Seul `jsonl` est supporté.                                                                                                      |

### Contrôles

| Option                             | Description                                                                                                                       |
| ---------------------------------- | --------------------------------------------------------------------------------------------------------------------------------- |
| `-s`, `--srs <CRS>`                | Système de projection des données (ex : `EPSG:2154`).                                                                              |
| `-W`, `--encoding <CHARSET>`       | Encodage des données. Par défaut `UTF-8`, remplacé par celui de la fiche de métadonnées si elle en déclare un.                    |
| `--data-extent <WKT>`              | Géométrie dans laquelle les données doivent se situer (même projection que les données).                                          |
| `--flat`                           | Ignore la hiérarchie de dossiers : les fichiers sont associés aux modèles par leur nom et non par leur chemin.                    |
| `--enable-conditions`              | Active les [conditions SQL](../document_model.md) des modèles de table. Désactivé par défaut (risque d'injection SQL : à réserver aux modèles de confiance). |
| `--plugins <LISTE>`                | Plugins chargés pour la validation, séparés par des virgules (ex : `CNIG`).                                                        |

### Correction des chaînes de caractères

| Option                              | Description                                                                              |
| ----------------------------------- | ---------------------------------------------------------------------------------------- |
| `--string-fix-utf8`                 | Corrige le double encodage UTF-8 (cas d'un fichier déclaré à tort en LATIN1).             |
| `--string-simplify-common`          | Remplace des caractères Unicode par des équivalents mieux supportés par les polices.      |
| `--string-simplify-charset <CHARSET>` | Remplace des caractères Unicode par des équivalents du jeu de caractères (ex : `œ` devient `oe` en LATIN1). |
| `--string-escape-charset <CHARSET>` | Échappe en hexadécimal (`\uXXXX`) les caractères non supportés par le jeu de caractères.   |
| `--string-escape-controls`          | Échappe en hexadécimal (`\uXXXX`) les caractères de contrôle.                             |
| `--string-all <CHARSET>`            | Applique `--string-fix-utf8`, `--string-simplify-common` et `--string-simplify-charset` pour le jeu de caractères donné. |

### Plugin CNIG (`--plugins CNIG`)

| Option                                  | Description                                                                                         |
| --------------------------------------- | --------------------------------------------------------------------------------------------------- |
| `--cnig-document-emprise <GEOM>`        | Emprise attendue du document : fichier GeoJSON ou géométrie WKT, en EPSG:4326.                      |
| `--cnig-complexity-tolerance <SEUILS>`  | Seuils de détection des géométries complexes (voir [plus bas](#options-du-plugin-cnig)).            |

### Plugin DGPR

| Option                       | Description                                                                                          |
| ---------------------------- | ---------------------------------------------------------------------------------------------------- |
| `--dgpr-tolerance <N>`       | Tolérance de comparaison des géométries, dans la projection d'entrée (ex : `1` pour 1 m en EPSG:2154). |
| `--dgpr-simplify <N>`        | Distance de simplification des géométries pour leur comparaison, dans la projection d'entrée.        |
| `--dgpr-skip-graph-topology` | Ignore le contrôle de topologie du graphe (partition des surfaces inondables, `DGPR_ISO_HT_*`).      |
| `--dgpr-skip-inclusion`      | Ignore le contrôle d'inclusion des surfaces inondables entre scénarios (`DGPR_INOND_INCLUSION_*`).   |
| `--dgpr-safe-simplify`       | *[déprécié]* Sans effet (la simplification préserve toujours la topologie).                          |

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
