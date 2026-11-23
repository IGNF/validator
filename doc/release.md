# Publication d'une version

Ce document décrit la publication d'une version du validateur : tag Git, image Docker et release GitHub avec les binaires. L'environnement de développement est décrit dans [developpement.md](developpement.md).

## Versions

| | Format | Exemple |
|-|--------|---------|
| développement (`pom.xml` sur `master`) | `X.Y.Z-SNAPSHOT` | `4.8.0-SNAPSHOT` |
| release | `X.Y.Z` | `4.8.0` |
| tag Git | `vX.Y.Z` | `v4.8.0` |

## Prérequis

- Un environnement de build complet (JDK 21, Maven, ogr2ogr : voir [developpement.md](developpement.md#prérequis)), car `release:prepare` relance les tests.
- Les droits de push sur `IGNF/validator`.
- [GitHub CLI](https://cli.github.com/) pour créer la release (`sudo apt-get install gh`, puis `gh auth login`). À défaut, la release peut être créée depuis l'interface web.
- Les outils de paquets `.deb` et `.rpm` (voir [developpement.md](developpement.md#paquets-deb-et-rpm-optionnel)).

## Préparer

1. Se placer sur `master`, à jour avec `origin`, sans modification locale :

   ```bash
   git checkout master
   git pull
   git status   # "rien à valider" et pas de commit en avance
   ```

2. Vérifier que la CI est verte sur le dernier commit de `master` ([Actions](https://github.com/IGNF/validator/actions)).

3. Dans [CHANGELOG.md](../CHANGELOG.md), vérifier que les entrées de la version portent son numéro (`vX.Y.Z - ...`). Pour une version `X.Y.0`, ajouter les sections `## X.Y.x`. Commiter et pousser.

## Publier

Remplacer `X.Y.Z` par la version (ex : `4.8.0`) et `X.Y.Z+1` par la suivante (ex : `4.8.1`).

```bash
# 1. supprimer les fichiers laissés par une release précédente (sinon release:prepare la reprend)
rm -f release.properties pom.xml.releaseBackup

# 2. passer en version X.Y.Z, relancer les tests, créer le tag vX.Y.Z, puis passer en X.Y.Z+1-SNAPSHOT
#    (deux commits "[maven-release-plugin] ...", rien n'est poussé : pushChanges=false)
mvn -B release:prepare \
  -DreleaseVersion=X.Y.Z \
  -DdevelopmentVersion=X.Y.Z+1-SNAPSHOT

# 3. vérifier les deux commits et le tag avant de pousser
git log --oneline -3
git show vX.Y.Z --stat | head

# 4. pousser : le tag déclenche la publication de l'image Docker
#    ghcr.io/ignf/validator:X.Y.Z, X.Y et latest
git push origin master
git push origin vX.Y.Z

# 5. construire les binaires depuis le tag (validator-cli.jar, .deb et .rpm dans validator-cli/target/)
git checkout vX.Y.Z
make package
java -jar validator-cli/target/validator-cli.jar version   # doit afficher X.Y.Z

# 6. créer la release GitHub avec les binaires
gh release create vX.Y.Z --title "vX.Y.Z" --notes "Voir [CHANGELOG.md](https://github.com/IGNF/validator/blob/vX.Y.Z/CHANGELOG.md)" \
  validator-cli/target/validator-cli.jar \
  validator-cli/target/*.deb \
  validator-cli/target/*.rpm

# 7. revenir sur master et nettoyer
git checkout master
rm -f release.properties pom.xml.releaseBackup
```

Sans `-B` ni les options `-DreleaseVersion` et `-DdevelopmentVersion`, `mvn release:prepare` demande les versions et le tag de façon interactive.

## Vérifier

- La release est publiée : <https://github.com/IGNF/validator/releases>.
- Le workflow [Docker](https://github.com/IGNF/validator/actions/workflows/docker.yml) du tag est vert, et l'image fonctionne :

  ```bash
  docker run --rm ghcr.io/ignf/validator:X.Y.Z version
  ```

## En cas de problème

**`git push origin vX.Y.Z` : `src refspec vX.Y.Z does not match any`** (en français : `le spécificateur de référence source vX.Y.Z ne correspond à aucune référence`). Le tag n'existe pas, car `release:prepare` s'est arrêté avant de le créer. En général, c'est parce que des fichiers sont modifiés ou non suivis (`Cannot prepare the release because you have local modifications`). Vérifier avec `git status`, commiter ou retirer ces fichiers, supprimer `release.properties`, puis relancer depuis l'étape 1.

**`release:prepare` échoue pendant les tests.** Rien n'a été commité. Corriger, commiter, puis relancer depuis l'étape 1.

**`release:prepare` est interrompu après les commits** (ex : le tag existe déjà). Commencer par regarder l'état :

```bash
git log --oneline -5
git tag -l 'vX.Y.*'
grep -m1 '<version>' pom.xml
```

Si `release.properties` est cohérent, reprendre avec `mvn release:prepare -Dresume=true`. Sinon, annuler les commits locaux non poussés (`git reset --hard origin/master`) et supprimer le tag local (`git tag -d vX.Y.Z`), puis relancer depuis l'étape 1.

**Erreur après le push.** Ne pas supprimer ni déplacer un tag déjà poussé, car l'image Docker et la release en dépendent. Publier une version corrective `X.Y.Z+1` à la place.
