
# Modélisation des données

## Principaux concepts

| Concept                                                       | Description                                                        | Implémentation  |
|---------------------------------------------------------------|--------------------------------------------------------------------|-----------------|
| [Document](https://github.com/IGNF/validator-schema#document) | Description d'une arborescence de fichier/du contenu d'une archive | `DocumentModel` |
| [File](https://github.com/IGNF/validator-schema#file)         | Description d'un fichier (chemin, type, présence obligatoire,...)  | `FileModel`     |
| [Table](https://github.com/IGNF/validator-schema#table)       | Description d'une table                                            | `FeatureType`   |
| [Column](https://github.com/IGNF/validator-schema#column)     | Description d'une colonne d'une table                              | `AttributeType` |

## Diagramme de classe

Le schéma ci-après illustre les relations entre les modèles :

![Diagramme de classe](img/class-diagram.jpeg)

## Format de table supportés

On nottera que :

* La lecture des tables est effectuée à l'aide d'une conversion GDAL (ogr2ogr) en format CSV.
* Les formats suivants sont supportés :
  * Shapefile
  * Mapinfo
  * CSV
  * GML (Simple Feature, i.e. une seule table dans le GML)
* Le champ géométrique doit être décrit sous le nom "WKT"

Remarque : Ce dernier point est lié au comportement par défaut de ogr2ogr lors de la conversion en CSV. Une prochaine version du validateur apportera de la souplesse sur ce point.
