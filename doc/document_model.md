# Fiche référence des modèles de standard

## Structure

Un modèle de standard se présente sous forme de dossier, le nom du dossier est préférablement sans espace (les remplacer par des `_`).

Ce dossier est composé de trois parties :

- Un fichier `files.json`, qui décrit les informations intrinsèques du modèle telles que son nom. C'est dans ce fichier que la structure des documents du standard est spécifiée.
- Un dossier `types`, qui contient les informations et contraintes sur les fichiers des documents décrits par le standard.
- Un dossier `codes`, qui contient les listes de données autorisées dans certains attributs (colonnes) des tables.

Exemple :
```
cnig_PLU_2025
├── types
│   ├── DOC_URBA.json
│   ...
├── codes
│   ├── ListeEtatPLU.csv
│   ...
└── files.json
```

## Fichier `files.json`

Le fichier files.json est la colonne vertebrale du document. Il permet de décrire la structure des documents du standard.

Puisque c'est un document json, il comporte évidement `{` et `}` en début et en fin de fichier, ce qui définit l'objet principal du fichier.

### Description du standard

Chaque entrée dans l'objet principale est présentée ci dessous. Les propriétés doivent toutes être renseignées, sauf si elles sont indiquées comme optionnelles.

##### `id` (obligatoire)

Comme la plupart des objets présents dans un modèle de standards, l'objet principal de `files.json` comporte un `id`. Les `id` sont des suites de 32 charactères alphanumériques. Chaque `id` doit être unique pour l'ensemble du modèle.

##### `name` (obligatoire)

Le nom technique du modèle. Évitez les espaces.

##### `title` (optionnel)

Le nom général du modèle. Il est souvent identique au `name`.

##### `description` (optionnel)

Description du modèle.

##### `abstract` (optionnel)

Dans la plupart des cas, la valeur `false` est appropriée. Cette propriété est assignée à `true` dans des modèles de modèles utilisés par le Géoportail de l'Urbanisme.

##### `constraints` (optionnel)

La propriété `constraints` est un objet composé de deux propriétés :
- `folderName` permet de valider le nom des dossiers des documents testés. On valide le nom du dossier si il vérifie le regex indiqué.
- `metadataSpecification` est la valeur attendue dans la fiche de métadonnée des documents testés.

##### `files` et `codes` (obligatoires)

Ces deux entrées sont des listes (donc introduites par `[` et `]`), qui définissent les fichiers du modèles et les listes de valeurs respectivement. Ces objets sont décrits dans les parties subséquentes.

### Exemple de `files.json`

```json
{
    "abstract": false,
    "id": "3935ffc17d3dabe0d77ea7d3c416695d",
    "name": "cnig_PLU_2025",
    "title": "cnig_PLU_2025",
    "description": "cnig_PLU_2025",
    "files": [
        ...
    ],
    "codes": [
        ...
    ],
    "constraints": {
        "folderName": "[a-zA-Z0-9]+_PLU_[0-9]{8}(_[a-zA-Z])?",
        "metadataSpecification": "CNIG PLU v2025"
    }
}
```

### Description de la structure des documents

L'objet `files` de `files.json` est une liste qui permet d'indiquer quelles sont les fichiers du modèle. Ce n'est pas ici que l'on va décrire le contenu des fichiers, on explicite ici ou ils se trouvent, quels sont leurs nom, etc...

Chaque entrée dans l'objet `files` correspond à un fichier du standard et comprend les proriétés suivantes :

##### `id` (obligatoire)

Suit les mêmes règles que l'`id` présenté précédement.

##### `name` (obligatoire)

Le nom technique du fichier.

##### `title` (optionnel)

Le nom général du fichier. `name` et `title` sont souvent identiques.

##### `description` (optionnel)

La description du fichier.

##### `path` (obligatoire)

Chemin relatif par rapport à la racine du document. Ce champ fait correspondre le nom du fichier à un regex.

Attention ! Ne pas inclure l'extension du fichier dans le regex !

##### `mandatory` (obligatoire)

Indique si la présence du fichier en question est rédhibitoire à sa validation. Les valeurs possibles sont :
- `ERROR` : l'absence du fichier dans le document va renvoyer une erreur, ce qui rend le document invalide.
- `WARN` : l'absence du fichier dans le document va remonter un avertissement dans le raport, mais cela n'invalide pas le document.
- `OPTIONAL` : l'absence du fichier dans le document ne pose aucun problème.

##### `type` (obligatoire)

Le type du fichier. Les valeurs autorisées sont les suivantes :
- `directory` : Dossier.
- `metadata` : Fiche de métadonnées XML au format ISO 19115 (`.xml`).
- `pdf` : Fichier PDF (`.pdf`).
- `table` : Table de données géographique ou non (`.csv`, `.dbf`, `.shp`, `.geojson`, `.gml`).
- `multi_table` : Un ensemble de tables stockées dans un seul fichier (`.gml`, `.gpkg`).

