# Eclipse Winery Developer Guide

This document provides an index to all development guidelines and background information of winery.

- [ADR](../adr) - list of [architectural decision records](https://adr.github.io) showing which design decisions were taken during development of Winery
- [Branches](branches.md) - information on branches
- [CodeHeaders](CodeHeaders.md) - documentation about required code headers
- [UI Configuration](Configurationmanagment.md) - documentation how to configure the frontends 
- IDE Setup
    - IntelliJ Ultimate setup: [config/IntelliJ IDEA/README.md](config/IntelliJ%20IDEA/)
    - Eclipse setup: [config/Eclipse/README.md](config/Eclipse/)
- [Encoding](Encoding.md) - information about how percent-encoding is used at Winery
- [Handling of Properties](property-handling.md)
- [How tos](howtos) - multiple howtos
- [ID system](id-system.md)
- [Projects](projects.md) - information on the projects
- [Property Handling](property-handling.md)
- [Recommended Reading](recommended-reading.md)
- [RepositoryLayout](RepositoryLayout.md) - documents the layout of the repository (stored as plain text files)
- [TOSCA-Mangement UI](TOSCA-Management.md)
- [REST](REST.md) - how Winery's REST API works
- [ToolChain](ToolChain) - GitHub workflow
- [Trouble Shooting](troubleshootings.md)
- [TOSCA](../tosca/) - notes on OASIS TOSCA
- [Extract Deployable Components from Chef Cookbooks](../user/CrawlerAndComponentExtractionForChefCookbooks.md) - Notes on crawler and extraction of components from chef cookbooks
- [DeployableComponents](../user/DeployableComponentsUsage.md) - Explanations on dockerfile component extraction tool

To get started, go on at [ToolChain](ToolChain.md).

As a user, continue at our [quick Start](../user/quickstart.md), which shows how to start how to work with Winery.

## Quick Develop

1. Clone the repository `git clone https://github.com/eclipse/winery && cd winery`.
2. If you are a Mac user, [install solidity natively](https://solidity.readthedocs.io/en/latest/installing-solidity.html#binary-packages).
3. Build the repository `mvn package -DskipTests` (skiping the tests for a faster build).
4. Continue your IDE setup:
    - [IntelliJ Ultimate](config/IntelliJ%20IDEA/)
    - [Eclipse](config/Eclipse/)

## License

Copyright (c) 2013-2018 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
