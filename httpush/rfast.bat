@echo on

REM Similar to run.bat pushes events into espad over HTTP skipping preparation steps.
REM For initial run on new data use run.bat
REM Usage rfast.bat PATT e.g. rfast.bat one

call setenv.bat

set MEMORY_OPTIONS=-Xms256m -Xmx256m -XX:+UseParNewGC
"%JAVA_HOME%"\bin\java %MEMORY_OPTIONS% com.espad.httpush.Main edata_%1
