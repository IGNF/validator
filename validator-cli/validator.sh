#!/bin/sh
# Shell script : validator.sh
# Author : CBouche
# Date : December 2013

if [ $# = 0 ]; then
	#statements
	echo "[HELP] ---------------------------------------------------------------"
	echo "[HELP] VALIDATOR : Programme de validation des Documents d'Urbanismes"
	echo "[HELP] et des Servitudes d'Utilite Publique"
	echo "[HELP] ---------------------------------------------------------------"
	echo "[HELP] Usage : "
	echo "[HELP] validator <dir> <configDir> [-v <version>]"
	echo "[HELP] "
	echo "[HELP] Detail :"
	echo "[HELP]        <dir>         Repertoire contenant les documents"
	echo "[HELP]                      a valider"
	echo "[HELP]        <configDir>   Repertoire contenant les fichiers"
	echo "[HELP]                      decrivant les normes"
	echo "[HELP]        <version>     Version courante de la norme utilisee"
	echo "[HELP] ---------------------------------------------------------------"
	exit 1
fi

# echo java -jar -Dlog4j.configurationFile=$PWD/log4j2.xml $PWD/validator-1.0-SNAPSHOT-jar-with-dependencies.jar $*

java -jar -Dlog4j.configurationFile=$PWD/log4j2.xml $PWD/target/validator-cli-1.0-SNAPSHOT.jar -c $PWD/config $*

