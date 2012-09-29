call cleanp.cmd
cd src/main/resources
mkdir edefs_%1
xcopy eventdefs\*.names edefs_%1
xcopy eventdefs\*.pl edefs_%1
xcopy eventdefs\*.cmd edefs_%1
cd edefs_%1
del *.csv
perl gen.pl
del *.names
del *.pl
cd ..
cd ../../../