# Nodes on OASIS TOSCA 1.0

## "name" vs. "id" at entities

Some entities carry a name, some an id and some both.
A justification is available at [TOSCA issue 47](https://issues.oasis-open.org/browse/TOSCA-47).

## Possible attachments of artifacts

Implementation Artifacts (IAs) may be attached at

* NodeType/Interfaces/Interface/Operation (via IA's operation attribute)
* NodeType/Interfaces/Interface/Operation
* NodeTemplate

Deployment Artifacts (DAs) may be attached at
* NodeType
* NodeTemplate

## License

Copyright (c) 2013-2018 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
