# Custom URI for lifecycle interface

Winery can generate a lifecycle interface.
That interface has to take a URI for a name

## Considered Alternatives

* `http://opentosca.org/interfaces/lifecycle`
* `http://www.example.com/interfaces/lifecycle` (from http://docs.oasis-open.org/tosca/tosca-primer/v1.0/tosca-primer-v1.0.html)
* `tosca.interfaces.node.lifecycle.Standard` (from http://docs.oasis-open.org/tosca/TOSCA-Simple-Profile-YAML/v1.1/TOSCA-Simple-Profile-YAML-v1.1.html)  

## Decision Outcome

* Chosen Alternative: `http://www.example.com/interfaces/lifecycle`
* Although the alternative is not standardized, it is the one consistent with the primer for TOSCA 1.0.


## License

Copyright (c) 2017 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
