# Eclipse Winery User Guide

Eclipse Winery is a Web-based environment to graphically model [OASIS TOSCA](../tosca/) topologies and plans managing these topologies.
It is an Eclipse project and thus support is available through its project page <https://projects.eclipse.org/projects/soa.winery>.
Winery is also part of the OpenTOSCA ecosystem where more information is available at <http://www.opentosca.org>.
For more information on TOSCA see [our TOSCA information page](../tosca/).

- [overview](overview.md) - overview on Winery
- [FAQ](FAQ.md) - frequently asked questions
- [Quick Setup](quicksetup.md) - shows how to setup the winery repository for TOSCA definitions
- [Quickstart](quickstart.md) - shows how to use Winery to model a node type and a topology

## Features

- [Splitting](Splitting) - splitting functionality
- [Target Allocation](TargetAllocation) - select best suited Cloud Provider for topologies
- [TopologyCompletion](TopologyCompletion) - topology completion with a [Tutorial](TopologyCompletionTutorial)
- [XaaSPackager](XaaSPackager) - Enables reusing modeled topologies as templates for single applications
- [Compliance Checking](ComplianceChecking.md) - Enables compliance checking of Topology Templates based on reusable Compliance Rules  
- [Implementation Artifact Generation](generateIA.md) - Shows how to generate and update an implementation artifact of type .war
- [Securing](Securing.md) - Secures insecure communication between two nodes

## Background Literature

[BBKL14] Breitenbücher, Uwe; Binz, Tobias; Kopp, Oliver; Leymann, Frank: Vinothek - A Self-Service Portal for TOSCA. In: Herzberg, Nico (Hrsg); Kunze, Matthias (Hrsg): Proceedings of the 6th Central-European Workshop on Services and their Composition (ZEUS 2014).

[KBBL12] Kopp, Oliver; Binz, Tobias; Breitenbücher, Uwe; Leymann, Frank: BPMN4TOSCA: A Domain-Specific Language to Model Management Plans for Composite Applications. In: Mendling, Jan (Hrsg); Weidlich, Matthias (Hrsg): 4th International Workshop on the Business Process Model and Notation, 2012

More readings at <https://www.opentosca.org>.

## Related Work

- [tosca-parser by the OpenStack project](https://github.com/openstack/tosca-parser) - parses TOSCA YAML files using Pyhton
- [TOSCA .Net Analyzer](https://github.com/QualiSystems/Toscana) - .NET libary for working with TOSCA YAML files

## License

Copyright (c) 2017-2018 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
