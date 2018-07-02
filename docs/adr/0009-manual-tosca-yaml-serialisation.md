# Manual serialization of SnakeYAML

The TOSCA YAML files have to be read into a Java model (deserialized) and written from the Java model into files (serialized).

## Considered Alternatives

* Manual serialization
* [SnakeYAML](https://bitbucket.org/asomov/snakeyaml)
* [jackson-dataformat-yaml](https://github.com/FasterXML/jackson-dataformat-yaml)

## Decision Outcome

* Chosen Alternative: *Manual serialization*
* SnakeYAML does not support annotations for serialization
* jackson-dataformat-yaml seems not to support annotations, such as [jackson-annotations](https://github.com/FasterXML/jackson-annotations)

## License

Copyright (c) 2017 Contributors to the Eclipse Foundation

See the NOTICE file(s) distributed with this work for additional
information regarding copyright ownership.

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0, or the Apache Software License 2.0
which is available at https://www.apache.org/licenses/LICENSE-2.0.

SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
