@echo off

REM Script to push events into espad over HTTP
REM Usage run.bat PATT e.g. run.bat one

call cleanp.cmd
call setenv.bat

xcopy /Y src\main\resources\eventdefs\tmst.pl target\classes\edefs_%1\
cd target/classes/edefs_%1/
perl tmst.pl edata_%1 comment_header_row using_utc_timemark
cd ../../../
 
set MEMORY_OPTIONS=-Xms256m -Xmx256m -XX:+UseParNewGC
"%JAVA_HOME%"\bin\java %MEMORY_OPTIONS% com.espad.httpush.Main edata_%1
