<!---~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ Copyright (c) 2020 Contributors to the Eclipse Foundation
  ~
  ~ See the NOTICE file(s) distributed with this work for additional
  ~ information regarding copyright ownership.
  ~
  ~ This program and the accompanying materials are made available under the
  ~ terms of the Eclipse Public License 2.0 which is available at
  ~ http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
  ~ which is available at https://www.apache.org/licenses/LICENSE-2.0.
  ~
  ~ SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->


# Developer Guide

This document provides an index to all development guidelines and background information of Eclipse Winery.

- [Recommended Reading](recommended-reading.md)
- [Modules](modules.md) - Winery's module structure
- [Branches](branches.md) - How to branch
- [Source Code Headers](source-code-headers.md) - Documentation about required source code headers
- [REST API](rest-api.md) - How Winery's REST API works
- [Encoding](double-encoding.md) - Information about how encoding is used in Winery
- [ID System](id-system.md) - Winery's ID System
- [Repository Layout](repository-layout.md) - Documents the layout of the repository (stored as plain text files)
- [Property Handling](property-handling.md)
- [Configuration and Features](config.md)
- [TOSCA 1.0 Notes](tosca.md)
- IDE Setup
  * IntelliJ IDEA (recommended): [config/IntelliJ IDEA](../config/IntelliJ%20IDEA/index.md)
  * Eclipse: [config/Eclipse](../config/Eclipse/index.md)
- [Winery GitHub Workflow](github-workflow.md)
- [Setup Winery Toolchain](toolchain.md)
- [Winery and Docker](docker.md)


## Getting Started

* Clone the repository: `git clone https://github.com/eclipse/winery && cd winery`.
* Build Eclipse Winery: `mvn clean install -DskipTests` (skipping the tests for a faster build).
* Setup your IDE:
  - IntelliJ IDEA (recommended): [config/IntelliJ IDEA](../config/IntelliJ%20IDEA/index.md)
  - Eclipse: [config/Eclipse](../config/Eclipse/index.md)
* Go to [Eclipse Winery Toolchain](toolchain.md) for further details
* Get familiar with [Winery's GitHub workflow](github-workflow.md)
