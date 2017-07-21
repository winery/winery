# Eclipse Winery™

Winery is a Web-based environment to graphically model TOSCA topologies and plans managing these topologies.
It is an Eclipse project and thus support is available through its project page <https://eclipse.org/winery>.
Winery is also part of the OpenTOSCA ecosystem where more information is available at <http://www.opentosca.org>.

**The code and the linked libraries are NOT approved by Eclipse Legal.**

Both development and user documentation is rendered at <http://eclipse.github.io/winery/>.
The source for the documentation can be found at [docs/](docs).

## Next steps
Winery currently is far from being a production ready modeling tool.
The next steps are:

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
* **The XSD of OASIS TOSCA v1.0 has been modified** - see https://github.com/eclipse/winery/issues/71
** An Implementation Artifact may carry a `name` attribute
** The contents of properties of Boundary Definitions are processed in `lax` mode

#### Libraries

* Do NOT update to jQuery 2.1.0.
  When using with firefox, line 5571 in jquery.js fails: `divStyle is null`.
  That means `window.getComputedStyle( div, null );` returned `null`, too.
* Do NOT update to jsPlumb 1.5.5.
  The new connection type determination does not play well together with Winery's usage of jsPlumb. See [jsPlumb#165].

## Acknowledgements

The initial code contribution has been supported by the [Federal Ministry for Economic Affairs and Energy] as part of the [CloudCycle] project (01MD11023).
Current development is supported by the Federal Ministry for Economic Affairs and Energy as part of the projects [SmartOrchestra] (01MD16001F) and [SePiA.Pro] (01MD16013F).

## License

Copyright (c) 2012-2017 University of Stuttgart.

All rights reserved. This program and the accompanying materials
are made available under the terms of the [Eclipse Public License v1.0]
and the [Apache License v2.0] which both accompany this distribution,
and are available at http://www.eclipse.org/legal/epl-v10.html
and http://www.apache.org/licenses/LICENSE-2.0

Contributors:
* Oliver Kopp - initial API and implementation

  [Apache License v2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
  [bug421284]: https://bugs.eclipse.org/bugs/show_bug.cgi?id=421284
  [bower]: https://github.com/bower/bower
  [chocolatey]: http://chocolatey.org/
  [CloudCycle]: http://www.cloudcycle.org/en/
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
  [SePiA.Pro]: http://projekt-sepiapro.de/en/
  [TerrificJS]: http://terrifically.org/
