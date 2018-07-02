# Version Identifier in a Debian-like Form

The version identifier must be defined with its parts and meanings.

The version must support three parts:

1. The part of which specifies the version of the modeled component
2. A management version specifying the TOSCA version (`w`)
3. A work-in-progress (`wip`) version to clearly identify development steps

## Considered Options

* Debian like schema
* Fedora like schema
* OpenSUSE like schema

## Decision Outcome

Chosen Option: "Debian like schema", because it supports "pre-versions" such as the required `wip` version

### Positive Consequences

* `wip` version is always smaller than the actual release: `example_1.0-w2-wip4 < example_1.0-w2`

## License

Copyright (c) 2017 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
