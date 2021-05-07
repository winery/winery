# Eclipse Winery – OpenTOSCA fork

[![License](https://img.shields.io/badge/License-EPL%202.0-blue.svg)](https://opensource.org/licenses/EPL-2.0)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

[![Winery CI](https://github.com/eclipse/winery/workflows/Winery%20CI/badge.svg)](https://github.com/eclipse/winery/actions?query=workflow%3A%22Winery+CI%22)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/4f342f5d0f534d84b40f2fe5143f412e)](https://www.codacy.com/gh/OpenTOSCA/winery/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=OpenTOSCA/winery&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://app.codacy.com/project/badge/Coverage/4f342f5d0f534d84b40f2fe5143f412e)](https://www.codacy.com/gh/OpenTOSCA/winery/dashboard?utm_source=github.com&utm_medium=referral&utm_content=OpenTOSCA/winery&utm_campaign=Badge_Coverage)

This is a fork of [Eclipse Winery](https://github.com/eclipse/winery) and might include current research results not yet approved by Eclipse legal.
Find out more about the OpenTOSCA eco system at [www.opentosca.org](http://www.opentosca.org/).

Winery is a web-based environment to graphically model TOSCA topologies and plans managing these topologies.
It is an Eclipse project and thus support is available through its project page <https://eclipse.org/winery>.
Winery is also part of the OpenTOSCA ecosystem where more information is available at <http://www.opentosca.org>.

**The code and the linked libraries are NOT approved by Eclipse Legal.**

There was no software security check of Eclipse Winery in the past.
There might be [remote code execution vulnerabilities](https://github.com/mbechler/marshalsec). 
Thus, when hosting Eclipse Winery, make it accessible to trustworthy parties only.

## Getting Started

Both development and user documentation is available at <https://eclipse.github.io/winery>.
The source for the documentation can be found in the [docs](docs) directory.

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

## Haftungsausschluss

Dies ist ein Forschungsprototyp.
Die Haftung für entgangenen Gewinn, Produktionsausfall, Betriebsunterbrechung, entgangene Nutzungen, Verlust von Daten und Informationen, Finanzierungsaufwendungen sowie sonstige Vermögens- und Folgeschäden ist, außer in Fällen von grober Fahrlässigkeit, Vorsatz und Personenschäden ausgeschlossen.

## Disclaimer of Warranty

Unless required by applicable law or agreed to in writing, Licensor provides the Work (and each Contributor provides its Contributions) on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied, including, without limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A PARTICULAR PURPOSE.
You are solely responsible for determining the appropriateness of using or redistributing the Work and assume any risks associated with Your exercise of permissions under this License.

## Acknowledgements

The initial code contribution has been supported by the [Federal Ministry for Economic Affairs and Energy] as part of the [CloudCycle] project (01MD11023).
Current development is supported by the Federal Ministry for Economic Affairs and Energy as part of the projects [SmartOrchestra] (01MD16001F) and [SePiA.Pro] (01MD16013F), as well as by the [DFG] (Deutsche Forschungsgemeinschaft) projects [SustainLife] (641730) and [ADDCompliance] (636503).
Further development is also funded by the European Union’s Horizon 2020 project [RADON].

## License

Copyright (c) 2012-2021 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0

  [CloudCycle]: http://www.cloudcycle.org/en
  [Federal Ministry for Economic Affairs and Energy]: http://www.bmwi.de/EN
  [Scalable JavaScript Application Architecture]: http://www.slideshare.net/nzakas/scalable-javascript-application-architecture-2012
  [SmartOrchestra]: http://smartorchestra.de/en
  [SePiA.Pro]: http://projekt-sepiapro.de/en
  [ADDCompliance]: http://addcompliance.cs.univie.ac.at
  [SustainLife]: http://www.iaas.uni-stuttgart.de/forschung/projects/SustainLife
  [RADON]: http://radon-h2020.eu
  [DFG]: http://www.dfg.de/en
