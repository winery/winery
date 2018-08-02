# FAQ

## Q: What is TOSCA?

A: Topology and Orchestration Specification for Cloud Applications (TOSCA) is an OASIS specification, which enables the modeling, provisioning and management of cloud applications. 
For more detail, please click [here](http://eclipse.github.io/winery/tosca/).

## Q: What is a CSAR?

A: Cloud Service Archive (CSAR) is a packaging format defined by the TOSCA specification, which enables to bundle modeled TOSCA components in a self-contained manner. 
In winery, you can model a service template and export it as a CSAR. 
This CSAR can be loaded into the OpenTOSCA container in order to deploy your application. 

## Q: How can I start the OpenTOSCA ecosystem?

A: You can start the ecosystem by simply using Docker Compose or by using installation scripts. 
Please refer to the [OpenTOSCA Getting Started guide](http://www.opentosca.org/sites/use_opentosca.html) for more details.

## Q: Is there an open repository for TOSCA types?

A: Yes! We provide a [github repository](https://github.com/OpenTOSCA/tosca-definitions-public) compatible to Winery, which contains several service templates, node types, etc. 
To use this repository with a Winery docker container, please refer to the corresponding [configuration instructions](https://github.com/OpenTOSCA/opentosca-docker#how-to-clone-a-private-tosca-definitions-repository-to-be-used-with-winery).

## Q: Where can I find a quick start guide to model node types?

A: You can find a Winery quick start guide about modeling node types [here](quickstart.md).  

## Q: How can I export my modeled application as a CSAR?

A: Select the Tab *Services Templates*. 
From the listed service templates, select the one you want to export. 
In the detailed view, press *Export* and then choose the option *CSAR (XML)*.

## Q: My modeled node type became the suffix name *wip*? what does this mean?

A: This means your node type has a *work in progress* (wip) version. That is, this node type can and might be changed. 
Once you are done, you can do a release of your node type. In this way, Winery will not allow changes in the (released) node type anymore.

## Q: How can I release a node type?

A: Select the Tab *Node Types*. 
From the listed node types, select the one you want to release. 
In the detailed view, press *Versions* and then choose the option *Release management version*.

## Q: On Mac OS X, I can neither delete a node template nor a relationship template.

A: Select the node template (or the relationship template) and press <kbd>fn</kbd> + <kbd>backspace</kbd>.

## Q: Where can I get more help?

A: If you need support, contact us at *opentosca@iaas.uni-stuttgart.de*.

## Q: How can I contribute to Winery?

A: Please see the [contributing guide](https://github.com/eclipse/winery/blob/master/CONTRIBUTING.md).
