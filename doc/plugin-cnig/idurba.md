# Validation des champs IDURBA

## Contexte

La validation des champs IDURBA est assurée pour les PLU, POS, CC, PLUi et PSMV à l'aide du plugin CNIG du validateur. Des retours sur cette validation sont pris en compte dans la version 4.0.x du validateur pour GpU 4.1.

## Formats de IDURBA

Le format de IDURBA a évolué dans la version 2017 des standards CNIG.

* Standard 2013 et 2014 (IdurbaFormatV1) : "{INSEE_OR_SIREN}(_?){YYYYMMDD}"

> La forme {INSEE_OR_SIREN}{YYYYMMDD} est la forme officielle. "_" est toléré puisqu'il a été temporairement présent dans les standards CNIG.

* Version supérieures (IdurbaFormatV2) : "{INSEE_OR_SIREN}_(PLU|PLUi|PLUI|POS|CC|PSMV)_{YYYYMMDD}(_{CodeDU})"

> PLUI est la forme officielle, PLUi est toléré puisqu'il a longtemps été accepté par GpU. Dans le nom de dossier, la forme officielle est bien "PLUi".

## Codes d'erreur associés à la validation des IDURBA.

### CNIG_IDURBA_INVALID : Validation du format de IDURBA dans DOC_URBA

En version 3.3 du GpU, en fonction de la version du standard, on connaît le format attendu pour IDURBA (v1 ou v2). Pour chaque champ nommé "IDURBA", on vérifie que cette forme est repectée. Si ce n'est pas le cas, une erreur avec le code CNIG_IDURBA_INVALID est renvoyée :

> La valeur du champ "IDURBA" ({VALUE}) ne respecte pas le format attendu ({IDURBA_FORMAT}.

Ceci créé vraissemblablement des WARNING non légitimes dans DOC_URBA où plusieurs versions de standard peuvent cohabiter.

En version 4.1 du GpU, dans DOC_URBA, on vérifie que l'IDURBA respecte l'un des deux formats connus au niveau DOC_URBA. Dans le cas contraire, on renvoie une erreur CNIG_IDURBA_INVALID :

> La valeur du champ "IDURBA" ({VALUE}) ne respecte pas le format attendu.


### CNIG_IDURBA_NOT_FOUND : Validation de l'existence d'une ligne avec IDURBA correspondant au nom de dossier dans DOC_URBA

En fonction de la version du standard et du nom de dossier du document, on connaît la valeur attendue pour IDURBA.

En version 3.3 et 4.1 de GpU, si aucune ligne ne correspond à l'IDURBA attendu dans DOC_URBA, le validateur renvoie une erreur CNIG_IDURBA_NOT_FOUND :

> Aucune ligne correspondant au document n’a été trouvée dans la table (IDURBA={EXPECTED_IDURBA})

### CNIG_IDURBA_MULTIPLE_FOUND : Validation de l'unicité de la ligne avec IDURBA correspondant au nom de dossier dans DOC_URBA

En fonction de la version du standard et du nom de dossier du document, on connaît la valeur attendue pour IDURBA.

En version 4.1 de GpU, si plusieurs lignes correspondent à l'IDURBA attendu dans DOC_URBA, le validateur renvoie une avertissement CNIG_IDURBA_MULTIPLE_FOUND :

> Plusieurs lignes correspondant au document trouvées dans la table DOC_URBA (IDURBA={EXPECTED_IDURBA})

### CNIG_IDURBA_UNEXPECTED : Vérification de cohérence entre IDURBA et NomDeDossier pour les tables autres que DOC_URBA

A partir de la version 4.1, pour les tables autre que DOC_URBA, on vérifie que IDURBA correspond bien à l'IDURBA attendu en fonction du nom de dossier. Dans le cas contraire, on renvoie une erreur CNIG_IDURBA_UNEXPECTED avec le message suivant :

> La valeur du champ "IDURBA" ({VALUE}) ne correspondant pas à la valeur attendue ({EXPECTED_VALUE})

Remarque : Ce contrôle est fait avec une tolérance sur la présence d'un "_" pour IdurbaFormatV1 et une tolérance entre PLUi et PLUI pour IdurbaFormatV2.


## Processus de validation des IDURBA

### Pré-validation

* Récupération du format de IDURBA en fonction de la version du standard
* Ajout d'une validation dédiée aux champs IDURBA du modèle

### Validation des valeurs lignes par lignes

* Validation des champs `DOC_URBA.IDURBA` : On vérifie que IDURBA correspond à un format connu (v1 ou v2) dans les tables, si ce n'est pas le cas : CNIG_IDURBA_INVALID
* Validation des champs {AUTRE_TABLE}.IDURBA : On vérifie que IDURBA correspond à la valeur attendue (document.tags.idurba), si ce n'est pas le cas : CNIG_IDURBA_UNEXPECTED

### Post-validation

Après la normalisation standard des données pour diffusion, on filtre `DATA/DOC_URBA.csv` pour conserver les lignes avec l'IDURBA attendu afin d'éviter des doublons au niveau WFS (les données de l'archive ne sont pas modifiées) :

* Si on ne trouve aucune ligne avec IDURBA attendu, une erreur `CNIG_IDURBA_NOT_FOUND` est ajoutée au rapport.

* Si on trouve une ligne avec `DOC_URBA.IDURBA` attendu :

    * On conserve la ligne
    * On stocke l'idurba correspondant dans `document.tags.idurba`
    * On stocke le typeref correspondant dans `document.tags.typeref`

* Si on trouve plusieurs lignes :

  * La dernière ligne trouvée est utilisée pour `document.tags.idurba` et `document.tags.typeref`
  * Un avertissement `CNIG_IDURBA_MULTIPLE_FOUND` est ajouté au rapport


