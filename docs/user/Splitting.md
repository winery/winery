# Splitting and Matching

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

![splitting](graphics/splitting.png)

## Matching Functionality

Additional and based on the split function a match function is added to the winery.
A topology is inspected if open requirements are contained. A open requirement is a requirement for which no relationship 
to a node template exists which has the matching capability assigned.
In case open requirements are found, the provider repositories are searched for suitable matching candidates.
A matching candidate can be a single node, but also a whole topology fragment.
The matching can be done to new hosts, but also to dat resources, etc.
That means, based on the matching capabilities matching candidates are found and based on the requirements and capabilities
and their inheritance hierarchies the correct relationship types are determined.

To use this functionality a strict type and inheritance system is important.
These are the rules:
  - Each capability type with the semantic, that it acts as host/container has to be derived from the capability "Container"
  - Each capability type with the semantic, that it acts as a endpoint has to be derived from the capability "Endpoint"
  - The two default relationship types "hostedOn" and "connectsTo" should ne available and requires as valid target the 
    capability "Container" oder "Endpoint"

By using this function a vertical as well as horizontal matching is possible.

## License

Copyright (c) 2013-2017 University of Stuttgart.

All rights reserved. Made available under the terms of the [Eclipse Public License v1.0] and the [Apache License v2.0] which both accompany this distribution.

 [Apache License v2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
 [Eclipse Public License v1.0]: http://www.eclipse.org/legal/epl-v10.html
