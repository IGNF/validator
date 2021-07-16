# JSON

## Description

Le type `JSON` permet d'indiquer que la valeur est une chaîne de caractère correspondant à un JSON valide.

## Format

Toute valeur respectant le format JSON, par exemple :

* `null`
* `true`
* `"Une valeur"`
* `{"tag1": "valeur 1","tag2": "valeur 2"}`

## Mapping

| Langage    | Type           |
| ---------- | -------------- |
| Java       | `java.net.URL` |
| JSON       | `any`          |
| PostgreSQL | `jsonb`        |

## Remarque

Au besoin, il sera possible d'étendre le modèle de contrainte (`ColumnConstraints`) pour ajouter la notion de validité au regard d'un schéma JSON.

