# Path

## Description

Le type `Path` permet d'indiquer que la valeur correspond à un chemin vers un fichier par rapport à la racine d'un document.

## Format

* Les chemins doivent être définis en utilisant des `/` (pas de `\`)
* L'utilisation de référence dans les fichiers est autorisée (`annexe/fichier.pdf#chapitre-8`)

## Mapping

| Langage    | Type           |
| ---------- | -------------- |
| Java       | `java.io.File` |
| JSON       | `string`       |
| PostgreSQL | `string`       |
