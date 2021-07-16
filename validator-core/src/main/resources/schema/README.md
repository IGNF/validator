# Modélisation des données

## Description

Cette documentation décrit schématiquement les modèles JSON de validation du validateur `IGNF/validator` pour ses versions `4.0` et supérieures.

## Vue d'ensemble des concepts

| Concept               | Description                                                                     | Implémentation  |
| --------------------- | ------------------------------------------------------------------------------- | --------------- |
| [Document](#document) | Modélisation du contenu d'un dossier ou d'une archive.                          | `DocumentModel` |
| [File](#file)         | Modélisation d'un fichier du document (chemin, type, présence obligatoire,...). | `FileModel`     |
| [Table](#table)       | Modélisation d'une table matérialisée dans un fichier.                          | `FeatureType`   |
| [Column](#column)     | Modélisation d'une colonne d'une table.                                         | `AttributeType` |

## Modélisation des concepts

### Document

Le concept de [Document.json](Document.json) est utilisé pour modéliser un dossier ou une archive contenant des fichiers.

| Propriété              | Type                  | Description                                             | Obligatoire | Version |
| ---------------------- | --------------------- | ------------------------------------------------------- | :---------: | :-----: |
| `document.name`        | `string`              | Le nom du modèle au niveau système (ex : cnig_PLU_2013) |      O      |   4.0   |
| `document.title`       | `string`              | Le nom du modèle pour l'affichage                       |      N      |   4.0   |
| `document.description` | `string`              | La description du modèle                                |      N      |   4.0   |
| `document.files`       | `File[]`              | La liste des fichiers attendus dans le dossier          |      O      |   4.0   |
| `document.constraints` | `DocumentConstraints` | Constraintes au niveau du document                      |      N      |   4.0   |

### DocumentConstraints

| Propriété                        | Type     | Description                                 | Obligatoire | Version |
| -------------------------------- | -------- | ------------------------------------------- | :---------: | :-----: |
| `document.folderName`            | `RegExp` | Nom de dossier attendu pour le document     |      N      |   4.0   |
| `document.metadataSpecification` | `string` | Valeur attendue dans la fiche de métadonnée |      N      |   4.0   |

### File

Les fichiers d'un document peuvent correspondre à des tables, des sous-dossiers, des PDF ou des fichiers de métadonnées. Ils sont décrits par les propriétés suivantes :

| Propriété          | Type                  | Description                                        | Obligatoire | Version |
| ------------------ | --------------------- | -------------------------------------------------- | :---------: | :-----: |
| `file.name`        | `string`              | Le nom identifiant le fichier                      |      O      |   4.0   |
| `file.title`       | `string`              | Le nom du fichier pour affichage                   |      N      |   4.0   |
| `file.description` | `string`              | La description du fichier                          |      N      |   4.0   |
| `file.type`        | [FileType](#filetype) | Le type de fichier                                 |      O      |   4.0   |
| `file.path`        | `RegExp`              | Chemin relatif par rapport à la racine du document |      N      |   4.0   |
| `file.tableModel`  | `string`              | Liens vers le [modèle de table](#table)            |      N      |   4.0   |

### FileType

Les types de fichiers supportés sont les suivants :

| Type          | Description                                                                               | Version |
| ------------- | ----------------------------------------------------------------------------------------- | :-----: |
| `directory`   | Dossier permettant d'en valider l'existence                                               |   4.0   |
| `metadata`    | Fiche de métadonnées XML au format ISO 19115 (validation profile INSPIRE & CNIG pour GpU) |   4.0   |
| `pdf`         | Fichier `.pdf`                                                                            |   4.0   |
| `table`       | Table de données géographique ou non (`.csv`, `.dbf`, `.shp`, `.geojson`, `.gml`)         |   4.0   |
| `multi_table` | Un ensemble de table stockés dans un seul fichier (`.gml`)                                |   4.2   |

Remarque : Le support du seul format GML pour les `multi_table` est lié à l'introduction du concept la validation des données [PCRS](https://github.com/cnigfr/PCRS). Il pourrait aussi s'appliquer aux données GeoPackage.

### Table

Le concept de [Table.json](Table.json) est utilisé pour modéliser une table et ses colonnes.

| Propriété           | Type       | Description                             | Obligatoire | Version |
| ------------------- | ---------- | --------------------------------------- | :---------: | :-----: |
| `table.name`        | `string`   | Le nom de la table au niveau système    |      O      |   4.0   |
| `table.title`       | `string`   | Le nom de la table pour affichage       |      N      |   4.0   |
| `table.description` | `string`   | La description de la table              |      N      |   4.0   |
| `table.columns`     | `Column[]` | La description des colonnes de la table |      O      |   4.0   |

Remarque : L'ajout d'une propriété  `PrimaryKey`, définie sous forme d'une chaîne de caractère ("ID" ou "C1,C2"), est à l'étude.

### Column

Le concept de [Column.json](Column.json) est utilisé pour modéliser une table et ses colonnes.

| Propriété            | Type                   | Description                          | Obligatoire | Version |
| -------------------- | ---------------------- | ------------------------------------ | :---------: | :-----: |
| `column.name`        | `string`               | Le nom de la table au niveau système |      O      |   4.0   |
| `column.title`       | `string`               | Le nom de la table pour affichage    |      N      |   4.0   |
| `column.description` | `string`               | La description de la table           |      N      |   4.0   |
| `column.type`        | [Type](types/index.md) | Le type de la colonne                |      O      |   4.0   |
| `column.constraints` | `ColumnConstraints`    | Les contraintes sur la colonne       |      N      |   4.0   |

### ColumnType

Le validateur supporte une liste finie de type :

| Nom                                     | Description                                          | Version |
| --------------------------------------- | ---------------------------------------------------- | :-----: |
| [Boolean](types/Boolean.md)             | Vrai ou faux                                         |   4.0   |
| [String](types/String.md)               | Chaîne de caractères                                 |   4.0   |
| [Integer](types/Integer.md)             | Valeur numérique entière                             |   4.0   |
| [Double](types/Double.md)               | Valeur numérique en virgule flottante                |   4.0   |
| [Date](types/Date.md)                   | Jour, mois et année                                  |   4.0   |
| [Geometry](types/Geometry.md)           | Géométrie de type non spécifié                       |   4.0   |
| [Point](types/Geometry.md)              | Géométrie de type point                              |   4.0   |
| [LineString](types/Geometry.md)         | Géométrie de type polyligne                          |   4.0   |
| [Polygon](types/Geometry.md)            | Géométrie de type polygone                           |   4.0   |
| [MultiPoint](types/Geometry.md)         | Géométrie de type multi-point                        |   4.0   |
| [MultiLineString](types/Geometry.md)    | Géométrie de type multi-polyligne                    |   4.0   |
| [MultiPolygon](types/Geometry.md)       | Géométrie de type multi-polygone                     |   4.0   |
| [GeometryCollection](types/Geometry.md) | Géométrie de type hétérogène                         |   4.0   |
| [Path](types/Path.md)                   | Chemin vers un fichier dans le [document](#document) |   4.0   |
| [Url](types/Url.md)                     | URL                                                  |   4.0   |


L'ajout des types suivants est à l'étude pour les futures versions :

| Nom                             | Description                 |
| ------------------------------- | --------------------------- |
| [DateTime](types/DateTime.md)   | Jour, mois, année et heures |
| [Year](types/Year.md)           | Année uniquement            |
| [YearMonth](types/YearMonth.md) | Année et mois               |
| [JSON](types/JSON.md)           | Valeur JSON valide          |


### ColumnConstraints

| Propriété   | Type              | Description                                                                  | Version |
| ----------- | ----------------- | ---------------------------------------------------------------------------- | :-----: |
| `required`  | `boolean`         | Contrainte interdisant une valeur nulle ou vide                              |   4.0   |
| `unique`    | `boolean`         | Contrainte d'unicité au sein de la table                                     |   4.0   |
| `enum`      | `any[]`           | Contrainte d'appartenance à une liste de valeur                              |   4.0   |
| `pattern`   | `string`          | Contrainte sous forme d'une expression régulière (syntaxe JAVA)              |   4.0   |
| `maxLength` | `integer`         | Contrainte sur la longueur maximale d'une chaîne de caractère                |   4.0   |
| `reference` | `ReferenceTarget` | Contrainte de correspondance entre la valeur et celle d'une table référencée |   4.0   |

Remarques :

* La cible d'une référence (`ReferenceTarget`) est définie sous forme d'une chaîne de caractère : `{TABLE_CIBLE}.{COLONNE_CIBLE}` ( (ex : "SERVITUDE.IDSUP")
* Moyennant des références à des tables statiques, les références permettront de gérer plus proprement l'appartenance à des listes de valeur codifiée (`reference: doc_urba_type(code)`) que l'utilisation des énumérés.
* Dans un premier temps, les clés étrangères ne sont pas supportées mais il sera possible d'étendre le modèle pour être en mesure de définir la clé étrangère `(TYPEPSC,STYPEPSC) REFERENCES prescription_urba_type(code,sous_code)` au niveau de `ZONE_URBA` dans les standards CNIG.
* L'ajout des contraintes suivantes est à l'étude pour les futures versions :

| Propriété | Type  | Description                                           |
| --------- | ----- | ----------------------------------------------------- |
| `minimum` | `any` | Contrainte sur la valeur minimale autorisée (incluse) |
| `maximum` | `any` | Contrainte sur la valeur maximale autorisée (incluse) |


