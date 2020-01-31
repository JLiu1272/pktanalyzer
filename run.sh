#!/bin/sh
# This is a script for running the java program 
# and cleaning up any *.class files 

# Compile the main program 
javac pktanalyzer.java

echo pkt/new_$1_packet1.bin

# Run the program 
java pktanalyzer pkt/new_$1_packet1.bin 

# Clean the class files 
mv *.class classes
