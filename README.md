# CONDUCTOR TO PN #

This application converts a [Conductor](https://netflix.github.io/conductor/) workflow blueprint into a TB net xml file.

### How do I get set up? ###

To build the application run:
```
#!shell
gradle build
```
This task generates compressed executable binaries into the `build/distribution` directory.

To try out the application, unpack the executables and run from the project directory:
```
#!shell
java -jar build/distribution/conductor2pn/conductor2pn.jar \
  -w 'build/resources/main/workers_mix.json' \
  -s 'build/resources/main/workflow_wait.json' \
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