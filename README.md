# Eclipse Winery
Winery is a Web-based environment to graphically model TOSCA topologies and plans managing these topologies.
It is an Eclipse project and thus support is available through its project page <https://projects.eclipse.org/projects/soa.winery>.
Winery is also part of the OpenTOSCA ecosystem where more information is available at <http://www.opentosca.org>.

**The code and the linked libraries are NOT approved by Eclipse Legal.**

## Runtime Requirements
* Java 8
* Servlet 3.0 capable container (e.g., Tomcat 7)

## Development Information
Winery uses maven and [bower] for fetching dependencies and building.
Bower is installed automatically using the [frontend-maven-plugin].
We recommend installing JDK8 by using `choco install jdk8` to be able to update it via `choco upgrade all`. See at the homepage of [chocolatey] for more information.
Please follow the the next step "Making the wars" before importing the project into an IDE.

### Making the wars
Run `mvn package`.
In case [bower] fails, try to investigate using `mvn package -X`.
You can start bower manually in `org.eclipse.winery.repository` and `org.eclipse.winery.topologymodeler` by issuing `bower install`.

There are two WARs generated:

* `org.eclipse.winery.repository/target/winery.war` and
* `org.eclipse.winery.topologymodeler/target/winery-topologymodeler.war`

They can be deployed on a Tomcat runtime environment.

## Branches
The `master` branch is always compiling and all tests should go through.
It contains the most recent improvements.
All other branches are real development branches and might event not compile.

There are no explicit branches for stable versions as winery is currently in development-only mode.

## Projects

### Model projects
Each of these projects are versioned separately.

* org.eclipse.winery.model.csar.toscametafile: model for TOSCA meta files contained in a CSAR
* org.eclipse.winery.model.selfservice: model for the self service portal
* org.eclipse.winery.model.tosca: model for TOCSA

### Support projects
* org.eclipse.winery.highlevelrestapi: support library to REST calls.

### Winery itself
Versioned together to ease development.

* org.eclipse.winery.common: Used in repository and topology modeler
* org.eclipse.winery.generators.ia: Implementation Artifact Generator, used as component in the repository
* org.eclipse.winery.repository: the repository including a JSP-based UI
* org.eclipse.winery.repository.client: Java-client for the repository
* org.eclipse.winery.topologymodeler: Graph-based modeler for topology templates

## Next steps
Winery currently is far from being a production ready modeling tool.
The next steps are:

* UI design improvements
  * Have Orion support `XML` as language. See also [Bug 421284][bug421284]
* Add more usability features to the topology modeler
* Remove non-required files from components/ directory to reduce the file size of the WAR file
  * This has to be done by submitting patches to `bower.json` of the upstream libraries
* Develop a plugin-system for user-defined editors. For instance, a constraint has a type. If a type is known to Winery, it can present a specific plugin to edit the type content instead of a generic XML editor.
* Rework file storage. Currently, files are stored along with their definitions. A new storage should store all files in one place and use an SHA1 id to uniquely identify the file. Then, it does not make any difference if storing a WAR, an XSD, or an WSDL.
* Add a real DAO layer to enable querying the available TOSCA contents using SQL or similar query language

Currently, `.jsp` files package HTML and JS.
We plan to use frameworks such as [TerrificJS] to provide a better modularity.
This follows Nicholas Zakas' "[Scalable JavaScript Application Architecture]".

## Known issues
* XSD Type declarations are not directly supported
** Declared types are converted to imports during a CSAR Import
** Editing of XSDs is not possible
* **The XSD of OASIS TOSCA v1.0 has been modified**
** An Implementation Artifact may carry a `name` attribute
** The contents of properties of Boundary Definitions are processed in `lax` mode

### Configure Winery (optional)
The repository location can be changed:
Copy `winery.properties` to `path-to-workspace\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\winery`.

