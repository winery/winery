# Eclipse Winery

[![License](https://img.shields.io/badge/License-EPL%202.0-blue.svg)](https://opensource.org/licenses/EPL-2.0)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Feclipse%2Fwinery.svg?type=shield)](https://app.fossa.io/projects/git%2Bgithub.com%2Feclipse%2Fwinery?ref=badge_shield)

Winery is a Web-based environment to graphically model TOSCA topologies and plans managing these topologies.
It is an Eclipse project and thus support is available through its project page <https://eclipse.org/winery>.
Winery is also part of the OpenTOSCA ecosystem where more information is available at <http://www.opentosca.org>.

**The code and the linked libraries are NOT approved by Eclipse Legal.**

**There was no security of Eclipse Winey. There might be [remote code execution vulnerabilities](https://github.com/mbechler/marshalsec). Thus, when hosting Eclipse Winery, make it accessible to turstworthy parties only.**

Both development and user documentation is rendered at <https://eclipse.github.io/winery/>.
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
* See https://github.com/eclipse/winery/issues


## Acknowledgements

The initial code contribution has been supported by the [Federal Ministry for Economic Affairs and Energy] as part of the [CloudCycle] project (01MD11023).
Current development is supported by the Federal Ministry for Economic Affairs and Energy as part of the projects [SmartOrchestra] (01MD16001F) and [SePiA.Pro] (01MD16013F).

## License

Copyright (c) 2012-2017 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0

  [CloudCycle]: http://www.cloudcycle.org/en/
  [Federal Ministry for Economic Affairs and Energy]: http://www.bmwi.de/EN/
  [Scalable JavaScript Application Architecture]: http://www.slideshare.net/nzakas/scalable-javascript-application-architecture-2012
  [SmartOrchestra]: http://smartorchestra.de/en/
  [SePiA.Pro]: http://projekt-sepiapro.de/en/
  [TerrificJS]: http://terrifically.org/


[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2Feclipse%2Fwinery.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2Feclipse%2Fwinery?ref=badge_large)