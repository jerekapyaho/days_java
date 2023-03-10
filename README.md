# Days

Days utility in Java

See also the [C++ version](https://github.com/jerekapyaho/days_cpp).

## Using opencsv

The program uses the [opencsv](https://opencsv.sourceforge.net/) library to parse the CSV file. Since this project does not use 
Maven or Gradle for dependency management, you will need to download 
the required JAR file from SourceForge and place it in the `libs` subdirectory. You will also need the opencsv dependency on [Apache Commons Lang](https://commons.apache.org/proper/commons-lang/index.html). Then you need to include them both in your compile and run commands.

The opencsv and Apache Commons Lang JAR files should not be committed to version control, so they are listed in the `.gitignore` file.

## Compile and run

To compile (on Linux):

    javac -classpath .:libs/commons-lang3-3.12.0.jar:libs/opencsv-5.7.1.jar *.java

To run (on Linux):

    java -classpath .:libs/commons-lang3-3.12.0.jar:libs/opencsv-5.7.1.jar Days

## Generated documentation

You can use the `javadoc` tool to generate documentation for the app:

    javadoc -d docs -classpath .:libs/opencsv-5.7.1.jar *.java

The documentation is generated in the `docs` subdirectory. 
