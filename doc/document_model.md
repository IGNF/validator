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

##### `id` (optionnel)

Comme la plupart des objets présents dans un modèle de standards, l'objet principal de `files.json` peut comporter un `id` (suite de 32 caractères alphanumériques, unique pour l'ensemble du modèle). Il est utilisé par les outils de gestion des modèles et **ignoré par le validateur**.

##### `name` (obligatoire)

Le nom technique du modèle. Évitez les espaces.

##### `title` (optionnel)

Le nom général du modèle. Il est souvent identique au `name`.

##### `description` (optionnel)

Description du modèle.

##### `abstract` (optionnel)

Dans la plupart des cas, la valeur `false` est appropriée. Cette propriété est assignée à `true` dans des modèles de modèles utilisés par le Géoportail de l'Urbanisme. Elle est ignorée par le validateur.

##### `constraints` (optionnel)

La propriété `constraints` est un objet composé de deux propriétés :
- `folderName` permet de valider le nom des dossiers des documents testés. On valide le nom du dossier si il vérifie le regex indiqué.
- `metadataSpecification` est la valeur attendue dans la fiche de métadonnée des documents testés.

##### `files` (obligatoire) et `codes` (optionnel)

Ces deux entrées sont des listes (donc introduites par `[` et `]`), qui définissent les fichiers du modèles et les listes de valeurs respectivement (`codes` vaut une liste vide par défaut). Ces objets sont décrits dans les parties subséquentes.

Un modèle chargé en http(s) ne peut référencer que des URL http(s) (modèles de tables et listes de codes) : une URL `file:` y est refusée.

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

Le nom technique du fichier. Pour une `table`, il est aussi le nom de la table dans la base de validation et des fichiers normalisés : il doit être composé de lettres, de chiffres et de `_`, sans commencer par un chiffre (`[A-Za-z_][A-Za-z0-9_]*`), sinon le modèle est rejeté.

##### `title` (optionnel)

Le nom général du fichier. `name` et `title` sont souvent identiques.

##### `description` (optionnel)

La description du fichier.

##### `path` (obligatoire)

Chemin relatif par rapport à la racine du document. Ce champ fait correspondre le fichier à une expression régulière (syntaxe JAVA, insensible à la casse).

- Le `path` contient le chemin complet, **dossiers inclus** (ex : `Pieces_ecrites/3_Reglement/[AB0-9]{5}_reglement_[0-9]{8}`). Il ne contient pas le `name`, qui n'est qu'un identifiant technique du modèle.
- Attention ! Ne pas inclure l'extension du fichier dans le regex ! Elle est ajoutée automatiquement selon le `type` (ex : `.pdf`, `.csv`/`.dbf`/`.shp`... pour une `table`, `.gml`/`.gpkg` pour une `multi_table`).

##### `mandatory` (optionnel, `WARN` par défaut)

Indique si la présence du fichier en question est rédhibitoire à sa validation. Les valeurs possibles sont :
- `ERROR` : l'absence du fichier dans le document va renvoyer une erreur, ce qui rend le document invalide.
- `WARN` : l'absence du fichier dans le document va remonter un avertissement dans le raport, mais cela n'invalide pas le document.
- `OPTIONAL` : l'absence du fichier dans le document ne pose aucun problème.

##### `type` (obligatoire)

Le type du fichier. Les valeurs autorisées sont les suivantes :
- `directory` : Dossier.
- `metadata` : Fiche de métadonnées XML au format ISO 19115 (`.xml`).
- `pdf` : Fichier PDF (`.pdf`).
- `table` : Table de données géographique ou non (`.csv`, `.dbf` pour un shapefile, `.tab`, `.gml`, `.gpkg`).
- `multi_table` : Un ensemble de tables stockées dans un seul fichier (`.gml`, `.gpkg`).

##### `tableModel` (optionnel, uniquement si `type` est une `table`)

