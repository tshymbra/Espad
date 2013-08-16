#!/bin/bash

. cleanp.sh
. setenv.sh

MEMORY_OPTIONS="-Xms256m -Xmx256m -XX:+UseParNewGC"
$JAVA_HOME/bin/java $MEMORY_OPTIONS -Dlog4j.configuration=log4j.xml -cp ${CLASSPATH} com.espad.host.Main -f=espad.epl