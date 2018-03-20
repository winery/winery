# Eclipse Winery User Guide

<!-- toc -->

- [Overview](#overview)
- [Quickstart](#quickstart)
- [Features](#features)
- [Miscellaneous notes](#miscellaneous-notes)
- [Related work](#related-work)
- [License](#license)

<!-- tocstop -->

## Overview

Eclipse Winery is a Web-based environment to graphically model [OASIS TOSCA](../tosca/) topologies and plans managing these topologies.
It is an Eclipse project and thus support is available through its project page <https://projects.eclipse.org/projects/soa.winery>.
Winery is also part of the OpenTOSCA ecosystem where more information is available at <http://www.opentosca.org>.
For more information on TOSCA see [our TOSCA information page](../tosca/).

![Winery Components](graphics/WineryComponents.png)

Winery consists of four parts (1) the type and template management, (2) the topology modeler, (3) the BPMN4TOSCA plan modeler, and (4) the repository.

The type, template and artifact management enables managing all TOSCA types, templates and related artifacts.
This includes node types, relationship types, policy types, artifact types, artifact templates, and artifacts such as virtual machine images.

The topology modeler enables to create service templates.
Service templates consists of instances of node types (node templates) and instances of relationship types (relationship templates).
They can be annotated with requirements and capabilities, properties, and policies.

The BPMN4TOSCA plan modeler offers web based creation of BPMN models with the TOSCA extension BPMN4TOSCA.
That means the modeler supports the BPMN elements and structures required by TOSCA plans and not the full set of BPMN [KBBL12].
This part is currently in development and not part of the opensourced code of Winery.

The repository stores TOSCA models and allows managing their content. For instance, node types, policy types, and artifact templates are managed by the repository.
The repository is also responsible for importing and  exporting CSARs, the exchange format of TOSCA files and related artifacts.


## Quickstart

On Windows:

1. `mkdir c:\winery-repository`
2. `cd c:\winery-repository`
2. `git config --global core.longpaths true` to enable long paths. Works perfectly on Windows.
3. `git clone https://github.com/winery/test-repository.git .` to clone the [test repository](https://github.com/winery/test-repository).
4. `git remote add tosca-definitions https://github.com/OpenTOSCA/tosca-definitions/` to make the [tosca-definitions](https://github.com/OpenTOSCA/tosca-definitions/) known
5. `git fetch tosca-definitions` - to fetch the tosca-definitions repository
6. `git checkout black` - to switch to the main branch of the test repository

Now you are at the [test repository](https://github.com/winery/test-repository) containing testing types.
If you do `git checkout master`, you are seeing the [OpenTOSCA TOSCA Definitions repository](https://github.com/OpenTOSCA/tosca-definitions/).


## Features

- [Splitting](Splitting) - splitting functionality
- [TopologyCompletion](TopologyCompletion) - topology completion with a [Tutorial](TopologyCompletionTutorial)
- [XaaSPackager](XaaSPackager) - Enables reusing modeled topologies as templates for single applications

## Miscellaneous notes

Properties of a Template can be either full XML or key/value based.
If key/value based, a wrapper XML element is required.
Since QNames have to be unique, Winery proposes as namespace the namespace of the template appended by `propertiesdefinition/winery`.
The name of the wrapper element is `properties`.

<!--
Implementation hint: This is implemented in `PropertiesDefinitionComponent.onCustomKeyValuePairSelected` (TS) and `org.eclipse.winery.model.tosca.TEntityType.getWinerysPropertiesDefinition` (Java).
-->

## Related Work

- [tosca-parser by the OpenStack project](https://github.com/openstack/tosca-parser) - parses TOSCA YAML files using Pyhton
- [TOSCA .Net Analyzer](https://github.com/QualiSystems/Toscana) - .NET libary for working with TOSCA YAML files

## License

Copyright (c) 2017 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