##### `tableModel` (obligatoire si et seulement si `type` est une `table`)

Chemin vers le modèle correspondant à la table. On fait appel ici au dossier `types` du modèle.

##### `tables` (obligatoires si et seulement si `type` est une `mutli_table`)

Si le type du fichier est une multitable, nous allons décrire chacune des tables que le fichier contient dans une seule liste.

Ainsi, `tables` est une liste de tables, et chacun des éléments de la liste est un objet avec les propriétés suivantes :
- `name` : le nom technique du modèle de la couche.
- `mandatory` : quel est l'impact de l'absence de la couche sur la validaté du document testé (voir plus haut).
- `path` : regex permettant de faire correspondre le nom de la couche du document testé avec le modèle.
- `tableModel` : Chemin vers le modèle correspondant à la table.

### Énumération des codes

Dans l'objet `codes` de `files.json`, on va énumérer tous les codes des modèles.

Nous completerons cette partie dans la partie sur les codes.

### Exemple complet de files.json

```json
{
    "abstract": false,
    "id": "3935ffc17d3dabe0d77ea7d3c416695d",
    "name": "cnig_PLU_2025",
    "title": "cnig_PLU_2025",
    "description": "cnig_PLU_2025",
    "files": [
        {
            "type": "table",
            "path": "Donnees_geographiques/[AB0-9]{5}_HABILLAGE_LIN_[0-9]{8}",
            "mandatory": "OPTIONAL",
            "tableModel": "./types/HABILLAGE_LIN.json",
            "id": "ee0d570522f83a665134675aa5241d57",
            "name": "HABILLAGE_LIN",
            "title": "HABILLAGE_LIN"
        },
        {
            "name": "GEOPACKAGE",
            "id" : "86d02bead41f4186be66260cf4727a21",
            "description": "ceci est un exemple de multi_table non présent dans cnig_PLU_2025",
            "type": "multi_table",
            "path": "[^\\/]*",
            "mandatory": "ERROR",
            "tables": [
                {
                "name": "prefix_procedure",
                "mandatory": "ERROR",
                "path": "((?:[0-9]{2,3}|2a|2b)[0-9]{4}[0-9]{4}_[0-9]{4}_[0-9]{4})_procedure",
                "tableModel": "./types/prefixeppr_procedure.json"
                },
                ...
            ]
        },
        ...
    ],
    "codes": [
        {
            "data": "./codes/PrescriptionLUrbaType.csv",
            "id": "023592188a2b11f0e88362966107deef",
            "name": "PrescriptionLUrbaType",
            "title": "PrescriptionLUrbaType"
        },
        ...
    ],
    "constraints": {
        "folderName": "[a-zA-Z0-9]+_PLU_[0-9]{8}(_[a-zA-Z])?",
        "metadataSpecification": "CNIG PLU v2025"
    }
}
```

## Dossier `types`

Dans le dossier `types`, on va renseigner quelles sont les caractéristiques des différentes `tables` que nous avons annoncées dans la partie `files` de `files.json`. Le `path` renvoie directement vers un fichier du dossier `types`.

Chaque fichier du dossier est un json mais le contenu varie si c'est une `table` ou une `multi_table`.

Nous allons décrire ici comment définir un modèle d'une table simple. Si le type est une `multi_table`, le fichier est un 

### Propriétés classiques

##### `id` (obligatoire)

Encore une fois, chaque modèle de table à un `id`. Attention, ce n'est **pas** le même que celui renseigné dans `files` de `files.json`.

##### `name` (obligatoire)

Le nom technique du modèle de table.

##### `title` (optionnel)

Le nom général du modèle de table. Il est courant qu'il soit identique au `name`.

##### `description` (optionnel)

La description de la table en question.

### `columns` (obligatoire)

C'est dans cette propriété que nous allons définir les colonnes de notre modèle de tables.

C'est une liste d'attributs, ou chacun des élément de la liste correspond à une colonne de la table. Chacun des éléments à les propriétés suivantes :

##### `id` (obligatoire)

`id` similaire aux différents `id` rencontrés : une chaine de 32 charactères alphanumériques.

##### `name` (obligatoire)

Le nom technique de l'attribut.

##### `title` (optionnel)

Le nom général de l'attribut. Il est souvent identique au `name`.

##### `description` (optionnel)

La description de l'attribut.

##### `type` (obligatoire)

Le type de l'attribut peut prendre les valeurs suivantes :
- `Boolean` : Vrai ou faux
- `String` : Chaîne de caractères
- `Integer` : Valeur numérique entière
- `Double` : Valeur numérique en virgule flottante
- `Date` : Jour, mois et année
- `Geometry` : Géométrie de type non spécifié
- `Point` : Géométrie de type point
- `LineString` : Géométrie de type polyligne
- `Polygon` : Géométrie de type polygone
- `MultiPoint` : Géométrie de type multi-point
- `MultiLineString` : Géométrie de type multi-polylign
- `MultiPolygon` : Géométrie de type multi-polygone
- `GeometryCollection` : Géométrie de type hétérogène
- `Path` : Chemin vers un fichier dans le document.
- `Url` : URL

