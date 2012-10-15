#!/bin/bash

# -----------------------------------------------------------------------------
# Similar to run.bat pushes events into espad over HTTP skipping preparation steps.
# For initial run on new data use run.bat
# Usage rfast.sh PATT e.g. rfast.sh one
# -----------------------------------------------------------------------------

. setenv.sh

target_data=edata_$1

MEMORY_OPTIONS="-Xms256m -Xmx256m -XX:+UseParNewGC"
$JAVA_HOME/bin/java $MEMORY_OPTIONS -Dlog4j.configuration=log4j.xml -cp ${CLASSPATH} com.espad.httpush.Main $target_data
