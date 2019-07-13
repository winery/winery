## Context and Problem Statement

The execution time of an online crawler of the DeployableComponents project can not be foreseen, because it depends on the accessed online service.
An architecture, which considers this problem, is needed.

## Considered Options

* Sequential execution of crawler and analyzer
* Asynchronous execution with defined execution time
* Asynchronous execution with defined number of dockerfiles
* Asynchronous round-based background execution

## Decision Outcome

Chosen option: "Asynchronous round-based background execution", because best performance with best fitting execution procedure to the expected use case.

### Positive Consequences <!-- optional -->

* good performance
* use case of hugh source data set is fulfilled

### Negative consequences <!-- optional -->

* more complex architecture

## Pros and Cons of the Options <!-- optional -->

### Sequential execution of crawler and analyzer

* Good, because simple architecture
* Bad, because bad performance (synchronous)

### Asynchronous execution with defined execution time

Asynchronous execution, which terminates after a predefined time.

* Good, because good performance
* Bad, because does not fulfill the expected use case of hugh amount of dockerfiles, which should be crawled (needs to be called several times)
* Bad, because can't not provide intermediate results

### Asynchronous execution with defined number of dockerfiles

Asynchronous execution, which terminates after a predefined number of dockerfiles is crawled.

* Good, because good performance
* Bad, because does not fulfill the expected use case of hugh amount of dockerfiles, which should be crawled (needs to be called several times). The actual number is irrelevant, it just needs to be hugh.

### Asynchronous round-based background execution

Asynchronous execution, which never terminates by it's own (must be terminated from outside). Provides intermediate results after every round. Round length (number of crawled dockerfiles) can be defined.

* Good, because good performance
* Good, because fulfills the expected use case of hugh amount of dockerfiles, which should be crawled. Round length can be high for best performance or lower for more frequent intermediate results.
* Bad, because complex architecture

## License

Copyright (c) 2019 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
