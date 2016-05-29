#!/bin/bash
NAMESPACE_JAVA="cz.sodae.hardware.nfc"
NAMESPACE_DIR=$(echo $NAMESPACE_JAVA | tr \. \/)

if [ "$NAMESPACE_DIR" = "" ] || [ "$NAMESPACE_DIR" = "/" ]; then echo "empty NAMESPACE_DIR!"; exit; fi;

echo "swig..."
swig -c++ -java -package $NAMESPACE_JAVA hardware_nfc.i
echo "g++..."
g++ -c hardware_nfc.cpp hardware_nfc.h hardware_nfc_wrap.cxx -I /usr/lib/jvm/java-7-openjdk-armhf/include -lnfc
echo "gcc..."
gcc -shared hardware_nfc.o hardware_nfc_wrap.o -lnfc -o libhardware_nfc.so
echo "javac..."
javac main.java NFCISO14443ATarget.java NFCController.java NFCException.java hardware_nfcJNI.java
echo "remove all in $NAMESPACE_DIR..."
rm $NAMESPACE_DIR/*
echo "create dir $NAMESPACE_DIR..."
mkdir -p $NAMESPACE_DIR
echo "copy files to $NAMESPACE_DIR..."
cp -f NFCISO14443ATarget.class NFCException.class NFCController.class hardware_nfcJNI.class $NAMESPACE_DIR
echo "create jar"
jar -cf $NAMESPACE_JAVA.jar cz
#echo "run"
#java -Djava.library.path=${PWD} main
