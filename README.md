Esper Pad (Espad) is a harness to help build/test Esper EPL code in distrubuted environments.
An Espad setup consists of a host process and one or more event providers running in remote processes.

Host process can be either a standalone Java process running Esper engine 
or it can be an Esper EE server. In any case you have your EPL applications in a separate text file called espad.epl.

In Espad event providers are standalone Esper engines reading events from csv files and sending them to the host over HTTP using Esper HTTP adapters.
Hence Espad can be used to send events into Esper host over the Internet. 

PREREQUISITES
-------------

Espad requires perl for a number of scripts. Perl interpeter must be on your PATH.
For building and dependencies Espad uses maven.


INSTALL
--------

To start box or host CSV Input Adapter and HTTP Adapter along with Java NIO is required.
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

2.1. Build box
cd box
mvn clean package -Dmaven.test.skip=true

or
 
2.2. Build host
cd host
mvn clean package -Dmaven.test.skip=true

3. Deploy box, by copying created war into the Esper EE hotdeploy folder


ARCHITECTURE
------------

Espad consists of 4 components:
 * ei - Event Initializer. Keeps events definitions in a text file event.names

 * host - Standalone Esper server process. Depends on ei for event definitions. 
 * box - Hosts your EPL applications in an Esper EE environment. Depends on ei for event definitions. 

 * httpush - HTTP Event Pusher. Reads events from CSV file and sends them to host over HTTP. Depends on ei for event definitions. 

Espad can be used for accelerated EPL prototyping and testing. 
The host and box install EPL statements listener to log all statement updates prpending "ESPAD NE" for new events and "ESPAD OU" for old events.
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

Now execute script run.cmd passing the event data tag as a parameter. It connects to the host over HTTP (port 18079) and sends the events from the event data folder.    

LICENSE
-------
Copyright © 2013 Taras Shymbra.

Since Esper is GPLv2 licensed, Espad is GPLv2 licensed as well. Please read the [license](http://www.opensource.org/licenses/gpl-2.0.php).
