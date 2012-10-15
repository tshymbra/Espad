
Esper Pad (Espad) is a harness to help build/test Esper EPL code in a distrubuted environments.
It relies on Esper EE. An Espad setup consists of an Esper EE server (host) running EPL applications 
and one or more event providers running in remote processes.

In Espad event providers are standalone Esper engines reading events from csv files and sending them to the host over HTTP. 
Providers send events over http using Esper HTTP adapters.
Hence Espad can be used to send events into Esper server over the Internet. 


PREREQUISITES
-------------

Espad requires perl for a number of scripts. Perl interpeter must be on your PATH.
For building and dependencies Espad uses maven.


INSTALL
--------

To start box host needs CSV Input Adapter and HTTP Adapter along with Java NIO.
You have to add required jars to your classpath, please refer to the EsperIO Reference Documentation.
For example, required Esper IO jars can be copied into lib folder and a following section can be added to setclasspath.bat

rem Espad dependencies
set CLASSPATH=%CLASSPATH%;%LIB%\esperio-csv-4.x.0.jar
set CLASSPATH=%CLASSPATH%;%LIB%\esperio-http-4.x.0.jar
set CLASSPATH=%CLASSPATH%;%LIB%\httpcore-nio-4.0.1.jar
set CLASSPATH=%CLASSPATH%;%LIB%\httpcore-4.0.1.jar

(note: replace x with actual Esper minor version)

1. Build ei: 
cd ei
mvn clean install -Dmaven.test.skip=true

2. Build box
cd box
mvn clean package -Dmaven.test.skip=true

3. Deploy box, by copying created war into the Esper EE hotdeploy folder
 

ARCHITECTURE
------------

Espad consists of 3 components:
 * ei - Event Initializer. Keeps events definitions in a text file event.names
 * box - Holds EPL applications. Depends on ei for event definitions. 
 * httpush - HTTP Event Pusher. Reads events from CSV file and sends them to host over HTTP. Depends on ei for event definitions. 

Espad can be used for accelerated EPL prototyping and testing. 
The box component installs EPL statements listener to log all statement updates prpending "ESPAD NE" for new events and "ESPAD OU" for old events.
This tags can be ussed for grepping to analyse you EPL mathching results.


EVENT DEFINITIONS
-----------------
The file event.names uses simple CSV format where first value is event name. It is followed by event attributes.
The first attribute is always timemark field. You must provide it.
The purpose of this field is to provide easy readable time reference that can be used to generate UTC timestamp. 

Espad uses following datetime format for the timemark field:
HH::MM DD.MM.YYYY
12:09 29.09.2011

The last attribute in an event definition is always timestamp. It is not required to provide this attribute - it will be automatically appended by Espad scripts if absent.
Event attributes can have types e.g. int id, float percent. Default attribute type is string.
 

USING HTTPUSH
--------------

Before using httpush run prepare.cmd with a single argument representing an event data tag. The tag is used as suffix of a event data folder.
The script first builds httpush. During this step events definitions from ei will be automatically expanded into src/main/resources which means each event gets an *.csv event data file. 
This is because httpush has a dependency on ei in the pom.xml - the execution "unpack_ei" does the job.

Secondly the script creates tagged event data folder and copies extracted event definitions into it. 
You can now edit event data files in this folder to add events into it.

Now execute script run.cmd passing the event data tag as a parameter. It connects the host over HTTP (potr 18079) and sends the events from the event data folder.    
