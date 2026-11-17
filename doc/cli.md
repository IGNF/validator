
# Utilisation du validateur en ligne de commande

## Pré-requis

* Exécutable java de OpenJDK 17 (`java --version` doit fonctionner et mentionner une version java 17 ou supérieure)
* Exécutable [ogr2ogr de GDAL](dependencies/ogr2ogr.md) (`ogr2ogr --version` doit fonctionner et mentionner une version supportée)

## Installation du validateur

Pour chaque version, les livrables sont attachés à une [release sur github.com](https://github.com/IGNF/validator/releases).

Vous pouvez télécharger le fichier `validator-cli.jar` pour la dernière release et tester à l'aide de la commande `java -jar validator-cli.jar version` qui affichera la version du validateur.

## Paramétrage

Les paramètres sont gérés sous forme de variables d'environnement :

| Nom            | Description                              | Valeur par défaut |
| -------------- | ---------------------------------------- | ----------------- |
| `OGR2OGR_PATH` | Chemin vers l'exécutable ogr2ogr de GDAL | "ogr2ogr"         |
| `OGR2OGR_TIMEOUT` | Durée maximale d'une conversion ogr2ogr (en secondes) | 3600 |
| `GMLAS_CONFIG` | Fichier de configuration du pilote GMLAS de GDAL (validation XML des GML avec un schéma XSD, ex : PCRS) | configuration intégrée |
| `DB_URL`       | URL JDBC d'une base PostgreSQL/PostGIS de validation (ex : `jdbc:postgresql://localhost:5432/validator`). Sinon, une base SQLite `document_database.db` est créée dans le dossier de sortie | |
| `DB_USER`, `DB_PASSWORD` | Identifiants de la base PostgreSQL (`DB_URL`) | |
| `DB_SCHEMA`    | Schéma PostgreSQL de la validation, **supprimé et recréé** à chaque validation : utiliser un schéma par validation simultanée (lettres, chiffres et `_` ; `public`, `information_schema` et `pg_*` interdits) | `validation` |
| `DB_STATEMENT_TIMEOUT` | Durée maximale d'une requête SQL sur la base PostgreSQL (`DB_URL`), en secondes (`0` : sans limite) | 3600 |
| `HTTP_PROXY`   | ex : http://proxy:3128                   |                   |
| `HTTPS_PROXY`  | ex : http://proxy:3128                   |                   |
| `NO_PROXY`     | localhost,demo.localhost                 |                   |


## Principales commandes

| Commande                                        | Description                                                                                     |
| ----------------------------------------------- | ----------------------------------------------------------------------------------------------- |
| `--help`                                        | Affiche la liste des commandes disponibles                                                      |
| `version`                                       | Affiche la version de l'exécutable validator-cli.jar (ex : `4.7.1`, `4.7.2-SNAPSHOT`,...)       |
| [document_validator](cli/document_validator.md) | Valide un document en fonction d'un modèle de document                                          |
| `projection_list`                               | Exporte la liste des projections supportées au format JSON.                                     |
| `error_config`                                  | Exporte la liste des modèles d'erreur au format JSON.                                           |
| `read_url`                                      | Lit le contenu d'une URL (pour debug des problèmes de proxy et de certificat)                   |
| [metadata_to_json](cli/metadata_to_json.md)     | Convertit d'un fiche de métadonnées XML (ISO19115) dans un modèle simplifié au format JSON.     |
| `cnig_extract_idgest`                           | Extrait de la valeur de IDGEST à partir d'un fichier SERVITUDE (moissonnage des SUP sur GpU)    |
| [document_geometry](cli/document_geometry.md)   | Réalise l'union stable des géométries en entrée                                                 |
| `unicode_table`                                 | Exporte une table unicode documentant les transformations optionnelles des chaînes (CSV)      |
| `sleep`                                         | Attend une durée donnée (test du délai maximal et de l'interruption du processus)             |

