# Metadata



| name                      | type                      | title                                | multiplicity | 
|---------------------------|---------------------------|--------------------------------------|--------------| 
| fileIdentifier            | String                    | File identifier                      | [1]          | 
| title                     | String                    | Resource title                       | [1]          | 
| abstract                  | String                    | Resource abstract                    | [1]          | 
| type                      | String                    | Resource type                        | [1]          | 
| locators                  | OnlineResource[]          | Resource locator                     | [0..*]       | 
| identifier                | String                    | Unique resource identifier           | [1]          | 
| language                  | LanguageCode              | Resource langage                     | [0..*]       | 
| topicCategory             | String                    | Topic category                       | [1]          | 
| keywords                  | Keywords                  | Keywork                              | [1..*]       | 
| extents                   | Extent[]                  | Extents with geographic bounding box | [1..*]       | 
| referenceSystemIdentifier | ReferenceSystemIdentifier | Référentiel de Coordonnées           | [1]          | 
| dateOfPublication         | Date                      | "Date of publication"                | [0..1]       | 
| dateOfLastRevision        | Date                      | Date of last revision                | [0..1]       | 
| dateOfCreation            | Date                      | Date of creation                     | [0..1]       | 
| characterSet              | CharacterSet              | Resource Character Set               | [0..1]       | 
| contraints                | Contraint[]               | Resource constraints                 | [0..*]       | 
| distributionFormats       | Format                    | Encodage (nom et version)            | [0..*]       | 
| spatialRepresentationType | String                    | Type de représentation géographique  | [0..1]       | 
| lineage                   | String                    | Lineage                              | [1]          | 
| spatialResolutions        | Resolution                | Spatial resolution                   | [0..*]       | 
| specifications            | Specification             | Specification title and degree       | [0..*]       | 
| contact                   | ResponsibleParty          | "Responsible party"                  | [1]          | 
| metadataContact           | ResponsibleParty          | Metadata point of contact            | [1..*]       | 
| metadataDate              | Date                      | Metadata date                        | [1]          | 
| metadataLanguage          | LanguageCode              | Metadata langage                     | [1]          | 



## fileIdentifier (String)

### Description

_File identifier_ is the identifier of the metadata.

### XPath

```(xpath)
fileIdentifier
```

### References

* INSPIRE_GUIDELINE_2017 - 2.2 General requirements / 2.2.1 File identifier (p11)
* CNIG_MD_DU - 1) Identification des données / fileIdentifier (p5)


## title (String)

### Description

_Resource title_ is the title of the resource.

### XPath

```(xpath)
identificationInfo[1]/*/citation/*/title
```

### References

* INSPIRE_GUIDELINE_2017 - 2.3 Identification info section / 2.3.1 Resource title (p14)
* INSPIRE_GUIDELINE_2013 - 2.2 Identification / 2.2.1 Resource title (p17)
* CNIG_MD_DU - 1) Identification des données / Intitulé de la resource (p4)


## abstract (String)

### Description

_Resource abstract_ is the detailed description of the resource.

### XPath

```(xpath)
identificationInfo[1]/*/abstract
```

### References

* INSPIRE_GUIDELINE_2017 - 2.3 Identification info section / 2.3.2 Resource abstract (p15)
* INSPIRE_GUIDELINE_2013 - 2.2 Identification / 2.2.2 Resource abstract (p18)
* CNIG_MD_DU - 1) Identification des données / Résumé de la resource (p4)


## type (String)

### Description

_Resource type_ represents... TODO

### XPath

```(xpath)
hierarchyLevel
```

### References

* INSPIRE_GUIDELINE_2013 - 2.2 Identification / 2.2.3 Resource Type (p20)
* CNIG_MD_DU - 1) Identification des données / Type de la resource (p5)


## locators (OnlineResource)

### Description

_Resource locators_ provides the locations of the resource according to different services (WMS, WFS, Download, etc.).


### Constraints

* Multiplicity : [0..*]

### XPath

```(xpath)
distributionInfo/*/transferOptions/*/onLine/*/linkage
```

### References

* INSPIRE_GUIDELINE_2013 - 2.2 Identification / 2.2.4 Resource locator (p21)
* CNIG_MD_DU - 1) Identification des données / Localisateur de la ressource (p5)


