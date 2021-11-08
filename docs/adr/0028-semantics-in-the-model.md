# Semantics in the Model

* Status: [accepted ] <!-- optional -->
* Deciders: Karoline Saatkamp, Lukas Harznetter, <!-- optional -->
* Date: 2018-02-07 <!-- optional -->

Technical Story: In order to solve solutions detected by [ToPS](https://github.com/OpenTOSCA/ToPS), we must have semantics.
These semantics must be known for the algorithms.

## Context and Problem Statement

In the problem detection and solving approach by Saatkamp et al., detected problems in a topology are solved by specific algorithms.
These algorithms must know some semantics in order to perform correctly.

Therefore, collection of predefined and known elements which the algorithms can work on is required.

## Considered Options

* Predefined elements in constants
* Predefined elements from configuration

## Decision Outcome

Chosen option: "Predefined elements in constants".
However, in near future, we could make this configurable by using the new configuration which is currently implemented by some students.

## License

Copyright (c) 2019 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
