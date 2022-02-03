
# POC Validateur PCRS

## Présentation

L'idée de départ de ce POC est d'étudier la possibilité de réutiliser [IGNF/validator](https://github.com/IGNF/validator) pour valider les données vecteurs numérisées conformément au standard [CNIG PCRS v2.0](https://cnigfr.github.io/PCRS/).

Des tests et échanges sont en cours avec différents producteurs de ces données. Ce document vise à faire un bilan de l'expérimentation et de ses limites.

## Évolutions traitées pour le POC

Les évolutions suivantes ont été traité au niveau du coeur du validateur pour valider les données [CNIG PCRS v2.0](https://cnigfr.github.io/PCRS/) :

### Validation d'un fichier à l'aide d'un schéma XSD

La validation des données GML en fonction du schéma XSD fournit par le standard CNIG PCRS v2.0 a été mise en oeuvre comme suit :

* Ajout de la possibilité de configurer la validation d'un fichier XML/GML au niveau du modèle (`"xsdSchema": "https://cnigfr.github.io/PCRS/schemas/CNIG_PCRS_v2.0.xsd"`)
* Ajout d'un code d'erreur spécifique aux erreurs de validation XSD : `XSD_SCHEMA_ERROR`
* Implémentation du contrôle à l'aide d'une classe standard java `javax.xml.validation.Validator` (adaptation d'un exemple de code fourni par un producteur PCRS).

Remarque : Ce développement est réutilisable dans d'autres contextes (ex : validation de fiche de métadonnée ou fichiers XML)

### Validation d'un fichier multi-table

On note dans [document.json](document.json) que le fichier `DONNEES` est de type `"multi_table"`.

Ce concept de fichier multi-table a été introduit pour la validation des données PCRS où un seul fichier GML contient plusieurs tables.

Remarque : Ce développement sera réutilisable dans d'autres contextes (ex : ajout du support de la validation de fichiers GeoPackage qui contiendraient eux aussi plusieurs table)

### Modèle de table optionnel

> TODO

### Support des géométries de type courbe

> TODO (préciser au passage qu'on avait avant des ATTRIBUTE_GEOMETRY_INVALID_FORMAT, voir si c'est bien le cas de geovendee-20210621 avec une ancienne version du validateur)

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


### Fausses alertes sur les types non déclarés dans le modèle

> TODO (reprendre mail d'explication sur MULTITABLE_UNEXPECTED sur des tables "PlanCorpsRueSimplifie" ou "{TypePCRS}_*" (table de relation) produites par ogr2ogr dans la conversion GML en CSV )

> TODO (préciser qu'il semble délicat de filtrer ces erreurs sans développer un plugin-pcrs dès lors que l'on conserve le principe du pivot CSV)

### Fausses alertes sur les dossiers

> TODO FILE_UNEXPECTED
