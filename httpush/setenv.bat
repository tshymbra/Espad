@echo off

if "%JAVA_HOME%" == "" (
  echo.
  echo JAVA_HOME not set
  goto EOF
)

if not exist "%JAVA_HOME%\bin\java.exe" (
  echo.
  echo Cannot find java executable, check JAVA_HOME
  goto EOF
)

set EE_HOME=..

if "%HTTPUSH_CLASSPATH_SET%" == "" (
  set HTTPUSH_CLASSPATH_SET=true
  goto setclasspath
) else (
  goto EOF
)

:setclasspath
set CLASSPATH=.;%CLASSPATH%
set CLASSPATH=%CLASSPATH%;target\classes
set CLASSPATH=%CLASSPATH%;target\lib\esper-4.6.0.jar
set CLASSPATH=%CLASSPATH%;target\lib\esperio-csv-4.6.0.jar
set CLASSPATH=%CLASSPATH%;target\lib\esperio-http-4.6.0.jar
set CLASSPATH=%CLASSPATH%;target\lib\httpcore-4.0.1.jar
set CLASSPATH=%CLASSPATH%;target\lib\httpclient-4.0.1.jar
set CLASSPATH=%CLASSPATH%;target\lib\httpcore-nio-4.0.1.jar
set CLASSPATH=%CLASSPATH%;target\lib\cglib-nodep-2.2.jar
set CLASSPATH=%CLASSPATH%;target\lib\commons-logging-1.1.1.jar
set CLASSPATH=%CLASSPATH%;target\lib\log4j-1.2.16.jar
set CLASSPATH=%CLASSPATH%;target\lib\antlr-runtime-3.2.jar
set CLASSPATH=%CLASSPATH%;target\lib\ei-1.0.0.jar

:EOF