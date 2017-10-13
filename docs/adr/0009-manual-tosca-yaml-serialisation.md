# TOSCA YAML serialization deserialization using SnakeYAML

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

Copyright (c) 2017 University of Stuttgart.

All rights reserved. Made available under the terms of the [Eclipse Public License v2.0] and the [Apache License v2.0] which both accompany this distribution.

 [Apache License v2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
 [Eclipse Public License v2.0]: http://www.eclipse.org/legal/epl-v20.html
