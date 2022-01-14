
# metadata_to_json

## Description

Conversion de métadonnées XML dans un modèle pivot simplifié au format JSON.

## Exemple d'utilisation

Pour convertir [01.xml](../../validator-core/src/test/resources/metadata/01.xml) en [01-expected.json](../../validator-core/src/test/resources/metadata/01-expected.json) :

```bash
java -jar validator-cli.jar metadata_to_json \
    -i validator-core/src/test/resources/metadata/01.xml \
    -o validator-core/src/test/resources/metadata/01-expected.json
```

## Remarques

* Le modèle pivot est une simplification du modèle ISO 19915 se concentrant sur les éléments utilisés dans les profiles INSPIRE et CNIG.
* **ATTENTION : Ce modèle pivot JSON n'est pas standardisé et est susceptible d'évoluer**
