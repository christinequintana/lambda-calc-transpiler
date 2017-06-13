# Lambda Calculus to Java Transpiler

Converts labeled lambda abstractions into compilable Java methods.

### To Run

The transpiler is built with the [Apache Maven build tool](https://maven.apache.org/). Maven can be installed by following the installation directions on their website. Alternatively on Mac, Maven can be install via Homebrew:

``` 
$ brew install maven 
```

To build the transpiler and perform any lambda evaluations defined in `main.lc`, cd into the `lcjt` folder and run

```
$ make
```

This will build the `jar` file for the transpiler and save it in `target/javacode`, compile all of the files in that directory, and run `Main.java`.

The results of the lambda expression evaluations will be printed to the screen.

### Directory Structure

Not the complete structure. Only includes files relevant to maintaining the code base of the transpiler.

```
|-- README.md
|-- lcjt
    |-- Makefile
    |-- lc 			// Contains the lambda abstraction definitions
    	|-- bool.lc
    	|-- logic.lc
    	|-- pairs.lc
    	|-- vars.lc
    	|-- main.lc
    |-- target 	
    	|-- javacode		// Contains the converted Java code
    	    |-- Bool.java
            |-- Logic.java
            |-- Pairs.java
            |-- Vars.java
            |-- Lib.java
            |-- Main.java
            |-- *.class
    |-- src
        |-- main
            |-- java
                |-- com
                    |-- christinequintana
                        |-- hw
                            |-- App.java 	// Transpiler code
```