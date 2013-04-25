#!/bin/bash

# -----------------------------------------------------------------------------
# Script to start standalone espad host
# Usage start.sh
# -----------------------------------------------------------------------------

. cleanp.sh
. setenv.sh

MEMORY_OPTIONS="-Xms256m -Xmx256m -XX:+UseParNewGC"
$JAVA_HOME/bin/java $MEMORY_OPTIONS -Dlog4j.configuration=log4j.xml -cp ${CLASSPATH} com.espad.host.HostMain