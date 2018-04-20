#!/bin/bash
RUN_SCRIPT_DIR=`pwd`
if [ -f "$RUN_SCRIPT_DIR/pom.xml" ]; then
    TARGET="$RUN_SCRIPT_DIR/target"
    DIST="$RUN_SCRIPT_DIR/target/rya-sparqlendpoint-debug-*-distribution.zip"
    if [ -d $TARGET ] && [ -f $DIST ]; then
        echo "Running packaged tomcat in target"
        RUN_ROOT="$RUN_SCRIPT_DIR/target/running"
        mkdir $RUN_ROOT
        cd $RUN_ROOT
        cp $RUN_SCRIPT_DIR/target/rya-sparqlendpoint-debug-*-distribution.zip .
        unzip $RUN_ROOT/rya-sparqlendpoint-debug-*-distribution.zip
        chmod +x bin/*.sh
        rm bin/*.bat
        ./bin/startup.sh
    else
        echo "Project needs to be packaged first (run: mvn clean package -Pdistribution)"
    fi
else
    echo "Script needs to be run from clone root";
fi
