
# Modélisation des documents

Les documents sont modélisés par des modèles de document (DocumentModel). Un modèle de document décrit des fichiers (FileModel), à savoir
la position attendue du fichier dans le dossier en entrée et le type de fichier (métadonnée, table de données, PDF, etc.)


## Modèle de document (DocumentModel)

Le modèle de document est identifié par un nom (ex : cnig_PLU_2013).

Remarque : Le modèle de document est porteur d'une expression régulière qui permet de valider le nom du dossier à valider.

## Modèle de fichier (FileModel)

Le modèle de fichier est identifié par au nom au sein du modèle de document. Le fichier est décrit par :

* Un nom court (name) : Identifiant du modèle de fichier dans le modèle de document. Ex : COMMUNE
* Une expression régulière (regexp) : Position du fichier dans l'arborescence (si différent du nom court). Ex : COMMUNE_(0-9){5}
* Un champ indiquant si le fichier est obligatoire (mandatory) : Les valeurs OPTIONAL/WARN/ERROR permettent de jouer sur le niveau de gravité en cas d'absence du fichier.

Il existe plusieurs types de fichier décrit ci-après qui possède des contrôles et champs spécifiques.

### Table (type=table)

Représente une table (porteuse ou non d'une géométrie) dans l'un des formats suivant :

* Shapefile
* Mapinfo
* CSV
* GML (Simple Feature, i.e. une seule table dans le GML)

Remarque :
* La lecture des tables est effectuée à l'aide d'une conversion GDAL (ogr2ogr) en format CSV.
* Le champ géométrique doit être décrit sous le nom 'WKT' (comportement par défaut de ogr2ogr)

### Metadonnées (type=metadata)

Ce type de fichier permet de décrire une fiche de métadonnée.

### Metadonnées (type=pdf)

Ce type de fichier permet de décrire une fiche de métadonnée.

### Dossier (type=directory)

Représente un dossier dans l'arborescence (ex : Pieces_ecrites/).

Remarque : Ce type de fichier est surtout utile pour contrôler la présence d'un dossier (même vide) dans un document.

## Modèle de table (FeatureType)

Un modèle de table s'applique à un modèle de fichier de type "TABLE".

## Modèle d'attribut de table (AttributeType)

Un modèle d'attribut est décrit par :

* Un nom (name), ex : INSEE
* Une définition (definition), "Code INSEE de la commune"

Ce champ définit optionnellement des contraintes à l'aide de :

* Un champ "nullable" indiquant si la valeur peut-être vide
* Un champ taille (size) limitant la taille des chaînes de caractères
* Un champ "liste de valeur" à laquelle doit appartenir la valeur si elle est non nulle
* Un champ expression régulière (regexp) limitant la valeur du champs, ex : validation de code INSEE

Les attributs sont dérivés en plusieurs types possèdant chacun des contrôles et formats spécifiques décrits ci-après.

### Boolean (java.lang.Boolean)

Représente une valeur vraie ou fausse.

Formats supportés :
* Pour vrai : 1, T, t, Y, y
* Pour faux : 0, F, f, N, n

Format normalisé :
* Pour vrai : 1
* Pour faux : 0

### String (java.lang.String)

Représente une chaîne de caractère.

## Integer (java.lang.Integer)

Représente un entier.

### Date (java.util.Date)

Représente une date.

Formats supportés :
* "yyyyMMdd"
* "yyyy"
* "dd/MM/yyyy"

Format normalisé : "yyyyMMdd"

### Geometry (com.vividsolutions.jts.geom.Geometry)

Remarque :
* Les types dérivés Point, LineString, Polygon, MultiPoint, MultiLineString, MultiPolygon, GeometryCollection
permettent de restreindre les types de géométrie autorisées.
* Des conversions automatiques sont effectuées quand elles sont possibles (MultiPoint avec un seul Point => Point).
* Le validateur signale par des erreurs les conversions impossibles (elles peuvent bloquer l'intégration dans des systèmes du type PostGIS)

### Path (java.io.File)

Représente un chemin vers un fichier contenu dans le répertoire du document.

Contrôle spécifique : Présence du fichier dans le répertoire du document.

### Url (java.net.URL)

Représente un lien hypertexte.

Contrôle spéficique : Validité de l'URL (pas de tentative de téléchargement).


# Principe de fonctionnement

1) Chargement du modèle de document et préparation d'un dossier de validation

Le validateur prépare un répertoire de travail pour la validation où il écrira :
* Le rapport de validation
* Le répertoire DATA contenant les fichiers normalisés

2) Enumération des fichiers

3) Recherche des modèles de fichier correspondant aux fichiers

Remarque : Le validateur signale alors les fichiers sans correspondance avec le modèle et les fichiers non trouvés

4) Validation de chaque modèle de fichier

Pour chaque modèle de fichier, le validateur valide les fichiers associés.

Remarques :

* Dans le cas des tables, le validateur valide la structure (FeatureType) et les attributs (AttributeType)
* La validation des types d'attribut repose sur la possibilité de convertir une valeur dans le type décrit. Pour résumer : false, f, 0 sont des booléens, tutu n'est pas un booléen.

5) Ecriture du rapport d'erreur

Le validateur écrit un rapport dans le dossier

6) Normalisation des données

Le validateur génère un dossier DATA "à plat" contenant tous les fichiers validés et normalisés.

# Extensibilité

Le validateur permet l'ajout qui plugin qui vont exécuter des tâches à différentes étapes de la validation :

* Avant la mise en correspondance des fichiers et du modèle (ex : modification d'extension)
* Avant la validation (ex : détection de l'encodage des fichiers à partir des métadonnées)
* Après la validation (ex : receuil de métadonnées sur les données validée, contrôle supplémentaires, etc.)

Remarque : La normalisation des données est en pratique le premier post-traitement.