### Trouble shooting
* IntelliJ
  * `java.lang.IllegalStateException: Illegal access: this web application instance has been stopped already` and `java.lang.IllegalStateException: Illegal access: this web application instance has been stopped already. Could not load [org.apache.xerces.util.XMLGrammarPoolImpl].`
    * `mvn clean package`
    * Build -> Build Artifacts... -> org.eclipse.winery.repository.war:exploded: Clean
    * Build -> Rebuild Project
  * Has issues with a new selfserivce portal model:  Use [everything](https://www.voidtools.com/) (`choco install everything`) to locate all "selfservice metadata jars" and delete them. Otherwise, Winery does not compile.
* In case some JavaScript libraries cannot be found by the browser, execute `bower prune`, `bower install`, `bower update` in both `org.eclipse.winery.repository` and `org.eclipse.winery.topologymodeler`.
* If `mvn package` does not work in a sub project, execute `mvn install` in the root. [Source](http://stackoverflow.com/q/29712865/873282)
* See [README.md of the repository](org.eclipse.winery.repository/README.md)
* See [README.md of the topology modeler](org.eclipse.winery.topologymodeler/README.md)

#### Libraries
* Do NOT update to jQuery 2.1.0.
  When using with firefox, line 5571 in jquery.js fails: `divStyle is null`.
  That means `window.getComputedStyle( div, null );` returned `null`, too.
* Do NOT update to jsPlumb 1.5.5.
  The new connection type determination does not play well together with Winery's usage of jsPlumb. See [jsPlumb#165].

## Acknowledgements
The initial code contribution has been supported by the [Federal Ministry for Economic Affairs and Energy] as part of the [CloudCycle] project (01MD11023).
Current development is supported by the Federal Ministry for Economic Affairs and Energy as part of the [SmartOrchestra] project (01MD16001F).

## License
Copyright (c) 2012-2016 University of Stuttgart.

All rights reserved. This program and the accompanying materials
are made available under the terms of the [Eclipse Public License v1.0]
and the [Apache License v2.0] which both accompany this distribution,
and are available at http://www.eclipse.org/legal/epl-v10.html
and http://www.apache.org/licenses/LICENSE-2.0

Contributors:
* Oliver Kopp - initial API and implementation

## Literature

### About TOSCA
* Binz, T., Breiter, G., Leymann, F., Spatzier, T.: Portable Cloud Services Using TOSCA. IEEE Internet Computing 16(03), 80--85 (May 2012). [DOI:10.1109/MIC.2012.43]
* Topology and Orchestration Specification for Cloud Applications Version 1.0. 25 November 2013. OASIS Standard. http://docs.oasis-open.org/tosca/TOSCA/v1.0/os/TOSCA-v1.0-os.html
* OASIS: Topology and Orchestration Specification for Cloud Applications (TOSCA) Primer Version 1.0 (2013)

See http://www.opentosca.org/#publications for a list of publications in the OpenTOSCA ecosystem.

### Programming
* Joshua Bloch. Effective Java, 2nd edition. Addison-Wesley

 [Apache License v2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
 [bug421284]: https://bugs.eclipse.org/bugs/show_bug.cgi?id=421284
 [bower]: https://github.com/bower/bower
 [chocolatey]: http://chocolatey.org/
 [CloudCycle]: http://www.cloudcycle.org/en/
 [DOI:10.1109/MIC.2012.43]: http://dx.doi.org/10.1109/MIC.2012.43
 [Eclipse Public License v1.0]: http://www.eclipse.org/legal/epl-v10.html
 [Eclipse IDE for Java EE Developers]: https://www.eclipse.org/downloads/packages/eclipse-ide-java-ee-developers/neon2
 [Federal Ministry for Economic Affairs and Energy]: http://www.bmwi.de/EN/
 [frontend-maven-plugin]: https://github.com/eirslett/frontend-maven-plugin
 [jsPlumb#165]: https://github.com/jsplumb/jsPlumb/issues/165
 [m2e-wtp]: http://eclipse.org/m2e-wtp/
 [m2eclipse]: http://eclipse.org/m2e/
 [nodejs]: http://nodejs.org/download/
 [Scalable JavaScript Application Architecture]: http://www.slideshare.net/nzakas/scalable-javascript-application-architecture-2012
 [SmartOrchestra]: http://smartorchestra.de/en/
 [TerrificJS]: http://terrifically.org/
