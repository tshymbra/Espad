#!/bin/bash

. cleanp.sh
cd src/main/resources
edefsfolder=edefs_$1/

if [ -d "$edefsfolder" ]; then
    echo "$edefsfolder already exists, exiting"
    exit $?
fi

echo "creating $edefsfolder"
mkdir $edefsfolder
cp eventdefs/*.names $edefsfolder
cp eventdefs/*.pl $edefsfolder
cd $edefsfolder
rm -f *.csv
perl gen.pl
rm -f *.names
rm -f *.pl
cd ../
cd ../../../
