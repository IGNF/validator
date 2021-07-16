# URL

## Description

Le type `Url` permet d'indiquer que la valeur doit correspondre à une URL valide.

## Format

Les URL relatives et absolues sont autorisées dès lors qu'elles sont valides.

Remarque : L'existence de la resource pointée par l'URL n'est pas validée.

## Mapping

| Langage    | Type           |
| ---------- | -------------- |
| Java       | `java.net.URL` |
| JSON       | `string`       |
| PostgreSQL | `string`       |
