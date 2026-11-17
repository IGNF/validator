# Écrire un modèle de document

Ce guide présente pas à pas l'écriture d'un modèle de document (« modèle de standard »). La liste complète des propriétés, de leurs types et de leurs valeurs par défaut est dans la **référence** : [Modélisation des données et des erreurs](../validator-core/src/main/resources/schema/README.md).

## Arborescence

Un modèle se présente sous forme d'un dossier (nom de préférence sans espace) :

```
cnig_PLU_2025
├── files.json          structure des documents : fichiers attendus et listes de codes
├── types               un modèle de table (colonnes et contraintes) par table
│   ├── DOC_URBA.json
│   ...
└── codes               listes de valeurs autorisées (CSV avec en-tête)
    ├── ListeEtatPLUi.csv
    ...
```

Le validateur charge le modèle à partir de l'URL de `files.json` (option `--model`, ex : `file:///chemin/cnig_PLU_2025/files.json`). Les modèles de tables et les listes de codes sont cherchés par défaut dans `types/{name}.json` et `codes/{name}.csv`.

## `files.json` : les fichiers du document

```json
{
    "name": "cnig_PLU_2025",
    "title": "cnig_PLU_2025",
    "constraints": {
        "folderName": "[a-zA-Z0-9]+_PLU_[0-9]{8}(_[a-zA-Z])?",
        "metadataSpecification": "CNIG PLU v2025"
    },
    "files": [
        {
            "name": "DOC_URBA",
            "type": "table",
            "path": "Donnees_geographiques/DOC_URBA",
            "mandatory": "ERROR"
        },
        {
            "name": "REGLEMENT",
            "type": "pdf",
            "path": "Pieces_ecrites/3_Reglement/[AB0-9]{5}_reglement_[0-9]{8}",
            "mandatory": "WARN"
        },
        {
            "name": "GEOPACKAGE",
            "type": "multi_table",
            "path": "[^\\/]*",
            "mandatory": "ERROR",
            "tables": [
                { "name": "procedure", "path": "[0-9]{9}_procedure" }
            ]
        }
    ],
    "codes": [
        { "name": "ListeEtatPLUi" }
    ]
}
```

* `constraints.folderName` contrôle le nom du dossier du document, `constraints.metadataSpecification` la spécification déclarée dans la fiche de métadonnées.
* Chaque fichier est trouvé par son `path` : une expression régulière (insensible à la casse) du chemin complet **dossiers inclus** et **sans extension**. L'extension est ajoutée selon le `type` (`table` : `.csv`, `.dbf`, `.tab`, `.gml`, `.gpkg` ; `multi_table` : `.gml`, `.gpkg` ; `pdf`, `metadata` (`.xml`), `directory`).
* `mandatory` : `ERROR`, `WARN` (par défaut) ou `OPTIONAL` selon l'impact de l'absence du fichier.
* Une `multi_table` (GML, GeoPackage) décrit ses couches dans `tables` : une couche est associée à un modèle par son `name` (sans tenir compte de la casse) ou par l'expression régulière `path`.
* Pour une table, `tableModel` est facultatif (`./types/{name}.json` par défaut) ; la valeur `auto` déduit les colonnes des données.

## `types/DOC_URBA.json` : les colonnes d'une table

```json
{
    "name": "DOC_URBA",
    "columns": [
        {
            "name": "DATAPPRO",
            "type": "String",
            "constraints": { "required": false, "pattern": "[0-9]{8}" }
        },
        {
            "name": "ETAT",
            "type": "String",
            "constraints": { "maxLength": 2 }
        }
    ],
    "constraints": {
        "conditions": [
            "DATAPPRO IS NOT NULL OR ETAT NOT LIKE '03'",
            {
                "sql": "ETAT NOT LIKE '03' OR (SELECT COUNT(*) FROM DOC_URBA d WHERE d.ETAT LIKE '03') = 1",
                "message": "Un seul document en vigueur (ETAT = 03) est autorisé"
            }
        ],
        "foreignKeys": [
            "(ETAT) REFERENCES ListeEtatPLUi(ETAT)"
        ]
    }
}
```

* Les contraintes de colonne `required` et `presenceRequired` valent `true` par défaut : il faut indiquer `"required": false` pour autoriser les valeurs vides.
* Les types disponibles (`String`, `Integer`, `Date`, `MultiPolygon`, `Path`, `Url`...) et leurs formats sont décrits dans la [référence](../validator-core/src/main/resources/schema/README.md#columntype).
* `conditions` : expressions SQL que chaque ligne doit respecter, contrôlées uniquement avec l'option `--enable-conditions` (SQL exécuté tel quel : à réserver aux modèles de confiance). Le `message` remplace l'expression dans l'erreur `DATABASE_CONSTRAINT_MISMATCH`.
* `foreignKeys` : clés étrangères (sur une ou plusieurs colonnes) vers une autre table du document ou vers une liste de codes.

## `codes/ListeEtatPLUi.csv` : une liste de valeurs

```csv
ETAT
01
02
03
```

La liste est déclarée dans `codes` de `files.json` et devient une table utilisable dans `foreignKeys` ou dans la contrainte de colonne `reference` (`ListeEtatPLUi.ETAT`).

## Points d'attention

* Les noms des tables, des colonnes et des listes de codes doivent respecter `[A-Za-z_][A-Za-z0-9_]*` (lettres, chiffres et `_`, sans commencer par un chiffre) : ils sont utilisés dans la base de validation et pour les fichiers normalisés. Un modèle qui ne les respecte pas est rejeté.
* Un modèle chargé en http(s) ne peut référencer que des URL http(s) (modèles de tables et listes de codes).
* Ne pas mettre d'extension dans les `path`.

## Exemples

* [Exemples de modèles de document](../validator-core/src/test/resources/config-json/README.md) (tests du validateur)
* Modèles CNIG du Géoportail de l'Urbanisme : [geoportail-urbanisme.gouv.fr/standard](https://www.geoportail-urbanisme.gouv.fr/standard/)
