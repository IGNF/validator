# Constraints

Implementation notes about ```gmd:resourceConstraints``` parsing.

## resourceConstraints

* ```identificationInfo[1]/*/resourceConstraints``` provides different categories of constraints applicable to the resource (or its metadata)
* Each ```resourceConstraints``` contains either a ```MD_Constraints```, a ```MD_LegalConstraints``` or a ```MD_SecurityConstraints```


### MD_Constraints

Restrictions on the access and use of a dataset or metadata

### MD_LegalConstraints

Subclass of MD_Constraints describing restrictions and legal prerequisites for accessing and using the dataset.

### MD_SecurityConstraints

Subclass of MD_Constraints handling restrictions imposed on the dataset because of national security, privacy, or other concerns

## Attributes type and multiplicity for each class

|                      | Type                  | MD_Constraints |  MD_LegalConstraints | MD_SecurityConstraints |
| -------------------- | --------------------- | -------------- | -------------------- | ---------------------- |
| useLimitation        | String                |   [0..*]       |         [0..*]       |        [0..*]          |
| accessConstraints    | MD_RestrictionCode    |      0         |         [0..*]       |         0              |
| useConstraints       | MD_RestrictionCode    |      0         |         [0..*]       |         0              |
| otherConstraints     | String                |      0         |         [0..*]       |         0              |
| classification       | MD_ClassificationCode |      0         |          0           |        [1..1]          |
| userNote             | String                |      0         |          0           |        [0..1]          |
| classificationSystem | String                |      0         |          0           |        [0..1]          |
| handlingDescription  | String                |      0         |          0           |        [0..1]          |

## MD_RestrictionCode

* copyright
* patent
* patentPending
* trademark
* license
* intellectualPropertyRights
* restricted
* otherRestrictions

Source : http://www.isotc211.org/2005/resources/Codelist/gmxCodelists.xml#MD_RestrictionCode

## MD_ClassificationCode

* unclassified
* restricted
* confidential
* secret
* topSecret

Source : http://www.isotc211.org/2005/resources/Codelist/gmxCodelists.xml#MD_ClassificationCode

## Limitations on public access

* identificationInfo[1]/*/resourceConstraints/*/accessConstraints : MD_RestrictionCode (ex : intellectualPropertyRights)
* identificationInfo[1]/*/resourceConstraints/*/otherConstraints : Free text
* identificationInfo[1]/*/resourceConstraints/*/classification  : MD_ClassificationCode (ex : restricted)

Quotes from TODO_REF :

* "The value of accessConstraints is otherRestrictions, if and only if they are instances of otherConstraints expressing limitations on public access"

## Condition applying to access and use

* identificationInfo[1]/*/resourceConstraints/*/useLimitation


## Resources

* [TODO_REF - 2.9 Constraints](http://inspire.ec.europa.eu/reports/ImplementingRules/metadata/MD_IR_and_ISO_20071210.pdf#page=25)

Introduction about constraints

* [](http://www.datypic.com/sc/niem20/e-gmd_resourceConstraints-1.html)

Schema explorer.
