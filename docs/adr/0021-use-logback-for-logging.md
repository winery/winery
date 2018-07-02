# Use logback for logging

* Status: Accepted
* Date: 2018-06-04

## Context and Problem Statement

Each application should log.

## Considered Options

* [Logback](https://logback.qos.ch/)

## Decision Outcome

Chosen option: "Logback", because it
1) natively implements SLF4J and
2) SLF4J [offers bridging legacy APIs](https://www.slf4j.org/legacy.html) and thus allows to unify logging.

Positive Consequences:

* Single point for logging configuration
* Logging over SLF4J API

Negative consequences:

* Developers have to include following dependencies in their `pom.xml`, because the concrete logging framework should be chosen at the project executed by a user (or on a server).
  Tests still require logging and thus the concrete logging framework has to be included for tests.

    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>1.7.25</version>
        <scope>compile</scope>
    </dependency>
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.2.3</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>jcl-over-slf4j</artifactId>
        <version>1.7.25</version>
        <scope>test</scope>
    </dependency>

* [For easy logging, it is enough to include logback libraries at `WEB-INF/lib`](https://logback.qos.ch/manual/loggingSeparation.html#easy).
  Thus, following dependency has to be included at REST services:

    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.2.3</version>
        <scope>compile</scope>
    </dependency>

* Any logging wrapper has also has to be included at "user-facing" ends (CLI or WAR).
  Note that it cannot be included at libraries, because it [leads to an infinite loop](https://www.slf4j.org/legacy.html#jclRecursion).

    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>jcl-over-slf4j</artifactId>
        <version>1.7.25</version>
        <scope>compile</scope>
    </dependency>

* Legacy/old libraries depending on a certain logging framework have to be loaded with an exclusion of the logging framework.

* The documentation of Winery has to state that slf4j was chosen and that the logging has to be configured accordingly.

## License

Copyright (c) 2017 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
