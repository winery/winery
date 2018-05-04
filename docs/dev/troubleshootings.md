# Trouble Shooting

## Debugging hints

### Debugging JavaScript code

#### Chrome

- Press <kbd>F12</kbd> to open the debug console
- Use the [Augury](https://chrome.google.com/webstore/detail/augury/elgalmkoelokbchhkhacckoklkejnhcd) Chrome extension

#### Firefox

- Press <kbd>F12</kbd> to open the debug console

### Faster Redeployment

It takes a few seconds until the whole application is redeployed.
You can use [JRebel](http://www.jrebel.com) for hot code replacement in the Tomcat in Eclipse and IntelliJ.

## Miscellaneous hints

### Generating the right output

* If necessary, set the content type of the JSP: `<%@page contentType="image/svg+xml; charset=utf-8" %>`
  * Otherwise, answer is plain text (and not XML)

* XML documents have to contain the header `<?xml version="1.0" encoding="UTF-8" standalone="yes"?>`
  * `standalone=yes` means that there is no external DTD
  * this eleminates parsing errors in firefox

### Trouble shooting IntelliJ

#### Strange errors
I get strange errors:
    `java.lang.NoClassDefFoundError: Lorg/slf4j/Logger`,
    `java.lang.ClassNotFoundException: com.sun.jersey.spi.container.servlet.ServletContainer`,
    `java.lang.IllegalStateException: Illegal access: this web application instance has been stopped already`,
    `java.lang.IllegalStateException: Illegal access: this web application instance has been stopped already. Could not load [org.apache.xerces.util.XMLGrammarPoolImpl].`, or
    `java.lang.IllegalStateException: Illegal access: this web application instance has been stopped already. Could not load [javax.xml.validation.Schema].`
    `java.lang.AbstractMethodError: javax.ws.rs.core.UriBuilder.uri(Ljava/lang/String;)Ljavax/ws/rs/core/UriBuilder;`

Solution 1:

1. Stop Tomcat
2. `mvn clean package` - either via command line or using the menu in IntelliJ
3. Build -> Build Artifacts... -> org.eclipse.winery.repository.war:exploded: Clean
4. Build -> Rebuild Project

Solution 2:

1. Stop Tomcat
2. Exit Eclipse
3. Ensure that everything is committed and pushed. Double check with `git gui`.
4. Reset everything in the repository as it was a clean checkout: `git clean -xdf`
5. Execute the basic setup steps again:
  - `mvn package install -DskipTests=true`
  - Open `pom.xml` in the root folder using IntelliJ and "Import as Project"
  - Setup Tomcat as usual

Solution 3:

Someone could have used an `ExecutorService` and not adhered the lifecycle.

#### Has issues with a new selfservice portal model

Use [everything](https://www.voidtools.com/) (`choco install everything`) to locate all "selfservice metadata jars" and delete them.
Otherwise, Winery does not compile.

### Other troubleshootings

When executing tests, winery logs its output in `winery-debug.log`, too.

Q: `Version.java` is missing, what can I do? <br/>
A: Execute `mvn resources:resources` in the project `org.eclipse.winery.repository.configuration`

In case some JavaScript libraries cannot be found by the browser, execute `bower prune`, `bower install`, `bower update` in both `org.eclipse.winery.repository` and `org.eclipse.winery.topologymodeler`.

If `mvn package` does not work in a sub project, please remember to use `-am` and execute it in the root folder. Example:

     mvn package -pl org.eclipse.winery.cli -am

When “The superclass "javax.servlet.jsp.tagext.SimpleTagSupport" was not found on the Java Build Path.”
appears, right click on the project then **Properties**, **Project Facets** and finally **Runtime**.
There, select the Apache Tomcat Runtime. Click "**Apply**", then "**OK**".

When running in the jetty 9.0.5, there is always the error message "Request Entity Too Large" when uploading a file.
There is the `maxFormContentSize` set in `jetty web.xml`, but it currently does not help to solve this issue.

If the Prefs class is not found, something is wrong with the libraries, tomcat config, eclipse environment (check
the build path!), etc.

The error message
`HTTP Status 500 - com.sun.jersey.api.container.ContainerException: org.apache.jasper.JasperException: The absolute uri: http://www.eclipse.org/winery/functions cannot be resolved in either web.xml or the jar files deployed with this application` indicates that `mvn generate-sources` was not run.

In case `javax.servlet.jsp.PageContext` cannot be found:
Project -> right click -> Properties -> Project Facets -> Dynamic Web Module -> "Runtimes" -> "New..."

When doing a copy-libs-to-tomcat hack, possibly "W3C_XML_SCHEMA_NS_URI cannot be resolved or is not a field" appears.
Remove `stax-api-1.0.1.jar` out of `tomcat8/lib`: Java's `rt.jar` should be used instead for `javax.xml.XMLConstants`.

Q: At `org.eclipse.winery.backend.ui`: Some strange errors<br/>
A: Execute in THAT directory
- `git clean -xdf` to remove all ignored and additional elements
- Rebuild whole Winery using `mvn package -DskipTests` in the root of the checkout

Q: I get `Cannot find module '../../repositoryUiDependencies/wineryModalModule/winery.modal.module'` in the topology modeler<br/>
A: Execute `mvn -am -pl org.eclipse.winery.topologymodeler.ui package`.
   This leads to a generation of the required source files in the topology modeler (by copying them from the repository.ui).


## License

Copyright (c) 2013-2018 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
