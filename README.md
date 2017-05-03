# CONDUCTOR TO PN #

This application converts a [Conductor](https://netflix.github.io/conductor/) workflow blueprint into a TB net xml file.

### How do I get set up? ###

To build the application run
```
#!shell
gradle build
```
This task generates a compressed executable jar file into the `build/distribution` directory.

To try out the application, unzip the executables and run
```
#!shell
cd build/distribution/conductor2pn
java -jar conductor2pn.jar \
  -w '../../resources/main/workers_mix.json' \
  -s '../../resources/main/workflow_wait.json' \
  -o 'wait_example.xml'
```

Usage instructions:
```
#!shell
usage: ConductorToPn [options]
 -h,--help                Print this message
 -o,--output <arg>        Output PNML file
 -s,--systemTasks <arg>   System tasks input file
 -w,--workers <arg>       Worker tasks input file
```

### Who do I talk to? ###

* [Matteo Camilli](http://camilli.di.unimi.it): <matteo.camilli@unimi.it>