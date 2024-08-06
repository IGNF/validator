# Modélisation des données et des erreurs

## Description

Cette documentation décrit schématiquement les modèles JSON de validation du validateur `IGNF/validator` pour ses versions `4.0` et supérieures.

## Vue d'ensemble des concepts

| Concept                            | Description                                                                     | Implémentation   |
| ---------------------------------- | ------------------------------------------------------------------------------- | ---------------- |
| [Document](#document)              | Modélisation du contenu d'un dossier ou d'une archive.                          | `DocumentModel`  |
| [File](#file)                      | Modélisation d'un fichier du document (chemin, type, présence obligatoire,...). | `FileModel`      |
| [Table](#table)                    | Modélisation d'une table matérialisée dans un fichier.                          | `FeatureType`    |
| [Column](#column)                  | Modélisation d'une colonne d'une table.                                         | `AttributeType`  |
| [ValidatorError](#validationerror) | Modélisation des erreurs (résultat de la validation)                            | `ValidatorError` |

## Modélisation des données

### Document

Le concept de [Document.json](Document.json) est utilisé pour modéliser un dossier ou une archive contenant des fichiers.

| Propriété              | Type                  | Description                                             | Obligatoire | Version |
| ---------------------- | --------------------- | ------------------------------------------------------- | :---------: | :-----: |
| `document.name`        | `string`              | Le nom du modèle au niveau système (ex : cnig_PLU_2013) |      O      |   4.0   |
| `document.title`       | `string`              | Le nom du modèle pour l'affichage                       |      N      |   4.0   |
| `document.description` | `string`              | La description du modèle                                |      N      |   4.0   |
| `document.files`       | `File[]`              | La liste des fichiers attendus dans le dossier          |      O      |   4.0   |
| `document.constraints` | `DocumentConstraints` | Contraintes au niveau du document                       |      N      |   4.0   |

### DocumentConstraints

| Propriété                        | Type     | Description                                 | Obligatoire | Version |
| -------------------------------- | -------- | ------------------------------------------- | :---------: | :-----: |
| `document.folderName`            | `RegExp` | Nom de dossier attendu pour le document     |      N      |   4.0   |
| `document.metadataSpecification` | `string` | Valeur attendue dans la fiche de métadonnée |      N      |   4.0   |

### File

Les fichiers d'un document peuvent correspondre à des tables, des sous-dossiers, des PDF ou des fichiers de métadonnées. Ils sont décrits par les propriétés suivantes :

| Propriété          | Type                  | Description                                            | Obligatoire | Version |
| ------------------ | --------------------- | ------------------------------------------------------ | :---------: | :-----: |
| `file.name`        | `string`              | Le nom identifiant le fichier                          |      O      |   4.0   |
| `file.title`       | `string`              | Le nom du fichier pour affichage                       |      N      |   4.0   |
| `file.description` | `string`              | La description du fichier                              |      N      |   4.0   |
| `file.type`        | [FileType](#filetype) | Le type de fichier                                     |      O      |   4.0   |
| `file.path`        | `RegExp`              | Chemin relatif par rapport à la racine du document (1) |      N      |   4.0   |
| `file.tableModel`  | `string`              | Liens vers le [modèle de table](#table)                |    N (2)    |   4.0   |

Remarques :

* (1) Les extensions ne sont pas spécifiées dans les modèles. L'expression régulière est complétée automatiquement en fonction du `type`, ce qui simplifie le support de plusieurs formats pour les tables (c.f. [FileType](#filetype) )
* (2) Il est conseillé de spécifier l'URL du modèle de table qui sera par défaut `./types/{file.name}.json`

### FileType

Les types de fichiers supportés sont les suivants :

| Type          | Description                                                                       | Version |
| ------------- | --------------------------------------------------------------------------------- | :-----: |
| `directory`   | Dossier permettant d'en valider l'existence                                       |   4.0   |
| `metadata`    | Fiche de métadonnées XML au format ISO 19115 (`.xml`)                             |   4.0   |
| `pdf`         | Fichier PDF (`.pdf`)                                                              |   4.0   |
| `table`       | Table de données géographique ou non (`.csv`, `.dbf`, `.shp`, `.geojson`, `.gml`) |   4.0   |
| `multi_table` | Un ensemble de tables stockées dans un seul fichier (`.gml`, `.gpkg`)             |   4.2   |

Remarque : L'ajout du concept `multi_table` est lié à la validation des données [PCRS vecteur](https://github.com/cnigfr/PCRS) où un même fichier GML contient plusieurs collections. Il est étendu au format GeoPackage pour le [Géostandards Risques](https://github.com/cnigfr/Geostandards-Risques).

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

Le validateur supporte une liste finie de types :

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


## Modélisation des erreurs

### ValidatorError

Le concept de [ValidatorError](ValidatorError.json) est utilisé pour modéliser les erreurs rapportés dans les rapports de validation. Les principales propriétés sont les suivantes :

| Nom             | description                                                          | Exemple                                         |
| --------------- | -------------------------------------------------------------------- | ----------------------------------------------- |
| `code`          | Code de l'erreur                                                     | `ATTRIBUTE_GEOMETRY_INVALID`                    |
| `level`         | Niveau de gravité de l'erreur (DEBUG, INFO, WARNING, ERROR ou FATAL) | `ERROR`                                         |
| `message`       | Message d'erreur détaillé                                            | `"La géométrie n'est pas valide"`               |
| `documentModel` | Nom du modèle de document                                            | `cnig_PLU_2017`                                 |
| `fileModel`     | Nom du modèle de fichier                                             | `ZONE_URBA`                                     |
| `attribute`     | Nom de la colonne concernée                                          | `WKT`                                           |
| `file`          | Chemin du fichier concerné                                           | `Donnees_geographique/ZONE_URBA.shp`            |
| `id`            | Numéro de la ligne dans le fichier / la table                        | `228`                                           |
| `featureBbox`   | Boite englobante de l'objet (lonMin,latMin,lonMax,latMax)            | `-2.156289,47.3279265,-2.1558376,47.3281971`    |
| `errorGeometry` | Localisation de l'erreur géométrique (format WKT, projection CRS:84) | `POINT (-2.1559630631294775 47.32809837488598)` |
| `featureId`     | Identifiant de l'objet (si disponible)                               | `id-6b2bd0b0-c593-4e40-8e2e-2037e35b4685`       |

Les propriétés suivantes sont spécifiques aux erreurs de validation XML sur la base de schémas XSD (`code=XSD_SCHEMA_ERROR`) : 

| Nom               | description                                      | Exemple                                                                                                      |
| ----------------- | ------------------------------------------------ | ------------------------------------------------------------------------------------------------------------ |
| `xsdErrorCode`    | Code de l'erreur                                 | [cvc-datatype-valid.1.2.3](https://knowledge.xmldation.com/fr/support/validator/cvc-datatype-valid-1-2-1/fr) |
| `xsdErrorMessage` | Message d'erreur standard du validateur XSD      | `'bad-nature' n'est pas une valeur valide du type d'union 'NatureAffleurantPCRSTypeType'.`                   |
| `xsdErrorPath`    | XPath correspondant à l'erreur de validation XML | `//PlanCorpsRueSimplifie/featureMember/AffleurantPCRS[@id='AffleurantEnveloppePCRS.0']/nature`               |

Les niveaux de gravité des erreurs sont utilisés comme suit :

* `FATAL` correspond à un échec de la validation (problème technique, plantage du validateur)
* `ERROR` matérialise problème bloquant pour l'intégration des données dans une base de données (ex : problème de type)
* `WARNING` matérialise un problème non bloquant pour l'intégration des données dans une base de données est rencontré
* `INFO` permet d'ajouter au rapport un message d'information visible par les utilisateurs du validateur (ex : projection lue dans une fiche de métadonnées)
* `DEBUG` permet d'ajouter un rapport un message d'information non visible par les utilisateurs (ex : version de GDAL)

