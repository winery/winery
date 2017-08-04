# Eclipse Winery™ Workflow Modeler

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

You can also directly embed it into Winery.
Ensure that `winery.properties` contains following line.

```
bpmn4toscamodelerBaseURI=http://localhost:9527/
```

## License

Copyright (c) 2017 ZTE Corporation.

All rights reserved. This program and the accompanying materials
are made available under the terms of the [Eclipse Public License v1.0]
and the [Apache License v2.0] which both accompany this distribution,
and are available at http://www.eclipse.org/legal/epl-v10.html
and http://www.apache.org/licenses/LICENSE-2.0.

Please check the [EPL FAQ](https://eclipse.org/legal/eplfaq.php#DUALLIC) for implications.

 [Apache License v2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
 [Eclipse Public License v1.0]: http://www.eclipse.org/legal/epl-v10.html

## Literature

See http://www.opentosca.org/#publications for a list of publications in the OpenTOSCA ecosystem.

 [Apache License v2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
 [Eclipse Public License v1.0]: http://www.eclipse.org/legal/epl-v10.html
 [Ein Modellierungswerkzeug für BPMN4TOSCA]: http://elib.uni-stuttgart.de/opus/volltexte/2015/9943/
 [TOSCA]: https://www.oasis-open.org/committees/tosca/
 [BPMN4TOSCA: A Domain-Specific Language to Model Management Plans for Composite Applications]: https://link.springer.com/chapter/10.1007/978-3-642-33155-8_4
