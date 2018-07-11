# Use filesystem as backend

Winery needs to store its contents.
These contents need to be shared.

## Considered Alternatives

* Filesystem
* Database

## Decision Outcome

* *Chosen Alternative: Filesystem*

## Pros and Cons of the Alternatives 

### Filesystem

* `+` Easy to manually change values
* `+` No need to educate students on a certain database system
* `+` Allows to use git as distributed version control system
* `-` Consistency check is hard to implement
* `-` Not transaction safe (concurrency)

### Database

* `+` Transaction safety
* `+` Scalability
* `-` Not (directly) possible to use git as distributed version control system
* `-` Higher skills required

## License

Copyright (c) 2017-2018 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0

