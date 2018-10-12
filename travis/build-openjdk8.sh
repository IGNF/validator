#!/bin/bash

# https://github.com/travis-ci/travis-ci/issues/8681#issuecomment-340821970

source /opt/jdk_switcher/jdk_switcher.sh

# https://github.com/INRIA/spoon/blob/master/chore/travis/travis-jdk8.sh
jdk_switcher use openjdk8 && mvn -Djava.src.version=1.8 clean package

