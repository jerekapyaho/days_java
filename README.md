# Days

Days utility in Java

See also the [C++ version](https://github.com/jerekapyaho/days_cpp).

## Using opencsv

The program uses the [opencsv](https://opencsv.sourceforge.net/) library to parse the CSV file. Since this project does not use 
Maven or Gradle for dependency management, you will need to download 
the JAR file from SourceForge and place it in the same directory as the Java source code files. You will also need the opencsv dependency on [Apache Commons Lang](https://commons.apache.org/proper/commons-lang/index.html). Then you need to include them both in your compile and run
commands.

The opencsv and Apache Commons Lang JAR files should not be committed to version control, so they are listed in the `.gitignore` file.

## Compile and run

To compile:

    javac *.java

To run (on Linux):

    java -classpath .:commons-lang3-3.12.0.jar:opencsv-5.7.1.jar Days
