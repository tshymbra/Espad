call cleanp.cmd
cd src/main/resources
mkdir edefs_%1
xcopy eventdefs\*.names edefs_%1
xcopy eventdefs\*.pl edefs_%1
xcopy eventdefs\*.cmd edefs_%1
cd edefs_%1
call genenames.cmd
del *.names
del *.pl
del *.cmd
cd ..
cd ../../../