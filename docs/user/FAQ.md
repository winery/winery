# FAQ

## Q: What is TOSCA?

A: The Topology and Orchestration Specification for Cloud Applications (TOSCA) is an OASIS standard to describe the deployment and management of applications in a portable manner. 
Based on standard-compliant TOSCA runtimes, such as the OpenTOSCA ecosystem, the deployment and management can be automated. 
For more details see our [Notes on TOSCA](https://eclipse.github.io/winery/tosca/).

## Q: What is a CSAR?

A: Cloud Service Archive (CSAR) is a packaging format defined by the TOSCA specification, which enables to bundle modeled TOSCA components in a self-contained manner. 
Besides the TOSCA elements, the executable artifacts are packed as well.
In winery, you can model a service template and export it as a CSAR. 
This CSAR can be loaded into the OpenTOSCA container in order to deploy your application. 

## Q: How can I start the OpenTOSCA ecosystem?

A: You can start the ecosystem by simply using Docker Compose or by using installation scripts. 
Please refer to the [OpenTOSCA Getting Started guide](https://www.opentosca.org/sites/use_opentosca.html) for more details.

## Q: Is there an open repository for TOSCA types?

A: Yes! We provide a [github repository](https://github.com/OpenTOSCA/tosca-definitions-public) compatible to Winery, which contains several service templates, node types, etc. 
To use this repository with a Winery docker container, please refer to the corresponding [configuration instructions](https://github.com/OpenTOSCA/opentosca-docker#how-to-clone-a-private-tosca-definitions-repository-to-be-used-with-winery).

## Q: Where can I find a quick start guide to model node types?

A: You can find a Winery quick start guide about modeling node types in our [Quickstart](quickstart.md).

## Q: How can I export my modeled application as a CSAR?

A: Select the tab *Services Templates*.
From the listed service templates, select the one you want to export. 
In the detailed view, press *Export* and then choose the option *CSAR (XML)*.

## Q: My modeled node type became the suffix name *wip*? what does this mean?

A: This means your node type has a *work in progress* (wip) version. That is, this node type can and might be changed. 
Once you are done, you can do a release of your node type. In this way, Winery will not allow changes in the (released) node type anymore.

## Q: How can I release a node type?

A: Select the tab *Node Types*.
From the listed node types, select the one you want to release. 
In the detailed view, press *Versions* and then choose the option *Release management version*.

## Q: On Mac OS X, I can neither delete a node template nor a relationship template.

A: Select the node template (or the relationship template) and press <kbd>fn</kbd> + <kbd>backspace</kbd>.

## Q: Where can I get more help?

A: If you need support, contact us at *opentosca@iaas.uni-stuttgart.de*.

## Q: How can I contribute to Winery?

A: Please see the [contributing guide](https://github.com/eclipse/winery/blob/master/CONTRIBUTING.md).

## License

Copyright (c) 2018 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
