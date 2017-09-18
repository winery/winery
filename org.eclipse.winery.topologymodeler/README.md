# Winery Topoloy Modeler

This is the topology modeler component of Winery.

## Installation

See [README.md of Winery](../README.md).

## Implementation hints

* use `div.NodeTemplateShape` as selector for node templates

## About the Code

This code is in a prototypcial status. These are the main issues:
* Instead of using EL, it directly uses Java code in the JSPs.
* Instead of including the libraries using AMD, they are directly included via `script` tags.
* Instead of using [AngularJS] (or a similar framework), we manually do the life update from the properties section to the node
* Saving generates the XML manually (in index.jsp:save()). Therefore the extensibility of TOSCA is not supported here.
* We depend on both bootstrap and jquery UI as jsPlumb does not support a bootstrap binding.

### Trouble shooting
When the topology modeler does not fully load (i.e., a white background is still there), look at the TOMCAT log file (of the repository).
You'll see something like `TOSCA component id class org.eclipse.winery.common.ids.definitions.NodeTypeId / {http://www.example.org/tosca/nodetypes}VirtualMachine not found`.
This is in indicator that you manually edited a `.definitions` file and did not change the location in the repository.
The directory structure and the namespace and id/name settings in the `.definitions` file have to be in sync.
As quick solution, you can open the type, switch to the XML tab and press "Save".
Then, the namespace and id/name setting in the `.definitions` is changed according to the storage.

## License
Copyright (c) 2012-2014 University of Stuttgart.

All rights reserved. This program and the accompanying materials
are made available under the terms of the [Eclipse Public License v2.0]
and the [Apache License v2.0] which both accompany this distribution,
and are available at http://www.eclipse.org/legal/epl-v20.html
and http://www.apache.org/licenses/LICENSE-2.0

Contributors:
* Oliver Kopp - initial API and implementation

 [AngularJS]: http://angularjs.org/
 [Apache License v2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
 [Eclipse Public License v2.0]: http://www.eclipse.org/legal/epl-v20.html
