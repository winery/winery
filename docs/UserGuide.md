# Winery

<!-- toc -->

- [Overview](#overview)
- [Splitting Functionality](#splitting-functionality)

<!-- tocstop -->

## Overview

Winery is a Web based environment to graphically model TOSCA topologies and plans managing these topologies.
It is an Eclipse project and thus support is available through its project page <https://projects.eclipse.org/projects/soa.winery>. Winery is also part of the OpenTOSCA ecosystem where more information is available at <http://www.opentosca.org>.

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


## Splitting Functionality

In the topologymodeler target labels can be shown/hidden by "Target Locations" - The Target Location assigned
 to a Node Template determines the service templates serving as cloud provider repositories, which should be searched
 for a suitable host. The namespace of all cloud provider repositories must start with "http://www.opentosca.org/providers/".
 To distinguish between different repositories the namespace must end with the target label, e.g.
  "../IAAS" or "../Amazon/PaaS". The target labels are not case sensitive.

A prerequisite for a splitting is the assignment of requirements and capabilities to all Node Templates in the
Topology Template, which should be split, and the Node Templates in the repositories.
The latter just have requirements assigned because they form the lowest level of the topology.
The naming convention for the mapping of Reqs and Caps are:

	- Requirement: ReqCanHostxyz
	- Capability:  CapCanHostxyz

By clicking the "Split" button first the topology is split according to the assigned labels (nodes with
hostedOn-predecessors with different labels are duplicated) and second the host nodes from the provider repositories
are matched.
For this, lower level nodes can be removed and replaced.

The split as well as the matched topology are persistently stored with an attached "-split" and "-matched" in
the Service Template Id.

![image](splitting.png)
