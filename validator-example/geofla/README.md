# Exemple GEOFLA_2015

## Données en entrée

Deux shapefiles :

* data/COMMUNE.shp
* data/LIMITE_COMMUNE.shp

## Configuration

### Mapping de fichiers

```
<?xml version="1.0" encoding="UTF-8"?>
<document>
  <name>GEOFLA</name>
  <files>
    <file>
      <type>table</type>
      <name>COMMUNE</name>
      <description>Fichier des communes</description>
      <path>COMMUNE</path>
      <mandatory>WARN</mandatory>
    </file>
    <file>
      <type>table</type>
      <name>LIMITE_COMMUNE</name>
      <description>Fichier des communes</description>
      <path>LIMITE_COMMUNE</path>
      <mandatory>WARN</mandatory>
    </file>
  </files>
</document>
```
[GEOFLA_2015/files.xml](GEOFLA_2015/files.xml)

### FeatureCatalogue


* LIMITE_COMMUNE : [GEOFLA_2015/types/COMMUNE.xml](GEOFLA_2015/types/LIMITE_COMMUNE.xml)

* COMMUNE : [GEOFLA_2015/types/COMMUNE.xml](GEOFLA_2015/types/COMMUNE.xml)
```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<featureType xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
	<typeName>COMMUNE</typeName>
    <attributes>
        <attribute>
            <name>ID_GEOFLA</name>
            <type>String</type>
            <definition>ID_GEOFLA</definition>
            <nullable>false</nullable>
        </attribute>
        <attribute>
            <name>CODE_COM</name>
            <type>String</type>
            <definition>CODE_COM</definition>
            <nullable>false</nullable>
        </attribute>
        <attribute>
            <name>INSEE_COM</name>
            <type>String</type>
            <definition>INSEE_COM</definition>
            <nullable>false</nullable>
        </attribute>
        <attribute>
            <name>NOM_COM</name>
            <type>String</type>
            <definition>NOM_COM</definition>
            <nullable>false</nullable>
        </attribute>
        <attribute>
            <name>STATUT</name>
            <type>String</type>
            <definition>STATUT</definition>
            <nullable>false</nullable>
            <listOfValues>
				<value><![CDATA[Capitale d'état]]></value>
				<value><![CDATA[Chef-lieu de canton]]></value>
				<value><![CDATA[Commune simple]]></value>
				<value><![CDATA[Préfecture de département]]></value>
				<value><![CDATA[Préfecture de région]]></value>
				<value><![CDATA[Sous-préfecture]]></value>
		    </listOfValues>
        </attribute>
        <attribute>
            <name>X_CHF_LIEU</name>
            <type>String</type>
            <definition>X_CHF_LIEU</definition>
            <nullable>false</nullable>
        </attribute>
        <attribute>
            <name>Y_CHF_LIEU</name>
            <type>String</type>
            <definition>Y_CHF_LIEU</definition>
            <nullable>false</nullable>
        </attribute>
        <attribute>
            <name>X_CENTROID</name>
            <type>String</type>
            <definition>X_CENTROID</definition>
            <nullable>false</nullable>
        </attribute>
        <attribute>
            <name>Y_CENTROID</name>
            <type>String</type>
            <definition>Y_CENTROID</definition>
            <nullable>false</nullable>
        </attribute>
        <attribute>
            <name>Z_MOYEN</name>
            <type>String</type>
            <definition>Z_MOYEN</definition>
            <nullable>false</nullable>
        </attribute>
        <attribute>
            <name>SUPERFICIE</name>
            <type>String</type>
            <definition>SUPERFICIE</definition>
            <nullable>false</nullable>
        </attribute>
        <attribute>
            <name>POPULATION</name>
            <type>String</type>
            <definition>POPULATION</definition>
            <nullable>false</nullable>
        </attribute>
        <attribute>
            <name>CODE_CANT</name>
            <type>String</type>
            <definition>CODE_CANT</definition>
            <nullable>false</nullable>
        </attribute>
        <attribute>
            <name>CODE_ARR</name>
            <type>String</type>
            <definition>CODE_ARR</definition>
            <nullable>false</nullable>
        </attribute>
        <attribute>
            <name>CODE_DEPT</name>
            <type>String</type>
            <definition>CODE_DEPT</definition>
            <nullable>false</nullable>
        </attribute>
        <attribute>
            <name>NOM_DEPT</name>
            <type>String</type>
            <definition>NOM_DEPT</definition>
            <nullable>false</nullable>
        </attribute>
        <attribute>
            <name>CODE_REG</name>
            <type>String</type>
            <definition>CODE_REG</definition>
            <nullable>false</nullable>
        </attribute>
        <attribute>
            <name>NOM_REG</name>
            <type>String</type>
            <definition>NOM_REG</definition>
            <nullable>false</nullable>
        </attribute>
        <attribute>
            <name>WKT</name>
            <type>MultiPolygon</type>
            <definition>WKT</definition>
            <nullable>false</nullable>
        </attribute>
    </attributes>
</featureType>
```




## Résultat

### Rapport de validation

[validation.xml](validation-expected/validation.xml)

```
<?xml version="1.0" encoding="UTF-8"?>
<Events xmlns="http://logging.apache.org/log4j/2.0/events">
  <Event logger="fr.ign.validator.report.ReportBuilderLegacy" timestamp="1466087763926" level="ERROR" thread="main">
    <Message><![CDATA[Feature | ATTRIBUTE_UNEXPECTED_VALUE | COMMUNE | ERROR | STATUT | 146 |  |  | GEOFLA | La valeur renseignée (Mauvaise valeur) ne correspond pas à une valeur autorisée (Capitale d'état, Chef-lieu de canton, Commune simple, Préfecture de département, Préfecture de région, Sous-préfecture).]]></Message>
  </Event>
</Events>
```