Chemin vers le modèle correspondant à la table. Par défaut, le modèle de table est cherché dans le dossier `types` du modèle (`./types/{name}.json` ; pour un modèle distant dont l'URL ne se termine pas par `files.json` : `./{nom du modèle}/types/{name}.json`). La valeur `auto` indique que la structure de la table est déduite des données (pas de modèle de table).

##### `tables` (obligatoires si et seulement si `type` est une `multi_table`)

Si le type du fichier est une multitable, nous allons décrire chacune des tables que le fichier contient dans une seule liste.

Ainsi, `tables` est une liste de tables, et chacun des éléments de la liste est un objet avec les propriétés suivantes :
- `name` : le nom technique du modèle de la couche (mêmes règles de nommage que le `name` d'une `table`). Une couche du fichier est associée à ce modèle si son nom est égal à `name` (sans tenir compte de la casse).
- `mandatory` : non pris en charge actuellement (ignoré : l'absence d'une couche ne produit pas d'erreur).
- `path` : regex (insensible à la casse, sans extension) permettant de faire correspondre le nom de la couche à l'intérieur du fichier avec le modèle, lorsque `name` ne suffit pas. Contrairement au `path` du fichier, il ne désigne pas un chemin dans le document mais une couche dans le fichier.
- `tableModel` : Chemin vers le modèle correspondant à la table (optionnel, mêmes règles par défaut et valeur `auto` que pour une `table`).

### Énumération des codes

Dans l'objet `codes` de `files.json`, on va énumérer toutes les listes de codes du modèle (voir l'exemple de la partie `foreignKeys`). Chaque élément est un objet avec les propriétés suivantes :
- `name` : le nom de la liste de codes, utilisé comme nom de table dans la base de validation (lettres, chiffres et `_`, sans commencer par un chiffre).
- `title` (optionnel) : le nom général de la liste.
- `data` (optionnel) : chemin ou URL du fichier CSV. Par défaut `./codes/{name}.csv` pour un modèle local (`file:`), `./{nom du modèle}/codes/{name}.csv` pour un modèle distant (http(s)).

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

Nous allons décrire ici comment définir un modèle d'une table simple. Le format est le même pour les tables d'une `multi_table` (un fichier par élément de `tables`).

### Propriétés classiques

##### `id` (optionnel)

Encore une fois, chaque modèle de table peut avoir un `id` (ignoré par le validateur). Attention, ce n'est **pas** le même que celui renseigné dans `files` de `files.json`.

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

Le nom technique de l'attribut. Il est utilisé comme nom de colonne dans la base de validation : il doit être composé de lettres, de chiffres et de `_`, sans commencer par un chiffre (`[A-Za-z_][A-Za-z0-9_]*`), sinon le modèle est rejeté.

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

Attention ! Les contraintes de clé étrangère portant sur plusieurs colonnes sont à renseigner dans les `constraints` de la table (voir plus bas).

Chaque contrainte peut prendre les valeurs suivantes :
- `presenceRequired` : `true` (par défaut) si la colonne doit être présente dans la table.
- `required` : `true` (par défaut) si toutes les valeurs de la colonne doivent être remplies. Ainsi, `false` permet l'intégration de valeurs nulles dans la colonne.
- `unique` : `true` si toutes les valeurs non-nulles de la colonne doivent être uniques (`false` par défaut).
- `minLength` et `maxLength` : entiers indiquant la longueur minimale et maximale du champ.
- `pattern` : contrainte sous forme d'une expression régulière (type `String`).
- `enum` : liste des valeurs autorisées (type `String`).
- `reference` : référence à une colonne d'une autre table ou liste de codes, au format `TABLE.COLONNE`.

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

La propriété condition est une liste de valeurs textuelles, chacune ayant le format d'une condition SQL. Le nom des colonnes à utiliser dans ces expressions sont les noms des colonnes précédement décrites. Les conditions ne sont contrôlées qu'avec l'option `--enable-conditions` (SQL exécuté tel quel : à réserver aux modèles de confiance).

Exemple (qui n'existe pas dans cnig_PLU_2025) :

```sql
URLREG LIKE '%.ru'
```

Une condition peut aussi être décrite sous forme d'objet, avec un `message` lisible qui remplace l'expression SQL dans les messages d'erreur (les deux formes peuvent être mélangées dans la même liste) :

```json
"conditions": [
    "DATAPPRO IS NOT NULL OR ETAT NOT LIKE '03'",
    {
        "sql": "ETAT NOT LIKE '03' OR (SELECT COUNT(*) FROM DOC_URBA d WHERE d.ETAT LIKE '03') = 1",
        "message": "Un seul document en vigueur (ETAT = 03) est autorisé"
    }
]
```

L'erreur `DATABASE_CONSTRAINT_MISMATCH` affiche alors : `Une règle de remplissage conditionnelle n'est pas respectée. (Un seul document en vigueur (ETAT = 03) est autorisé)`.

#### `foreignKeys`

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