# Taxonaut: a comparative viewer of multiple hierarchies

Taxonaut is a Java application software to compare multiple hierarchies retrieved from data sources
accessible by GBIF Species API.  Current version is [3.1.11](https://github.com/nomencurator/taxonaut/releases/tag/3.1.11).
[Executable jar file](https://github.com/nomencurator/taxonaut/releases/download/v3.1.11/Taxonaut-3.1.11-jar-with-dependencies.jar) is available to try on any Java 8 environment including Linux, Mac OS X, Solaris and Windows.
Download the jar file and double click it, or if it does not work, execute java from the command line by typing
```
java -jar Taxonaut-3.1.11-jar-with-dependencies.jar
```
at the directory where the jar file is.  Java Runtime Environment is available from [download site] (https://java.com/download/), if you do not have.  It is tested with openjdk 14-ea 2020-03-17.  Network connection to GBIF data server (api.gbif.org) is essential.   See [Wiki page](https://github.com/nomencurator/taxonaut/wiki) for more instructions.

##License

The software is provided under the Apache Licence.  Its copy is available from http://www.apache.org/licenses/LICENSE-2.0 .
Executable jar file contains following third party software libraries: GBIF API client Java code, Apache axis including jaxrpc, Jackson version 1, Google guava and Find Bugs.  These third party software libraries are copyrighted by the original authors. All but Find Bugs are licenced under the Apache Licence.  Find Bugs is licensed under  the Lesser GNU Public License, https://www.gnu.org/licenses/lgpl.html.

##How to cite

Please cite the software as follows with style-dependent modifications of course:

Ytow N. 2016.  Taxonaut: an application software for comparative display of multiple taxonomies with a use case of GBIF Species API.  [Biodiversity Data Journal 4: e9787](http://bdj.pensoft.net/articles.php?id=9787) doi: [10.3897/BDJ.4.e9787](http://dx.doi.org/10.3897/BDJ.4.e9787).
