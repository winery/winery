# Wrap properties in TOSCA properties element

When GETting/PUTting the properties of an entitty template, the content has to be serialized somehow.

## Considered Alternatives

* Wrap properties in TOSCA properties element
* Use nested XML element (`getAny()`)

## Decision Outcome

* Chosen Alternative: Wrap properties in TOSCA properties element
* Receiving an XML element is not possible with JAX-B/JAX-RS as that setting relies on strong typing.

## License

Copyright (c) 2017 University of Stuttgart.

All rights reserved. Made available under the terms of the [Eclipse Public License v2.0] and the [Apache License v2.0] which both accompany this distribution.

 [Apache License v2.0]: http://www.apache.org/licenses/LICENSE-2.0.html
 [Eclipse Public License v2.0]: http://www.eclipse.org/legal/epl-v20.html
