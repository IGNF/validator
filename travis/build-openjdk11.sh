#!/bin/bash

# see https://github.com/INRIA/spoon/blob/master/chore/travis/

source /opt/jdk_switcher/jdk_switcher.sh
wget https://raw.githubusercontent.com/sormuras/bach/master/install-jdk.sh
chmod +x install-jdk.sh

source ./install-jdk.sh -f 11 -c
mvn clean package

