
# POC Validateur PCRS

## Présentation

L'idée de départ de ce POC est d'étudier la possibilité de réutiliser [IGNF/validator](https://github.com/IGNF/validator) pour valider les données vecteurs numérisées conformément au standard [CNIG PCRS v2.0](https://cnigfr.github.io/PCRS/).

Des tests et échanges sont en cours avec différents producteurs de ces données. Ce document vise à faire un bilan de l'expérimentation et de ses limites.

## Évolutions traitées pour le POC

Les évolutions suivantes ont été traités au niveau du coeur du validateur pour valider les données [CNIG PCRS v2.0](https://cnigfr.github.io/PCRS/) :

### Validation d'un fichier à l'aide d'un schéma XSD

La validation des données GML en fonction du schéma XSD du standard CNIG PCRS v2.0 a été mise en oeuvre comme suit :

* Ajout de la possibilité de configurer la validation d'un fichier XML/GML au niveau du modèle (`"xsdSchema": "https://cnigfr.github.io/PCRS/schemas/CNIG_PCRS_v2.0.xsd"`)
* Ajout d'un code d'erreur spécifique aux erreurs de validation XSD (`XSD_SCHEMA_ERROR`)
* Implémentation du contrôle à l'aide d'une classe standard java (`javax.xml.validation.Validator`) par adaptation d'un exemple de code fourni par un producteur PCRS.

Remarque : Ce développement est réutilisable dans d'autres contextes (ex : validation de fiche de métadonnée ou fichiers XML)

### Validation d'un fichier multi-table

On note dans [document.json](document.json) que le fichier `DONNEES` est de type `"multi_table"`.

Ce concept de fichier multi-table a été introduit pour la validation des données PCRS où un seul fichier GML contient plusieurs tables.

Remarque : Ce développement sera réutilisable dans d'autres contextes (ex : ajout du support de la validation de fichiers GeoPackage qui contiendraient eux aussi plusieurs table)

### Modèle de table optionnel

Dans la mesure où le modèle XSD permet de valider la structure des données, il a été décidé dans un premier temps de ne pas imposer la fourniture d'un modèle de table pour chaque type PCRS.

Pour ce faire, un notion de modèle automatique a été introduite. Le modèle considéré est fonction des données, avec détection des champs identifiants et géométrique pour validation de ces aspects.

### Support des géométries de type courbe

Le support des géométries de type courbe (voir [documentation postgis](https://postgis.net/docs/using_postgis_dbmanagement.html#SQL_MM_Part3)) a été ajouté pour permettre la lecture des géométries correspondantes par le validateur.

Remarque : Ceci permet entre autres d'éviter des erreurs `ATTRIBUTE_GEOMETRY_INVALID_FORMAT` qui étaient produites par l'incapacité de geotools de lire les géométries produites par ogr2ogr de GDAL.

## Limites de l'expérimentation

### Déclaration obligatoire de la projection des données

Il serait pertinent de rendre optionnelle la déclaration de la projection des données à validateur dans l'appel au validateur dans la mesure où elle est déclarée dans les fichiers GML.

### Message d'erreur de validation XSD

Les messages d'erreur de validation XSD ne sont pas lisible par le commun des mortels, mais c'est un problème lié à l'utilisation d'un validateur XSD générique qui a amené à y renoncer dans d'autres contextes (ex : validation des fiches de métadonnées du GpU)

### Contexte des erreurs de validation XSD

Pour l'heure, seul le message et numéro de ligne dans le fichier GML permettent de localiser une erreur validation XSD.

Il semble possible d'ajouter dans certains cas le type et l'identifiant de l'objet concerné, mais la récupération de ces informations n'est pas triviale à partir des erreurs `javax.xml.validation.Validator`.

### Nommage des fichiers de données

Le standard permet théoriquement un nommage en `*.xml` pour le fichier de DONNEES. Le validateur ne le permet pas dans cette version car ceci induit une ambiguïté avec la fiche de métadonnées.

### Absence de contrôles métiers

Les seuls contrôles mis en oeuvre sont ceux qui peuvent être portés par le modèle (i.e. par le coeur du validateur et la validation XSD).

En ce sens, il n'y a pas de contrôle du type "la classe EmpriseEchangePCRS est obligatoire et elle doit contenir un seul objet".

En fonction des retours, il conviendra d'étudier la pertinence d'évolutions modèles (ex : ajout d'un concept `minNumFeatures` et `maxNumFeatures`), voire le développement plugin dédié PCRS.

### Fausses alertes sur les types non déclarés dans le modèle

Des avertissements non pertinents peuvent être levés avec le code d'erreur `MULTITABLE_UNEXPECTED` avec ce type de message :

* La table 'PlanCorpsRueSimplifie' n'est pas prévue dans le modèle de validation
* La table 'AffleurantGeometriquePCRS_enveloppe' n'est pas prévue dans le modèle de validation.
* La table 'AffleurantGeometriquePCRS_ligne' n'est pas prévue dans le modèle de validation.
* La table 'AffleurantGeometriquePCRS_point' n'est pas prévue dans le modèle de validation.

Ils sont liés au fait que la lecture des GML est réalisée à l'aide du driver GMLAS de GDAL qui matérialise la racine du document GML et relations sous forme de tables.

Les avertissements de type `MULTITABLE_UNEXPECTED` sur les tables `PlanCorpsRueSimplifie` et `{TypePCRS}_*` peuvent être ignorés dans l'immédiat.

Il est délicat de les filtrer sans développer un plugin dédié pour le PCRS ("plugin-pcrs") dès lors que l'on continue de s'appuyer sur le driver GMLAS de GDAL et le pivot CSV pour la lecture des données.

### Fausses alertes sur les dossiers

Un avertissement de `FILE_UNEXPECTED` peut être levé sur un dossier contenant le fichier GML validé.

A notre connaissance, il n'y a pas de règle de nommage pour des dossiers PCRS et les fichiers GML.

Il conviendra peut-être d'ignorer l'arborescence des dossiers (option `--flat` dans l'appel au validateur)

### Risque de caractères interdits dans les noms de fichiers

En amont de l'appel au validateur, il y a généralement une opération d'extraction d'archive ZIP où les caractères présents dans les noms de fichiers sont contrôlés sans forcément une grande tolérance pour limités les risques de sécurité.

Il conviendrait d'éviter les caractères accentués et les espaces dans les noms des fichiers GML (peut-être nommer le GML en fonction de l'identifiant de l'`EmpriseEchangePCRS`?)

