Trivial argument processing library for Java. Motivated in part by Apache
Commons' deprecation of their GNU-style argument parser.

To include in a Maven pom.xml:

    <project>
      ...
      <repositories>
        ...
        <repository>
	  <id>ivartj</id>
	  <url>http://maven.ivartj.org/</url>
	</repository>
      </repository>
      ...
      <dependencies>
        ...
        <dependency>
	  <groupId>org.ivartj.args</groupId>
	  <artifactId>args</artifactId>
	  <version>1.1</version>
	</dependency>
      </dependencies>
      ...
    </project>

Javadocs can be produced by running:

    mvn javadoc:javadoc

Which will place Javadoc documentation in:

  ./target/site/apidocs/

Example of intended usage can also be found in:

  ./src/test/java/org/ivartj/args/TestUsage.java
