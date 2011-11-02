#!/bin/sh

if [ -z "${JAVA_HOME}" ]
then
  echo "JAVA_HOME not set"
  exit 0
fi

if [ ! -x "${JAVA_HOME}/bin/java" ]
then
  echo Cannot find java executable, check JAVA_HOME
  exit 0
fi

EE_HOME=..

CLASSPATH=.:$CLASSPATH
CLASSPATH=$CLASSPATH:target/classes

CLASSPATH=$CLASSPATH:target/lib/esper-4.2.0.jar
CLASSPATH=$CLASSPATH:target/lib/esperio-csv-4.2.0.jar
CLASSPATH=$CLASSPATH:target/lib/esperio-http-4.2.0.jar
CLASSPATH=$CLASSPATH:target/lib/httpcore-4.0.1.jar
CLASSPATH=$CLASSPATH:target/lib/httpclient-4.0.1.jar
CLASSPATH=$CLASSPATH:target/lib/httpcore-nio-4.0.1.jar
CLASSPATH=$CLASSPATH:target/lib/cglib-nodep-2.2.jar
CLASSPATH=$CLASSPATH:target/lib/commons-logging-1.1.1.jar
CLASSPATH=$CLASSPATH:target/lib/log4j-1.2.16.jar
CLASSPATH=$CLASSPATH:target/lib/antlr-runtime-3.2.jar
CLASSPATH=$CLASSPATH:target/lib/ei-1.0.0.jar

export CLASSPATH="$CLASSPATH"
