# Boolean

## Description

Représente une valeur "vraie" ou "fausse"

## Formats

Les formats suivants sont supportés implicitement lors de la validation :

* 1, 0 (normalisation CSV en sortie pour import)
* T, F
* t, f
* Y, N
* y, n

Remarque : `true` et `false` ne sont **pas** acceptés.

## Mapping

| Langage    | Type      |
| ---------- | --------- |
| Java       | `Boolean` |
| JSON       | `boolean` |
| PostgreSQL | `boolean` |
