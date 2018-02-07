# Eclipse Winery Workflow Modeler

This project is an implementation of a workflow modeler for creating and maintaining [TOSCA] compatible management plans.

It is based on the paper [BPMN4TOSCA: A Domain-Specific Language to Model Management Plans for Composite Applications].
Technical refinements and an inital implementation was provided by the thesis [Ein Modellierungswerkzeug für BPMN4TOSCA] by Thomas Michelbach.
The implementation was based on a different technology.
It was decided that Angular is more modern and thus, the Workflow Modeler was reimplemented using Angular.

## Installation

  Fetch dependencies using `npm install`

## Run

Execute `npm start`.
Open <http://localhost:9527/> with correct HTTP GET params:

- `repositoryURL` - e.g., http://dev.winery.opentosca.org/winery/
- `namespace` - e.g., http://www.example.org/servicetemplates
- `id` - e.g., PlanDemonstration
- `plan` - e.g., Test

Resulting URL would be: <http://localhost:9527/winery-workflowmodeler/?repositoryURL=http:%2F%2Fdev.winery.opentosca.org%2Fwinery%2F&namespace=http:%2F%2Fwww.example.org%2Fservicetemplates&id=PlanDemonstration&plan=Test>

You can also directly embed it into Winery. Note the [assumptions made by the winery](../org.eclipse.winery.repository.ui/README.md).


## License

Copyright (c) 2017 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0

 [Ein Modellierungswerkzeug für BPMN4TOSCA]: http://elib.uni-stuttgart.de/opus/volltexte/2015/9943/
 [TOSCA]: https://www.oasis-open.org/committees/tosca/
 [BPMN4TOSCA: A Domain-Specific Language to Model Management Plans for Composite Applications]: https://link.springer.com/chapter/10.1007/978-3-642-33155-8_4
