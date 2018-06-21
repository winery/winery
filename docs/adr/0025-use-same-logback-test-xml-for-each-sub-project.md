# Use same `logback-test.xml` for each sub project

## Context and Problem Statement

When executing tests, logback is loaded as logging framework.
Logback in testing mode is configured using `logback-test.xml`.
In case logback is not configured, it does not output anything.

## Decision Drivers

- Ease of use for developers

## Considered Options

* Same `logback-test.xml` for each sub project
* Different, individually configured `logback-test.xml` files for each sub project

## Decision Outcome

Chosen option: "Use same `logback-test.xml` for each sub project", because

- During development, the "local" `logback-test.xml` file can be adjusted to the needs
- During continuous integration, the output should contain warnings and errors only and not any debug information

### Positive Consequences

- When modifying `logback-test.xml`, a developer just has to copy it over to the other sub projects without thinking which change to propagate to which sub project.

## Negative Consequences

- There are log level configurations for unused classes in some sub projects.
  For instance `com.sun.jersey` is configured in all projects, but is only used in the REST API project.

## License

Copyright (c) 2018 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
