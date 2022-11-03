# CNIG_PCRS_v2.0
## Description

Modèle **expérimental** pour la validation des jeux de données respectant le standard [CNIG PCRS v2.0](https://cnigfr.github.io/PCRS/).
## Modélisation des fichiers

Le fichier [document.json](document.json) décrit la structure d'un dossier ou d'une archive contenant deux fichiers :

* Un fichier `DONNEES`, requis, correspondant au fichier GML stockant une liste de table (`*.gml`)
* Un fichier `METADONNEES`, optionnel, correspondant à la fiche de métadonnées du jeu de données (`*.xml`)

## Modélisation des tables

Il n'y a pas de modèle de table associé à ce modèle puisque la validation de la structure. La validation de la structure et du contenu du fichier GML est réalisée à l'aide du fichier https://cnigfr.github.io/PCRS/schemas/CNIG_PCRS_v2.0.xsd.

On nottera toutefois que les tables possiblement présente dans le GML sont listées et configurées avec un modèle de table "automatique" (`{"tableModel":"auto"}`). Le validateur récupère la liste des attributs présents dans les données ce qui permet :

* La validation des géométries
* La validation de l'unicité de `gml_id`
* Le calcul de statistiques pour chaque table (bbox, nombre d'object,...)


## Remarques

* Le standard permet théoriquement un nommage en `*.xml` pour le fichier de DONNEES. Le validateur ne le permet pas dans cette version car ceci induit une ambiguïté avec la fiche de métadonnées.
* Les erreurs de validations XSD sont associées à un code "XSD_SCHEMA_ERROR". Pour l'heure, seule la ligne est correspondante est récupérée pour être ajoutée au niveau de l'erreur.

