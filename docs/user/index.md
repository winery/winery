# Eclipse Winery™ User Guide

<!-- toc -->

- [Overview](#overview)
- [Quickstart](#quickstart)
- [Features](#features)
- [License](#license)

<!-- tocstop -->

## Overview

Eclipse Winery™ is a Web-based environment to graphically model [OASIS TOSCA](../tosca/) topologies and plans managing these topologies.
It is an Eclipse project and thus support is available through its project page <https://projects.eclipse.org/projects/soa.winery>.
Winery is also part of the OpenTOSCA ecosystem where more information is available at <http://www.opentosca.org>.

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
3. `git clone https://github.com/OpenTOSCA/tosca-definitions/ .`
4. `git remote add test-repository https://github.com/winery/test-repository.git`
5. `git fetch test-repository`
6. `git checkout black`

Now you are at the [test repository](https://github.com/winery/test-repository) containing testing types.
If you do `git checkout master`, you are seeing the [OpenTOSCA TOSCA Definitions repository](https://github.com/OpenTOSCA/tosca-definitions/).

In case you start from a fresh repository, please ensure that the Artifact Type `WAR` in the namespace `http://opentosca.org/artifacttypes` exists.

## Features

- [Splitting](Splitting) - splitting functionality
- [TopologyCompletion](TopologyCompletion) - topology completion with a [Tutorial](TopologyCompletionTutorial)
- [XaaSPackager](XaaSPackager) - Enables reusing modeled topologies as templates for single applications

## License

Copyright (c) 2017 University of Stuttgart.

All rights reserved. This program and the accompanying materials
are made available under the terms of the [Eclipse Public License v1.0]
and the [Apache License v2.0] which both accompany this distribution.

  [Apache License v2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
  [Eclipse Public License v1.0]: http://www.eclipse.org/legal/epl-v10.html
