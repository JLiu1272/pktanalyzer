How to run this file 

The quickest way to run this program is to use the run.sh shell script written 
The run.sh requires one argument, it needs to know what packet you are analysing. The packets
are available in the pkt directory. And we are assuming that the packet always has the structure of
new_<protocol>_packet1.bin

Running Example 
./run.sh tcp 

----- Run it as a pure command --------

1) You need to first compile the program using javac command 
javac pktanalyzer.java

2) Run the program 
java pktanalyzer <Binary File Path> 