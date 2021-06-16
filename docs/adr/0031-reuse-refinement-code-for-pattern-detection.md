<!---~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  ~ Copyright (c) 2021 Contributors to the Eclipse Foundation
  ~
  ~ See the NOTICE file(s) distributed with this work for additional
  ~ information regarding copyright ownership.
  ~
  ~ This program and the accompanying materials are made available under the
  ~ terms of the Eclipse Public License 2.0 which is available at
  ~ http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
  ~ which is available at https://www.apache.org/licenses/LICENSE-2.0.
  ~
  ~ SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
  ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~-->


# Reuse the pattern refinement implementation for pattern detection

## Context and Problem Statement

To create an executable deployment model, the pattern refinement process replaces a matching subgraph with the Refinement Structure of a PRM.
To create a PbDCM, the pattern detection process replaces a matching subgraph with the Detector of a PRM.
The replacement procedure is identical for both processes, only the structures used for the replacement differ.
Therefore, the implementation of the pattern refinement process should be reused to implement the pattern detection process.

## Decision Drivers

* Avoid duplicate code
* Avoid introducing errors and inconsistencies during reimplementation

## Considered Options

* Swap the Detector of all PRMs with their Refinement Structures
* Reimplementation
* Use common interface

## Decision Outcome

Chosen option: "Swap the Detector of all PRMs with their Refinement Structures", because reimplementation introduces too much duplicate code and a common interface requires a lot of boilerplate code while also decreasing readability.

### Positive Consequences <!-- optional -->

* Complete pattern refinement implementation can be reused

### Negative consequences <!-- optional -->

* Readability and understandability decreases

## Pros and Cons of the Options

### Swap the Detector of all PRMs with their Refinement Structures

In the backend, the elements of the PRMs retrieved from the repository are swapped, .i.e, the Detector of each PRM is set to its Refinement Structure, its Refinement Structure is set to its Detector, and all mappings are adapted accordingly.

* Good, because complete refinement code can be reused
* Bad, because decreases readability and understandability

### Reimplementation

The complete pattern refinement code is reimplemented for pattern detection, i.e., the reimplemented code considers the Detector during the replacement, redirection of Relations using the Relation Mappings, and retaining elements using the Stay Mappings.

* Good, because better readability
* Bad, because results in a lot of duplicate code
* Bad, because the reimplemented code can contain errors and inconsistencies already fixed in the refinement implementation

### Use common interface

Implement an interface which returns the Refinement Structure of a PRM for the replacement procedure of the pattern refinement process and returns the Detector of a PRM during the pattern detection process.

* Good, because refinement code can be reused
* Bad, because requires a lot of boilerplate code
* Bad, because it decreases readability

## License

Copyright (c) 2021 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
