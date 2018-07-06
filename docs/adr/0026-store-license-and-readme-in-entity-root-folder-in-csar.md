# Store `LICENSE` and `README.md` in respective entity's root folder in a CSAR

## Context and Problem Statement

`LICENSE` and `README.md` have to be stored in a standardized location when CSARs are exported.

## Decision Drivers

- Standardized CSAR structure

## Considered Options

* Store these files in a root folder of a respective entity
* Store these files in separate folders in a root folder of a respective entity

## Decision Outcome

Chosen option: "Store these files in a root folder of a respective entity", because

- Less visual clutter
- In repository, these files are not separated and stored together with the definition file

### Positive Consequences

- Standardized access to `LICENSE` and `README.md` files in any Winery-exported CSAR 

## Negative Consequences

- Puts additional restrictions on the CSAR's structure

## License

Copyright (c) 2018 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
