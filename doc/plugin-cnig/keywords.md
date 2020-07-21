# Validation des mots clés en fonction des CSMD CNIG

Le nom de dossier du document n'est plus obligatoirement fourni sous forme d'un identifiant dans la fiche de métadonnées ( https://www.geoportail-urbanisme.gouv.fr/document/{NomDeDossier} ).

Les informations contenues dans le nom de dossier sont désormais disponibles sous forme de mots clés associés à des thésaurus de sorte qu'il soit possible, par exemple dans le cadre du moissonnage ATOM du GpU, de récupérer les informations et reconstituer le nom de dossier à partir des fiches de métadonnées.

Le plugin CNIG du validateur contrôle la présence de ces mots clés et la cohérence avec le nom de dossier d'un document.

## Documents de référence

* [DU - Mots clés](http://cnig.gouv.fr/wp-content/uploads/2019/03/190308_Consignes_saisie_metadonnees_DU_v2019-03.pdf#page=8&zoom=auto,-45,753)
* [SUP - Mots clés](http://cnig.gouv.fr/wp-content/uploads/2019/03/190308_Consignes_saisie_metadonnees_SUP_v2019-03.pdf#page=8&zoom=auto,-45,763)
* [SCOT - Mots clés](http://cnig.gouv.fr/wp-content/uploads/2019/03/190308_Consignes_saisie_metadonnees_SCOT_v2019-03.pdf#page=7&zoom=auto,-45,555)

## Méthode de validation

Le validateur procède comme suit :

* Calcul de la liste des mots clés attendus en fonction du nom de dossier

> Par exemple, pour 25349_PLU_20010101_B, on attend un mot clé "PLU" pour le type de document, un mot clé "25349" pour l'emprise, un mot clé "B" pour le code DU.

* Pour chaque mot clé attendu

  * Recherche du mot clé en fonction du nom de thesaurus spécifié dans les CSMD (erreur CNIG_METADATA_KEYWORD_NOT_FOUND si non trouvé).
  * Contrôle de cohérence de la valeur avec le nom de dossier (erreur  CNIG_METADATA_KEYWORD_INVALID en cas d'incohérence)

## Ressources

* [keywords.ods](keywords.ods) : tableau de synthèse des CSMD DU/SUP/SCOT pour l'implémentation
* [issue 120](https://github.com/IGNF/validator/issues/120) : issue github détaillant l'implémentation initiale.