## identifier (String)

### Description

The _Unique resource identifier_ is the resource identifier.

### XPath

```(xpath)
dentificationInfo[1]/*/citation/*/identifier/*/code
```

### References

* INSPIRE_GUIDELINE_2013 - 2.2 Identification / 2.2.5 Unique resource identifier (p24)
* CNIG_MD_DU - 1) Identification des données / Identificateur de ressource unique (p5)


## language (LanguageCode)

### Description

_Resource langage_ is the language of the resource (ex : fre). It is defined as a codeList.


### XPath

```(xpath)
identificationInfo[1]/*/language
```

### References

* INSPIRE_GUIDELINE_2013 - 2.2 Identification / 2.2.7 Resource langage (p26)
* CNIG_MD_DU - 1) Identification des données / Langue de la ressource (p6)


## topicCategory (String)

### Description

_Topic category_ represents ...

### XPath

```(xpath)
identificationInfo[1]/*/topicCategory
```

### References

* INSPIRE_GUIDELINE_2013 - 2.3 Classification of spatial data and services / 2.3.1 Topic category (p27)
* CNIG_MD_DU - 2) Classification des données et services géographiques / Catégorie thématique (p7)


## keywords (Keywords)

### Description

_Keywork_ represents ...


### XPath

```(xpath)
identificationInfo[1]/*/descriptiveKeywords/*/keyword
identificationInfo[1]/*/descriptiveKeywords/*/thesaurusName
```

### References

* INSPIRE_GUIDELINE_2013 - 2.3 Classification of spatial data and services / 2.4 Keyword (p31)
* CNIG_MD_DU - 3) Mots-clés (p8)


## extents (Extent)

### Description

_Extents with geographic bounding box_ represents ...


### XPath

```(xpath)
identificationInfo[1]/*/extent/*/geographicElement/*/westBoundLongitude
identificationInfo[1]/*/extent/*/geographicElement/*/eastBoundLongitude
identificationInfo[1]/*/extent/*/geographicElement/*/southBoundLatitude
identificationInfo[1]/*/extent/*/geographicElement/*/northBoundLatiTude
```

### References

* INSPIRE_GUIDELINE_2013 - 2.5 Geographic location / 2.5.1 Geographic bounding box (p35)
* CNIG_MD_DU - 4) Situation géographique / Rectangle de délimitation géographique (p9)


## referenceSystemIdentifier (ReferenceSystemIdentifier)

### Description

_Reference system identifier_ represents ...

### XPath

```(xpath)
referenceSystemInfo/*/referenceSystemIdentifier/*/code
```

### References

* CNIG_MD_DU - 4) Situation géographique / Référentiel de coordonnées (p9)


## dateOfPublication (Date)

### Description

_Date of publication_ represents ...

### XPath

```(xpath)
identificationInfo[1]/*/citation/*/date[./*/dateType/*/text()='publication’/*/date
```

### References

* INSPIRE_GUIDELINE_2013 - 2.6 Temporal reference / 2.6.2 Date of publication (p38)


## dateOfLastRevision (Date)

### Description

_Date of last revision_ represents ...

### XPath

```(xpath)
identificationInfo[1]/*/citation/*/date[./*/dateType/*/text()='revision']/*/date
```

### References

* INSPIRE_GUIDELINE_2013 - 2.6 Temporal reference / 2.6.3 Date of last revision (p40)
* CNIG_MD_DU - 5) Références temporelles / Dates de référence (p10)


## dateOfCreation (Date)

### Description

_Date of creation_ represents ...

### XPath

```(xpath)
identificationInfo[1]/*/citation/*/date[./*/dateType/*/text()='creation']/*/date
```

### References

* INSPIRE_GUIDELINE_2013 - 2.6 Temporal reference / 2.6.4 Date of creation (p38)


## characterSet (CharacterSet)

### Description

_Resource Character Set_ represents ...

### XPath

```(xpath)
identificationInfo[1]/*/characterSet
```

### References

* CNIG_MD_DU - Encodage - (p6)


## contraints (Contraint)

### Description

_Resource constraints_ represents ...

### XPath

