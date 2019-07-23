#!/bin/bash

# https://github.com/travis-ci/travis-ci/issues/8681#issuecomment-340821970

source /opt/jdk_switcher/jdk_switcher.sh

# https://github.com/INRIA/spoon/blob/master/chore/travis/
jdk_switcher use openjdk11 && mvn -Djava.src.version=1.8 clean package

