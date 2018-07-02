# Modify JAX-B generated classes

| Date | Status |
| -- | -- |
| 2018-02-05 | Accepted |

- OASIS provides an XSD file, where Java classes can be generated from.
- There are extension to TOSCA, which should made available easily.

## Considered Options

* Modify the generated classes
* Add a wrapper model based an a separate package with generated classes only.

## Decision Outcome

Chosen option: "Modfiy the generated classes", because
- The JAX-B code generation was not updated to fit modern Java 8 code style
- Allows modifications added easily
- TOSCA XSD is not likely to be updated quickly. And if it is updated, there is a huge update, where the whole model code has to be adapted.

## License

Copyright (c) 2017 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