```(xpath)
identificationInfo[1]/*/resourceConstraints/*
```

### References

* INSPIRE_GUIDELINE_2013 - 2.9 Constraints related to access and use (p51-55)


## distributionFormats (Format)

### Description

_distribution formats_ represents ...


### XPath

```(xpath)
distributionInfo/*/distributionFormat/*/name
distributionInfo/*/distributionFormat/*/version
```

### References

* CNIG_MD_DU - Encodage - (p6)


## spatialRepresentationType (String)

### Description

_spatial representation type_ represents ...

### XPath

```(xpath)
identificationInfo[1]/*/spatialRepresentationType
```

### References

* CNIG_MD_DU - Type de représentation géographique - (p7)


## lineage (String)

### Description

_Lineage_ represents ...


### XPath

```(xpath)
dataQualityInfo/*/lineage/*/statement
```

### References

* INSPIRE_GUIDELINE_2013 - 2.7 Quality and validity / 2.7.1 Lineage (p42)
* CNIG_MD_DU - 6) Qualité et validité / Généalogie (p10)


## spatialResolutions (Resolution)

### Description

_Spatial resolution_ represents ...

### XPath

```(xpath)
identificationInfo[1]/*/spatialResolution/*/equivalentScale/*/den
ominator (equivalent scale)
identificationInfo[1]/*/spatialResolution/*/distance (distance)
```

### References

* INSPIRE_GUIDELINE_2013 - 2.7 Quality and validity / 2.7.2 Spatial resolution (p45)
* CNIG_MD_DU - 6) Qualité et validité / Résolution spatiale (p10)


## specifications (Specification)

### Description

_Specification title and degree_ represents ...

### XPath

```(xpath)
dataQualityInfo/*/report/*/result/*/specification 
dataQualityInfo/*/report/*/result/*/pass
```

### References

* INSPIRE_GUIDELINE_2013 - 2.8 Conformity / 2.8.2 Specification (p49)
* CNIG_MD_DU - 7) Conformité / Spécification (p12)
* 
* INSPIRE_GUIDELINE_2013 - 2.8 Conformity / 2.8.1 Degree (p48)
* CNIG_MD_DU - 7) Conformité / Degré (p12)


## contact (ResponsibleParty)

### Description

_contact_ describe the responsible organisation and point of contact for the described resource.

### XPath

```(xpath)
identificationInfo[1]/*/pointOfContact/*/organisationName
identificationInfo[1]/*/pointOfContact/*/address/*/electronicMailAddress
identificationInfo[1]/*/pointOfContact/*/role
```

### References

* INSPIRE_GUIDELINE_2017 - 2.3 Identification info section / 2.3.3 Responsible organisation and point of contact for the described resource (p16)
* INSPIRE_GUIDELINE_2013 - 2.10 Responsible organisation (p55)
* CNIG_MD_DU - 9) Organisation responsable de l’établissement, de la gestion, 
* de la maintenance et de la diffusion des séries de données (p15)


## metadataContact (ResponsibleParty)

### Description

_Metadata point of contact_ represents ...


### XPath

```(xpath)
contact*/organisationName
contact/*/address/*/electronicMailAddress
contact/*/role
```

### References

* INSPIRE_GUIDELINE_2013 - 2.11 Metadata on metadata / 2.11.1 Metadata point of contact (p55)
* CNIG_MD_DU - 10) Métadonnées concernant les métadonnées / Point de contact pour la métadonnées (p15)


## metadataDate (Date)

### Description

_Metadata date_ represents ...

### XPath

```(xpath)
dateStamp
```

### References

* INSPIRE_GUIDELINE_2013 - 2.11 Metadata on metadata / 2.11.2 Metadata date (p60)
* CNIG_MD_DU - 10) Métadonnées concernant les métadonnées / Date des métadonnées (p15)


## metadataLanguage (LanguageCode)

### Description

_Metadata langage_ represents ...

### XPath

```(xpath)
language
```

### References

* INSPIRE_GUIDELINE_2013 - 2.11.3 Metadata language / 2.11.3 Metadata langage (p60)
* CNIG_MD_DU - 10) Métadonnées concernant les métadonnées / Langue des métadonnées (p15)
