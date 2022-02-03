# Principe de fonctionnement des plugins

Les "plugins" sont en mesure d'exécuter des tâches à différentes étapes de la validation pour répondre aux besoins métiers :

* Avant la mise en correspondance des fichiers et du modèle (ex : modification d'extension, ajout de contrainte métier sur le modèle,...)
* Avant la validation (ex : détection de l'encodage des fichiers à partir des métadonnées)
* Après la validation (ex : recueil de métadonnées sur les données validées, contrôles supplémentaires, etc.)

Ils sont activés lors de l'appel au validateur, par exemple avec l'option `--plugins cnig` pour le [Géoportail de l'Urbanisme](https://www.geoportail-urbanisme.gouv.fr).
