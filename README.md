# Eclipse Winery – OpenTOSCA fork

[![Build Status](https://travis-ci.org/OpenTOSCA/winery.svg?branch=ustutt)](https://travis-ci.org/OpenTOSCA/winery)
[![License](https://img.shields.io/badge/License-EPL%202.0-blue.svg)](https://opensource.org/licenses/EPL-2.0)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

This is a fork of [Eclipse Winery](https://github.com/eclipse/winery) and might include current research results not yet approved by Eclipse legal.
Find out more about the OpenTOSCA eco system at [www.opentosca.org](http://www.opentosca.org/).

**There was no security of Eclipse Winey. There might be [remote code execution vulnerabilities](https://github.com/mbechler/marshalsec). Thus, when hosting Eclipse Winery, make it accessible to turstworthy parties only.**

Both development and user documentation is rendered at <https://eclipse.github.io/winery/>.
The source for the documentation can be found at [docs/](docs).

## Differences to Eclipse Winery

The branch `ustutt` differs from eclipse/winery in the following files:

- [.github/PULL_REQUEST_TEMPLATE.md](.github/PULL_REQUEST_TEMPLATE.md) - extended for USTUTT students
- [README.md](README.md) - This text file + disclaimer
- [LICENSE.spdx](LICENSE.spdx) - Update OpenTOSCA specifics

One can find out the differences between the `ustutt` branch of OpenTOSCA/winery and the `master` branch of eclipse/winery by executing the following command:

    git fetch --all
    git difftool upstream/master origin/ustutt

Precondition:
   
    git remote add upstream https://github.com/eclipse/winery.git

## Running Winery

### Running via Docker

1. `docker build -t winery .`.
   In case, there are issues, you can also try `docker build --no-cache -t winery .`
2. `docker run -p 8080:8080 winery` to run Winery on <http://localhost:8080>

You can also use the pre-built image and bin it to a local repository:

    docker run -it -p 8080:8080 -v $(pwd):/var/opentosca/repository opentosca/winery

### Running Winery CLI via Docker

1. `docker build -t winery-cli -f Dockerfile.cli .`
2. `docker run -v $(pwd):/root/winery-repository -it winery-cli` to check `${pwd}` for consistency.

You can also use the pre-built image:

- Linux: `docker run -it -v $(pwd):/root/winery-repository opentosca/winery-cli`
- Windows: `docker run -it -v ${PWD}:/root/winery-repository opentosca/winery-cli`

In case you want to have verbose information, you can execute following:

- Linux: `docker run -it -v $(pwd):/root/winery-repository opentosca/winery-cli winery -v`
- Windows: `docker run -it -v ${PWD}:/root/winery-repository opentosca/winery-cli winery -v`

Currently supported CLI arguments:

```
usage: winery
 -h,--help         prints this help
 -p,--path <arg>   use given path as repository path
 -v,--verbose      be verbose: Output the checked elements
```

## Next steps

Winery currently is far from being a production ready modeling tool.
The next steps are:

* Add more usability features to the topology modeler
* Add support for multiple repositories
* Develop a plugin-system for user-defined editors. For instance, a constraint has a type. If a type is known to Winery, it can present a specific plugin to edit the type content instead of a generic XML editor.
* Add a real DAO layer to enable querying the available TOSCA contents using SQL or similar query language

## Known issues

* XSD Type declarations are not directly supported
** Declared types are converted to imports during a CSAR Import
** Editing of XSDs is not possible
* **The XSD of OASIS TOSCA v1.0 has been modified** - see https://github.com/eclipse/winery/issues/71
  * An Implementation Artifact may carry a `name` attribute
  * The contents of properties of Boundary Definitions are processed in `lax` mode
  * New elements have been added:
    * Pattern Refinement Models
    * Compliance Rules
* See https://github.com/eclipse/winery/issues

## Haftungsausschluss

Dies ist ein Forschungsprototyp.
Die Haftung für entgangenen Gewinn, Produktionsausfall, Betriebsunterbrechung, entgangene Nutzungen, Verlust von Daten und Informationen, Finanzierungsaufwendungen sowie sonstige Vermögens- und Folgeschäden ist, außer in Fällen von grober Fahrlässigkeit, Vorsatz und Personenschäden ausgeschlossen.

## Disclaimer of Warranty

Unless required by applicable law or agreed to in writing, Licensor provides the Work (and each Contributor provides its Contributions) on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied, including, without limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A PARTICULAR PURPOSE.
You are solely responsible for determining the appropriateness of using or redistributing the Work and assume any risks associated with Your exercise of permissions under this License.

## Acknowledgements

The initial code contribution has been supported by the [Federal Ministry for Economic Affairs and Energy] as part of the [CloudCycle] project (01MD11023).
Current development is supported by the Federal Ministry for Economic Affairs and Energy as part of the projects
[SmartOrchestra] (01MD16001F) and [SePiA.Pro] (01MD16013F), as well as by the [DFG] (Deutsche Forschungsgemeinschaft) projects [SustainLife] (641730) and [ADDCompliance] (636503).
Further development is also funded by the European Union’s Horizon 2020 project [RADON].

## License

Copyright (c) 2012-2020 Contributors to the Eclipse Foundation

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
  [ADDCompliance]: http://addcompliance.cs.univie.ac.at/
  [SustainLife]: http://www.iaas.uni-stuttgart.de/forschung/projects/SustainLife
  [RADON]: http://radon-h2020.eu/
  [DFG]: http://www.dfg.de/en/
