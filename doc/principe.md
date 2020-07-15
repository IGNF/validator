
# Principe de fonctionnement du validateur de document

## 1) Chargement du modèle de document

Le modèle de document est chargé en fonction des options `--config/--version`.

## 2) Préparation d'un dossier de validation

Le validateur prépare un répertoire de travail pour la validation où il écrira :

* Le rapport de validation (`validation/validation.jsonl`)
* Un répertoire contenant les fichiers normalisés (`validation/DATA`)

## 3) Enumération des fichiers (`DocumentFile`) avec des extensions supportées

## 4) Recherche des modèles de fichier (`FileModel`) correspondant aux fichiers (`DocumentFile`)

Le validateur signale les fichiers sans correspondance avec le modèle et les fichiers non trouvés.

## 5) Validation des fichiers en fonction des modèles

Pour chaque fichier trouvé (`DocumentFile`), le validateur valide en fonction du `FileModel` trouvé. Dans le cas des tables, le validateur valide le contenu en fonction du `FeatureType` décrivant la table.

Remarque : La validation des types d'attributs repose sur la possibilité de convertir une valeur dans le type décrit. Pour résumer : `false`, `f` et `0` sont des booléens, `tutu` n'est pas un booléen.

## 6) Création d'une base de données de validation

Les données sont chargées dans une base de validation pour les contrôles d'ensemble sur les données.

## 7) Validation des contraintes d'ensemble à l'aide de la base de validation

Les contraintes d'ensemble (unicité, référence,...) sont validées à l'aide de la base de validation.

## 8) Normalisation des données

Le validateur génère un dossier `validation/DATA` contenant "à plat" tous les fichiers validés et normalisés.

