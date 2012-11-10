#!/bin/bash

# -----------------------------------------------------------------------------
# Script to push events into espad over HTTP
# Usage run.sh PATT e.g. run.sh one
# -----------------------------------------------------------------------------

. cleanp.sh
. setenv.sh

target_data=edata_$1
target_defs=edefs_$1

cp src/main/resources/eventdefs/tmst.pl target/classes/$target_defs/
cd target/classes/$target_defs/
perl tmst.pl $target_data comment_header_row using_utc_timemark
cd ../../../

MEMORY_OPTIONS="-Xms256m -Xmx256m -XX:+UseParNewGC"
$JAVA_HOME/bin/java $MEMORY_OPTIONS -Dlog4j.configuration=log4j.xml -cp ${CLASSPATH} com.espad.httpush.Main $target_data
