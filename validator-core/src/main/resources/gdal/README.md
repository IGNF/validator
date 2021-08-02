# GDAL config files

* [gmlasconf-original.xml](gmlasconf-original.xml) original GMLAS driver configure from https://raw.githubusercontent.com/OSGeo/gdal/master/gdal/data/gmlasconf.xml (02/08/2021)

* [gmlasconf-validator.xml](gmlasconf-validator.xml) : configuration adapted to validate PRCS dataset keeping CamelCase names (see [issue-241](https://github.com/IGNF/validator/issues/241))

```bash
diff gmlasconf-validator.xml gmlasconf-original.xml
37c37
<         <PostgreSQLIdentifierLaundering>false</PostgreSQLIdentifierLaundering>
---
>         <PostgreSQLIdentifierLaundering>true</PostgreSQLIdentifierLaundering>
40c40
<             <MaximumNumberOfFields>30</MaximumNumberOfFields>
---
>             <MaximumNumberOfFields>10</MaximumNumberOfFields>
```