##### `constraints` (optionnel)

Liste de contraintes associées à la colonne.

Attention ! Les contraintes de clé étrangère, ou de liste de valeurs, ne sont pas à renseigner ici.

Chaque contrainte peut prendre les valeurs suivantes :
- `presenceRequired` : `true` si la colonne doit être présente dans la table.
- `required` : `true` si toutes les valeurs de la colonne doivent être remplies. Ainsi, 'false' permet l'intégration de valeurs nulles dans la colonne.
- `unique` : `true` si toutes les valeurs non-nulles de la colonne doivent être uniques.
- `maxLength` : entier indiquant la longueur maximale du champ.
- `pattern` : contrainte sous forme d'une expression régulière.

### Exemple de `columns` :

```json
"columns": [
    {
        "type": "Url",
        "id": "17bad748b0bb4361455ac976ac41c13b",
        "name": "URLREG",
        "title": "URLREG",
        "description": "URL ou URI qui pointe sur le fichier du règlement papier complet scanné",
        "constraints": {
            "required": false,
            "presenceRequired": true,
            "unique": false,
            "maxLength": 254
        }
    },
    ...
]
```

### `constraints` (optionnel)

Cette propriété des tables permet d'ajouter des controles qui valide des données à partir d'autres tables, listes de valeurs, ou même d'autre colonnes de la table.

Deux types de contraintes de tables existent : les `conditions` et les `foreignKeys`, qui sont toutes deux des listes.

#### `conditions`

La propriété condition est une liste de valeurs textuelles, chacune ayant le format d'une condition SQL. Le nom des colonnes à utiliser dans ces expressions sont les noms des colonnes précédement décrites.

Exemple (qui n'existe pas dans cnig_PLU_2025) :

```sql
URLREG LIKE '%.ru'
```

#### `foreighKeys`

Comme son nom l'indique, cette liste de valeur permet de réaliser des clés étrangères vers d'autres tables ou listes de valeurs. On utilise la aussi une syntaxe SQL, en particulier en utilisant le terme `REFERENCES`.

Attention, pour les listes de valeurs, on va faire référence à un fichier dans le dossier `codes`. Il faut alors :
- creer un csv dans le dossier `codes`, avec le nom souhaité, et au moins une colonne de valeurs, avec un en-tête.
- ajouter ce fichier dans la propriété `codes` de `files.json`.

Exemple :
- dans un fichier json du dossier `types` :
```json
"columns": [
    {
      "type": "String",
      "id": "c8d5869d42965b27d3316711a2db57fc",
      "name": "ETAT"
    },
    ...
],
"constraints": {
    "foreignKeys": [
        "(ETAT) REFERENCES ListeEtatPLUi(ETAT)"
    ]
}
```
- dans le fichier `codes/ListeEtatPLUi.csv` :
```csv
ETAT
01
02
03
```
- dans le fichier `files.json`:
```json
"codes": [
    {
        "data": "./codes/ListeEtatPLUi.csv",
        "id": "4b26536f095974b6dd19845fa013fdc0",
        "name": "ListeEtatPLUi",
        "title": "ListeEtatPLUi"
    }
]
```

### Exemple complet d'un fichier du dossier `types`:

```json
{
    "id": "48ac04777ad427d939e25ed2350f8787",
    "name": "DOC_URBA",
    "title": "DOC_URBA",
    "description": "Table contenant la liste des documents d'Urbanisme PLU ou POS dont la numérisation ou l'élaboration sous fourme numérique est engagée",
    "columns": [
        {
            "type": "String",
            "id": "6518d7cc46cf4007341e061d835f7fc3",
            "name": "DATAPPRO",
            "title": "DATAPPRO",
            "description": "Date de la dernière approbation administrative du document d'urbanisme",
            "constraints": {
                "required": false,
                "presenceRequired": true,
                "unique": false,
                "pattern": "[0-9]{8}"
            }
        },
        {
            "type": "String",
            "id": "c8d5869d42965b27d3316711a2db57fc",
            "name": "ETAT",
            "title": "ETAT",
            "description": "Etat juridique du document d'urbanisme",
            "constraints": {
                "required": true,
                "presenceRequired": true,
                "unique": false,
                "maxLength": 2
            }
        },
        ...
    ],
    "constraints": {
        "conditions": [
            "DATAPPRO IS NOT NULL OR ETAT NOT LIKE '03'",
            ...
        ],
        "foreignKeys": [
            "(ETAT) REFERENCES ListeEtatPLUi(ETAT)",
            ...
        ]
    }
}
```