This project provides a wrapper around org.apache.commons.httpclient.

It is independent of Winery itself as Winery uses JAX-RS.
The generated implementation artifacts, however, rely on this package.

## Build fat JARs
To build a fatJar of the HighLevelRestAPI which also contains all the dependencies use the maven profile fatJar (i.e., run `mvn clean package -P fatJar`).
Maven will create a JAR called `org.eclipse.winery.highlevelrestapi-*-jar-with-dependencies.jar` in `/target`.