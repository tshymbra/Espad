@echo on

REM Script to start standalone espad host
REM Usage start.bat

call cleanp.cmd
call setenv.bat

set MEMORY_OPTIONS=-Xms256m -Xmx256m -XX:+UseParNewGC 
"%JAVA_HOME%"\bin\java %MEMORY_OPTIONS% com.espad.host.HostMain