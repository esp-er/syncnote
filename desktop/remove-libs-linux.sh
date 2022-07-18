#!/bin/bash
shopt -s extglob 
mkdir .tmp
unzip -d .tmp/ build/compose/jars/OpenNotes-linux-x64-1.0.0.min.jar
rm -rf .tmp/org/sqlite/native/!(Linux)
rm -rf .tmp/org/sqlite/native/Linux/!(x86_64)
cd .tmp/
jar cmf META-INF/MANIFEST.MF OpenNotes-linux-x64-1.0.0.min.jar *
mv OpenNotes-linux-x64-1.0.0.min.jar ../
cd ..
rm -rf .tmp



