# Principe de fonctionnement du validateur de document

## 1) Chargement du modèle de document

Le modèle de document (`DocumentModel`) est chargé à partir de son URL renseignée par l'option `--model=<DOCUMENT_MODEL_URL>`.

## 2) Préparation d'un dossier de validation

Le validateur prépare un répertoire de travail pour la validation où il écrira :

* Le rapport de validation (`validation/validation.jsonl`) avec une erreur au format JSON par ligne (voir [JSON Lines](http://jsonlines.org/))
* Un répertoire contenant les fichiers normalisés (`validation/DATA`)

## 3) Énumération des fichiers avec des extensions supportées

Le validateur liste les fichiers (`DocumentFile`) ayant des extensions connues dans le document à valider (dossier `--input`).

## 4) Recherche des modèles de fichier correspondant aux fichiers

Le validateur recherche le modèle de fichier (`FileModel`) correspondant aux `DocumentFile` trouvés à l'étape précédente.

## 5) Validation des fichiers en fonction des modèles

Le validateur effectue une validation sur chaque `DocumentFile` ayant un `FileModel` associé.

Dans le cas des tables (`FileModel` de type `TableModel`), le validateur valide le contenu en fonction du `FeatureType` décrivant la table.

On soulignera que la validation des types d'attributs repose sur la possibilité de convertir une valeur dans le type décrit. Pour résumer : `false`, `f` et `0` sont des booléens, `tutu` n'est pas un booléen.

## 6) Création d'une base de données de validation

Les tables sont chargées dans une base de validation pour les contrôles d'ensemble sur les données.

## 7) Validation des contraintes d'ensemble à l'aide de la base de validation

Les contraintes d'ensemble (unicité, référence,...) sont validées à l'aide de la base de validation.

## 8) Normalisation des données

Le validateur génère un dossier `validation/DATA` contenant "à plat" tous les fichiers validés et normalisés.

