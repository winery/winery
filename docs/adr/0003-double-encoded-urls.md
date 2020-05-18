# Double Encoded URLs

URLs should be human-readable, slashes are not allowed.
Encoded slahes are not enabled as default due to security reasons.

More information about encoding is available at [dev/Encoding.md](../dev/encoding.md).

## Considered Alternatives

* Using namespace prefixes in the URLs
* Single-encoded URLs and forcing the environment to be reconfigered
* Double-encoded URLs

## Decision Outcome

* *Chosen Alternative: Double-encoded URLs*

## Pros and Cons of the Alternatives

### Using namespace prefixes in the URLs

* `+` No encoding issues
* `-` Not globally unique: The URLs will change if the user reconfigures the namespace prefix

### Single-encoded URLs

* `+` Nice URLs
* `-` All hosting environments have to be configured accordingly. This can lead to security issues when running other applications in parallel.

### Double-encoded URLs

* `+` Nearly-nice URLs
* `+` Hosting-environments do not have to be reconfigured
* `-` Double-encoding might cause headaches during the implementation

## License

Copyright (c) 2017 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0

