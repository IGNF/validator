# Date

## Description

Représente un jour, un mois et une année.

## Formats

Les formats suivants sont supportés implicitement lors de la validation :

* `yyyyMMdd`
* `dd/MM/yyyy`
* `yyyy-MM-dd`
* `yyyy/MM/dd`
* `yyyy` (année seule)

La lecture est stricte : une date inexistante (ex : `20151717`) est refusée.

## Mapping

| Langage    | Type             |
| ---------- | ---------------- |
| Java       | `java.util.Date` |
| JSON       | `string`         |
| PostgreSQL | `date`           |
