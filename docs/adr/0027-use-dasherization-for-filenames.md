# Use dasherization for filenames

## Context and Problem Statement

Graphics files have to take a consistent file name

## Decision Drivers <!-- optional -->

* Easy to process by Jekyll
* No WTFs at the creators

## Considered Options

* [Dasherization](https://softwareengineering.stackexchange.com/a/104476/52607) (e.g., `architecture-bpmn4tosca.png`)
* Camel Case (e.g., `ArchitectureBPMN4TOSCA.png`)

## Decision Outcome

Chosen option: "Dasherization", because

* clear separation of parts of the name
* consistent to other URLs (which are typically lowercase)

## Links

* GADR: https://github.com/adr/gadr-misc/blob/master/gadr--filename-convention.md

## License

Copyright (c) 2018 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
