# Use hardcoded namespaces for threat modeling

## Context and Problem Statement

The threat modeling approach relies on pairs of threats and mitigations.
Each "threat" should be referenced by one particular "mitigation".

## Considered Options

* hardcoding the namespaces, default node types and properties.
* dynamic namespaces similar to "pattern namespaces" for threats and mitigations

## Decision Outcome

Chosen option: hardcoded namespaces, due to ease of implementation and static nature of the problem

### Positive Consequences
In the context of threat modeling multiple different types of threats/mitigations are not necesaary so a minimal base type that carries the required properties (reference) can be used and extended


## License

Copyright (c) 2018 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
