# IPSec Algorithm Implementation

* Status: [accepted ]
* Deciders: Lukas Harzenetter
* Date: 2019-02-07



## Context and Problem Statement

In the problem detection and solving approach by Saatkamp et al., detected problems in a topology are solved by specific algorithms.
These algorithms must know some semantics in order to perform correctly.

Concretely: The IPSec algorithm must know some kind of abstract Virtual Machine (VM) Node Type, since it replaces unsecure VMs with secure VMs that open a secure connection on the IP level.

## Considered Options for VM Nodes

* VMs collected in a special namespace
* Abstract VM Node Type

## Decision Outcome for VM Nodes

Chosen option: "Abstract VM Node Type" since TOSCA allows inheritance and inheritance creates mor semantic meaning.

## Considered Option for Secure VMs

* Secure VMs collected in a special namespace
* Abstract Secure VM Node Type
* Annotate Secure Types with a Tag

## Decision Outome for Secure VMs

Chosen option: "Secure VMs collected in a special namspace" since they are special kinds of the "normal" VMs, they should inherit from them (and consequently from the abstract VM type mentioned above) to create a meaningful semantics.
However, instead of creating a special namespace, this should be changed to "Annotate Secure Types with a Tag" in near future.


## License

Copyright (c) 2019 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
