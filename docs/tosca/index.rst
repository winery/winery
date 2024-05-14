.. Copyright (c) 2020-2022 Contributors to the Eclipse Foundation

.. See the NOTICE file(s) distributed with this work for additional
.. information regarding copyright ownership.

.. This program and the accompanying materials are made available under the
.. terms of the Eclipse Public License 2.0 which is available at
.. http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
.. which is available at https://www.apache.org/licenses/LICENSE-2.0.

.. SPDX-License-Identifier: EPL-2.0 OR Apache-2.0

.. _tosca:


Notes on TOSCA
**************

The *Topology and Orchestration Specification for Cloud Applications (TOSCA)* is a standard defined by the OASIS organization.
It defines a language to model (cloud) applications to automate their provisioning and management.
Thereby, TOSCA is vendor and technology independent and aims at defining applications in a portable and interoperable manner.

In general, there are two different flavours built in to TOSCA:
(i) declarative modeling and
(ii) imperative modeling.
While the traditional, declarative way to model an application is in the form of a *Topology Template*, i.e., a graph that describes the application's components and their relations,
it also supports imperative workflows that exactly state the tasks and their order in which they have to be processed.
However, since we can automatically generate the imperative workflows based on the declarative model, Winery focuses mainly on the creation of the component's types,
i.e., *Node Types*, and whole applications, i.e., *Service Templates* that add additional meta-information and wrap a *Topology Template*.

For more details about the standard, go to the specifications as linked below.
For more documentation about how to model an application using Winery and the OpenTOSCA ecosystem, see <../user/xml/index.rst>. 

Recommended Readings
====================

#. `Portable Cloud Services Using TOSCA. In: IEEE Internet Computing (2012) <http://doi.org/10.1109/MIC.2012.43>`_ - Short overview.
#. `TOSCA: Portable Automated Deployment and Management of Cloud Applications. In: Advanced Web Services (2014) <http://doi.org/10.1007/978-1-4614-7535-4_22>`_ - Longer overview.
#. `TOSCA Simple Profile in YAML Version 1.3 <http://docs.oasis-open.org/tosca/TOSCA-Simple-Profile-YAML/v1.3/TOSCA-Simple-Profile-YAML-v1.3.html>`_ - The simple profile in YAML.

See `<http://www.opentosca.org/sites/publications.html>`_ for a list of publications in the OpenTOSCA ecosystem.

TOSCA 1.3 YAML
==============

* `Official Specification <http://docs.oasis-open.org/tosca/TOSCA-Simple-Profile-YAML/v1.3/TOSCA-Simple-Profile-YAML-v1.3.html>`_
* :download:`Class Diagram <TOSCA-Simple-Profile-in-YAML-v1.0-os-class-diagram.pdf>`
* :download:`PlantUML <TOSCA-Simple-Profile-in-YAML-v1.0-os-class-diagram.plantuml>`

TOSCA 1.0 XML (Deprecated)
==========================

* `Official Specification <http://docs.oasis-open.org/tosca/TOSCA/v1.0/TOSCA-v1.0.html>`_
* :download:`Class Diagram <TOSCA-v1.0-os-class-diagram.pdf>`
* :download:`PlantUML <TOSCA-v1.0-os-class-diagram.plantuml>`

Example TOSCA YAML Files
========================

* `Project RADON <https://github.com/radon-h2020/radon-particles>`_

Available TOSCA Implementations
===============================

* `<https://wiki.oasis-open.org/tosca/TOSCA-implementations>`_
