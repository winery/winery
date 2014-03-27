This project provides a Java client to a Winery repository.

## Build fat JARs
To build a fatJar of the repository client which also contains all the dependencies use the maven profile fatJar (i.e., run `mvn clean package -P fatJar`).
Maven will create a JAR called `org.eclipse.winery.repository.client-*-jar-with-dependencies.jar` in `/target`.