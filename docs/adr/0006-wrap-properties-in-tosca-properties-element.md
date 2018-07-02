# Wrap properties in TOSCA properties element

When GETting/PUTting the properties of an entitty template, the content has to be serialized somehow.

## Considered Alternatives

* Wrap properties in TOSCA properties element
* Use nested XML element (`getAny()`)

## Decision Outcome

* Chosen Alternative: Wrap properties in TOSCA properties element
* Receiving an XML element is not possible with JAX-B/JAX-RS as that setting relies on strong typing.

## License

Copyright (c) 2017 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0

