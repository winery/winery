# Eclipse Winery – OpenTOSCA fork

[![Build Status](https://travis-ci.org/OpenTOSCA/winery.svg?branch=master)](https://travis-ci.org/OpenTOSCA/winery)
[![License](https://img.shields.io/badge/License-EPL%202.0-blue.svg)](https://opensource.org/licenses/EPL-2.0)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

This is a fork of [Eclipse Winery](https://github.com/eclipse/winery) and might include current research results not yet approved by Eclipse legal.
Find out more about the OpenTOSCA eco system at [www.opentosca.org](http://www.opentosca.org/).

**There was no security of Eclipse Winey. There might be [remote code execution vulnerabilities](https://github.com/mbechler/marshalsec). Thus, when hosting Eclipse Winery, make it accessible to turstworthy parties only.**

Both development and user documentation is rendered at <https://eclipse.github.io/winery/>.
The source for the documentation can be found at [docs/](docs).

## Running via docker

1. `docker build -t winery .`.
   In case, there are issues, you can also try `docker build --no-cache -t winery Dockerfiles/full`
2. `docker run -p 9999:8080 winery` to run winery on http://localhost:9999/

## Running CLI via docker

1. `docker build -t winery-consistencycheck -f Dockerfile.consistencycheck .`
2. `docker run -v ${pwd}:/root/winery-repository -it winery-consistencycheck` to check `${pwd}` for consistency.

You can also use the pre-built image:

- Linux: `docker run -it -v ${pwd}:/root/winery-repository opentosca/winery:consistencycheck`
- Windows: `docker run -it -v C:/winery-repository:/root/winery-repository opentosca/winery:consistencycheck`

In case you want to have verbose information, you can execute following:

- Linux: `docker run -it -v ${pwd}:/root/winery-repository opentosca/winery:consistencycheck winery -v`
- Windows: `docker run -it -v C:/winery-repository:/root/winery-repository opentosca/winery:consistencycheck winery -v`

Currently supported CLI arguments:

```
usage: winery
 -h,--help         prints this help
 -p,--path <arg>   use given path as repository path
 -v,--verbose      be verbose: Output the checked elements
 ```

## Differences to Eclipse Winery

The branch `master` differs from eclipse/winery in the following files:

- [README.md](README.md) - This text file
- [.travis.settings.yml](.travis.settings.yml) - Different Travis settings
- [.travis.yml](.travis.yml) - Different AWS S3 upload directory
- [Dockerfile](Dockerfile) - Custom Docker build for the OpenTOSCA organization
- [Dockerfile.consistencycheck](Dockerfile.consistencycheck) - Custom Docker build for the OpenTOSCA organization
- [pom.xml](pom.xml) - Upload to opentosca/mvn-repo
- [winery](winery) - Executable required to launch Winery CLI

Further, following PRs have been merged into this repository:

- https://github.com/OpenTOSCA/winery/pull/44 - not startet a PR on eclipse/winery, because Eclipse Orion offers an own markdown component

One can find out the differences between the master branches of these two repositories by executing the following command:

    git fetch --all
    git difftool upstream/master origin/master

Precondition:

    git remote add upstream https://github.com/eclipse/winery.git

## Haftungsausschluss

Dies ist ein Forschungsprototyp.
Die Haftung für entgangenen Gewinn, Produktionsausfall, Betriebsunterbrechung, entgangene Nutzungen, Verlust von Daten und Informationen, Finanzierungsaufwendungen sowie sonstige Vermögens- und Folgeschäden ist, außer in Fällen von grober Fahrlässigkeit, Vorsatz und Personenschäden ausgeschlossen.

## Disclaimer of Warranty

Unless required by applicable law or agreed to in writing, Licensor provides the Work (and each Contributor provides its Contributions) on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied, including, without limitation, any warranties or conditions of TITLE, NON-INFRINGEMENT, MERCHANTABILITY, or FITNESS FOR A PARTICULAR PURPOSE.
You are solely responsible for determining the appropriateness of using or redistributing the Work and assume any risks associated with Your exercise of permissions under this License.

## License

Copyright (c) 2012-2017 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
